package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.views.utils.CoordinateConverter;
import rs.ilijaruzic.ats.views.MapView;

import javax.swing.Timer;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapController
{
    private final SimulationModel model;
    private final MapView view;
    private final InactivityController inactivityController;
    private final Timer timer;

    public MapController(SimulationModel model, MapView view, InactivityController inactivityController)
    {
        this.model = model;
        this.view = view;
        this.inactivityController = inactivityController;

        this.timer = new Timer(300, e ->
        {
            view.setBlinkState(!view.getBlinkState());
            view.repaint();
        });
        timer.setRepeats(true);

        view.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                view.requestFocusInWindow();
                handleMapClick(e.getPoint());
            }
        });
    }

    private void handleMapClick(Point point)
    {
        int currentWidth = view.getWidth();
        int currentHeight = view.getHeight();

        AirportModel clickedAirport = findAirportAtPoint(point, currentWidth, currentHeight);
        if (clickedAirport != null)
        {
            AirportModel currentlySelected = model.getSelectedAirport();
            if (clickedAirport.equals(currentlySelected))
            {
                model.setSelectedAirport(null);
                stopBlinking();
            } else
            {
                model.setSelectedAirport(clickedAirport);
                startBlinking();
            }
        }
    }

    private AirportModel findAirportAtPoint(Point point, int panelWidth, int panelHeight)
    {
        for (AirportModel airport : model.getAirports())
        {
            if (!airport.isVisible()) continue;

            Point airportScreenPosition = CoordinateConverter.geoToScreen(airport.getX(), airport.getY(), panelWidth, panelHeight);

            int halfSize = MapView.AIRPORT_SIZE / 2;
            if (Math.abs(point.x - airportScreenPosition.x) <= halfSize &&
                    Math.abs(point.y - airportScreenPosition.y) <= halfSize)
            {
                return airport;
            }
        }
        return null;
    }

    private void startBlinking()
    {
        inactivityController.pause();
        timer.start();
    }

    private void stopBlinking()
    {
        timer.stop();
        view.setBlinkState(false);
        view.repaint();
        inactivityController.resume();
    }
}