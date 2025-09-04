package rs.ilijaruzic.ats.views;

import rs.ilijaruzic.ats.models.AirplaneModel;
import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.IObserveNotificationModel;
import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.views.utils.CoordinateConverter;

import javax.swing.*;
import java.awt.*;

public class MapView extends JPanel implements IObserveNotificationModel
{
    public static final int AIRPORT_SIZE = 10;
    public static final int AIRPLANE_SIZE = 8;
    private final SimulationModel model;
    private boolean blinkState = false;

    public MapView(SimulationModel model)
    {
        this.model = model;
        this.model.addObserver(this);
        setBackground(Color.decode("#a2d2ff"));
    }

    public boolean getBlinkState()
    {
        return blinkState;
    }

    public void setBlinkState(boolean state)
    {
        this.blinkState = state;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (AirportModel airport : model.getAirports())
        {
            if (!airport.isVisible()) continue;
            Point point = CoordinateConverter.geoToScreen(airport.getX(), airport.getY(), getWidth(), getHeight());
            if (airport.equals(model.getSelectedAirport()))
            {
                g2d.setColor(blinkState ? Color.RED : Color.GRAY);
            } else
            {
                g2d.setColor(Color.GRAY);
            }
            g2d.fillRect(point.x - AIRPORT_SIZE / 2, point.y - AIRPORT_SIZE / 2, AIRPORT_SIZE, AIRPORT_SIZE);
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(airport.getCode(), point.x + AIRPORT_SIZE / 2 + 5, point.y - fm.getHeight() / 2 + fm.getAscent());
        }

        g2d.setColor(Color.BLUE);

        for (AirplaneModel airplane : model.getActiveAirplanes())
        {
            Point point = CoordinateConverter.geoToScreen(airplane.getCurrentX(), airplane.getCurrentY(), getWidth(), getHeight());
            g2d.fillOval(point.x - AIRPLANE_SIZE / 2, point.y - AIRPLANE_SIZE / 2, AIRPLANE_SIZE, AIRPLANE_SIZE);
        }
    }

    @Override
    public void sendNotification()
    {
        repaint();
    }
}
