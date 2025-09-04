package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.*;
import rs.ilijaruzic.ats.utils.ValidationUtils;
import rs.ilijaruzic.ats.views.DataEntryView;
import rs.ilijaruzic.ats.views.utils.ErrorHandler;

import javax.swing.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public record DataController(SimulationModel model, DataEntryView view) implements IObserveNotificationModel
{
    public DataController(SimulationModel model, DataEntryView view)
    {
        this.model = model;
        this.view = view;
        this.model.addObserver(this);
        view.getAddAirportButton().addActionListener(e -> addAirport());
        view.getAddFlightButton().addActionListener(e -> addFlight());
        updateAirportComboBoxes();
        updateFlightPanelState();
    }

    private void addAirport()
    {
        String name = view.getAirportNameField().getText();
        String code = view.getAirportCodeField().getText();
        String xStr = view.getAirportXField().getText();
        String yStr = view.getAirportYField().getText();

        List<String> errors = ValidationUtils.Validate(AirportModel.class, name, code, xStr, yStr);
        if (model.getAirportByCode(code.toUpperCase()).isPresent())
        {
            errors.add("Airport with code '" + code.toUpperCase() + "' already exists.");
        }
        if (!errors.isEmpty())
        {
            ErrorHandler.showErrorMessages(view, errors);
            return;
        }

        try
        {
            AirportModel airport = new AirportModel(name, code, Double.parseDouble(xStr), Double.parseDouble(yStr));
            model.addAirport(airport);
            clearAirportInputFields();
            view.requestFocusInWindow();
        } catch (IllegalArgumentException ex)
        {
            ErrorHandler.showErrorMessage(view, ex.getMessage());
        }
    }

    private void addFlight()
    {
        String originCode = (String) view.getOriginAirportCombo().getSelectedItem();
        String destCode = (String) view.getDestinationAirportCombo().getSelectedItem();
        String timeStr = view.getDepartureTimeField().getText();
        String durationStr = view.getFlightDurationField().getText();

        List<String> errors = ValidationUtils.Validate(FlightModel.class, originCode, destCode, timeStr, durationStr);
        if (errors.isEmpty())
        {
            Optional<AirportModel> originOpt = model.getAirportByCode(originCode != null ? originCode.toUpperCase() : null);
            Optional<AirportModel> destOpt = model.getAirportByCode(destCode != null ? destCode.toUpperCase() : null);

            if (originOpt.isPresent() && destOpt.isPresent())
            {
                for (AirportModel otherAirport : model.getAirports())
                {
                    if (ValidationUtils.isAirportOnPath(originOpt.get(), destOpt.get(), otherAirport))
                    {
                        errors.add("Flight path collides with airport: " + otherAirport.getCode());
                    }
                }
            }
        }

        if (!errors.isEmpty())
        {
            ErrorHandler.showErrorMessages(view, errors);
            return;
        }

        try
        {
            Optional<AirportModel> originOpt = model.getAirportByCode(originCode);
            Optional<AirportModel> destOpt = model.getAirportByCode(destCode);
            FlightModel flight = new FlightModel(originOpt.get(), destOpt.get(), LocalTime.parse(timeStr), Integer.parseInt(durationStr));
            model.addFlight(flight);
            clearFlightInputFields();
            view.requestFocusInWindow();
        } catch (Exception ex)
        {
            ErrorHandler.showErrorMessage(view, ex.getMessage());
        }
    }

    private void updateAirportComboBoxes()
    {
        String selectedOrigin = (String) view.getOriginAirportCombo().getSelectedItem();
        String selectedDest = (String) view.getDestinationAirportCombo().getSelectedItem();
        JComboBox<String> originCombo = view.getOriginAirportCombo();
        JComboBox<String> destCombo = view.getDestinationAirportCombo();
        originCombo.removeAllItems();
        destCombo.removeAllItems();
        for (AirportModel airport : model.getAirports())
        {
            originCombo.addItem(airport.getCode());
            destCombo.addItem(airport.getCode());
        }
        originCombo.setSelectedItem(selectedOrigin);
        destCombo.setSelectedItem(selectedDest);
    }

    private void updateFlightPanelState()
    {
        boolean enable = !model.getAirports().isEmpty();

        view.getOriginAirportCombo().setEnabled(enable);
        view.getDestinationAirportCombo().setEnabled(enable);
        view.getDepartureTimeField().setEnabled(enable);
        view.getFlightDurationField().setEnabled(enable);
        view.getAddFlightButton().setEnabled(enable);
    }

    private void clearAirportInputFields()
    {
        view.getAirportNameField().setText("");
        view.getAirportCodeField().setText("");
        view.getAirportXField().setText("");
        view.getAirportYField().setText("");
    }

    private void clearFlightInputFields()
    {
        view.getDepartureTimeField().setText("");
        view.getFlightDurationField().setText("");
        view.getOriginAirportCombo().setSelectedIndex(-1);
        view.getDestinationAirportCombo().setSelectedIndex(-1);
    }

    @Override
    public void sendNotification()
    {
        updateAirportComboBoxes();
        updateFlightPanelState();
    }
}
