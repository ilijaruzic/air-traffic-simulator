package rs.ilijaruzic.ats;

import rs.ilijaruzic.ats.controllers.*;
import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.views.*;
import rs.ilijaruzic.ats.views.tablemodels.AirportTableModel;
import rs.ilijaruzic.ats.views.tablemodels.FlightTableModel;
import rs.ilijaruzic.ats.views.utils.ErrorHandler;

import javax.swing.*;

public class App
{
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex)
        {
            ErrorHandler.showErrorMessage(null, "Could not set the native look and feel. The application will use the default style.");
        }

        SwingUtilities.invokeLater(App::initialize);
    }

    private static void initialize()
    {
        SimulationModel model = new SimulationModel();

        MainView mainView = new MainView("Air Traffic Simulator", model);
        DataEntryView dataEntryView = mainView.getDataEntryView();

        AirportTableModel airportTableModel = new AirportTableModel(model);
        FlightTableModel flightTableModel = new FlightTableModel(model);

        dataEntryView.setAirportTableModel(airportTableModel);
        dataEntryView.setFlightTableModel(flightTableModel);

        InactivityController inactivityController = new InactivityController(mainView);
        new DataController(model, mainView.getDataEntryView());
        new FileController(model, mainView);
        new MapController(model, mainView.getMapView(), inactivityController);
        new FilterController(model, mainView.getFilterView());
        new SimulationController(model, mainView, inactivityController);

        mainView.setVisible(true);
        mainView.requestFocusInWindow();
    }
}