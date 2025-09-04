package rs.ilijaruzic.ats.views;

import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.IObserveNotificationModel;
import rs.ilijaruzic.ats.models.SimulationModel;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FilterView extends JPanel implements IObserveNotificationModel
{
    private final SimulationModel model;
    private final JPanel checkboxPanel;
    private final Map<String, JCheckBox> airportCheckboxes;

    public FilterView(SimulationModel model)
    {
        this.model = model;
        this.model.addObserver(this);
        this.airportCheckboxes = new HashMap<>();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Visible Airports"));

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        setAirportCheckboxes();
    }

    private void setAirportCheckboxes()
    {
        checkboxPanel.removeAll();
        airportCheckboxes.clear();

        for (AirportModel airport : model.getAirports())
        {
            JCheckBox checkbox = new JCheckBox(airport.toString(), airport.isVisible());
            airportCheckboxes.put(airport.getCode(), checkbox);
            checkboxPanel.add(checkbox);
        }

        revalidate();
        repaint();
    }

    public Map<String, JCheckBox> getAirportCheckboxes()
    {
        return Collections.unmodifiableMap(airportCheckboxes);
    }

    @Override
    public void sendNotification()
    {
        setAirportCheckboxes();
    }
}
