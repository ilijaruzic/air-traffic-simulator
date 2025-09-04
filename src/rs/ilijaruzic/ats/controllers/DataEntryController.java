package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.*;
import rs.ilijaruzic.ats.utils.ValidationUtils;
import rs.ilijaruzic.ats.views.DataEntryView;
import rs.ilijaruzic.ats.views.utils.ErrorHandler;

import javax.swing.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public record DataEntryController(SimulationModel model, DataEntryView view) implements IObserveNotificationModel
{
    public DataEntryController(SimulationModel model, DataEntryView view)
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
        String originAirportCode = (String) view.getOriginAirportCombo().getSelectedItem();
        String destinationAirportCode = (String) view.getDestinationAirportCombo().getSelectedItem();
        String departureTimeStr = view.getDepartureTimeField().getText();
        String durationInMinutesStr = view.getFlightDurationField().getText();

        List<String> errors = ValidationUtils.Validate(FlightModel.class, originAirportCode, destinationAirportCode, departureTimeStr, durationInMinutesStr);
        if (errors.isEmpty())
        {
            Optional<AirportModel> originAirportOption = model.getAirportByCode(originAirportCode != null ? originAirportCode.toUpperCase() : null);
            Optional<AirportModel> destinationAirportOption = model.getAirportByCode(destinationAirportCode != null ? destinationAirportCode.toUpperCase() : null);

            if (originAirportOption.isPresent() && destinationAirportOption.isPresent())
            {
                for (AirportModel otherAirport : model.getAirports())
                {
                    if (ValidationUtils.isAirportOnPath(originAirportOption.get(), destinationAirportOption.get(), otherAirport))
                    {
                        errors.add("Flight path collides with airport: " + otherAirport.getCode());
                    }
                }
            }
        } else
        {
            ErrorHandler.showErrorMessages(view, errors);
            return;
        }

        try
        {
            Optional<AirportModel> originAirportOption = model.getAirportByCode(originAirportCode);
            Optional<AirportModel> destinationAirportOption = model.getAirportByCode(destinationAirportCode);
            if (originAirportOption.isPresent() && destinationAirportOption.isPresent())
            {
                FlightModel flight = new FlightModel(originAirportOption.get(), destinationAirportOption.get(), LocalTime.parse(departureTimeStr), Integer.parseInt(durationInMinutesStr));
                model.addFlight(flight);
                clearFlightInputFields();
                view.requestFocusInWindow();
            }
        } catch (Exception ex)
        {
            ErrorHandler.showErrorMessage(view, ex.getMessage());
        }
    }

    private void updateAirportComboBoxes()
    {
        String selectedOriginAirport = (String) view.getOriginAirportCombo().getSelectedItem();
        String selectedDestinationAirport = (String) view.getDestinationAirportCombo().getSelectedItem();
        JComboBox<String> originAirportCombo = view.getOriginAirportCombo();
        JComboBox<String> destinationAirportCombo = view.getDestinationAirportCombo();
        originAirportCombo.removeAllItems();
        destinationAirportCombo.removeAllItems();
        for (AirportModel airport : model.getAirports())
        {
            originAirportCombo.addItem(airport.getCode());
            destinationAirportCombo.addItem(airport.getCode());
        }
        originAirportCombo.setSelectedItem(selectedOriginAirport);
        destinationAirportCombo.setSelectedItem(selectedDestinationAirport);
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
