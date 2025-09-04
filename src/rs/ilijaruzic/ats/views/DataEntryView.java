package rs.ilijaruzic.ats.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import java.awt.*;

public class DataEntryView extends JPanel
{
    private JTextField airportNameField;
    private JTextField airportCodeField;
    private JTextField airportXField;
    private JTextField airportYField;
    private JButton addAirportButton;

    private JComboBox<String> originAirportCombo;
    private JComboBox<String> destinationAirportCombo;
    private JTextField departureTimeField;
    private JTextField flightDurationField;
    private JButton addFlightButton;

    private JTable airportsTable;
    private JTable flightsTable;

    public DataEntryView()
    {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel inputFormsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputFormsPanel.add(createAirportInputPanel());
        inputFormsPanel.add(createFlightInputPanel());

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        tablesPanel.add(createAirportsTablePanel());
        tablesPanel.add(createFlightsTablePanel());

        add(inputFormsPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
    }

    private JPanel createAirportInputPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Airport"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        airportNameField = new JTextField(15);
        panel.add(airportNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Code (3 letters):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        airportCodeField = new JTextField(15);
        panel.add(airportCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("X Coordinate:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        airportXField = new JTextField(15);
        panel.add(airportXField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Y Coordinate:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        airportYField = new JTextField(15);
        panel.add(airportYField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        addAirportButton = new JButton("Add Airport");
        panel.add(addAirportButton, gbc);

        return panel;
    }

    private JPanel createFlightInputPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Flight"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Постављамо заједничке параметре једном
        gbc.fill = GridBagConstraints.HORIZONTAL;

        originAirportCombo = new JComboBox<>();
        destinationAirportCombo = new JComboBox<>();
        departureTimeField = new JTextField(15);
        flightDurationField = new JTextField(15);
        addFlightButton = new JButton("Add Flight");

        int currentRow = 0;
        addFormRow(panel, gbc, "Origin:", originAirportCombo, currentRow++);
        addFormRow(panel, gbc, "Destination:", destinationAirportCombo, currentRow++);
        addFormRow(panel, gbc, "Departure (HH:mm):", departureTimeField, currentRow++);
        addFormRow(panel, gbc, "Duration (minutes):", flightDurationField, currentRow++);

        gbc.gridx = 1;
        gbc.gridy = currentRow;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        panel.add(addFlightButton, gbc);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int row)
    {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private JScrollPane createAirportsTablePanel()
    {
        airportsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(airportsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Airports"));
        return scrollPane;
    }

    private JScrollPane createFlightsTablePanel()
    {
        flightsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Flights"));
        return scrollPane;
    }

    public void setAirportTableModel(TableModel model)
    {
        this.airportsTable.setModel(model);
    }

    public void setFlightTableModel(TableModel model)
    {
        this.flightsTable.setModel(model);
    }

    public JTextField getAirportNameField()
    {
        return airportNameField;
    }

    public JTextField getAirportCodeField()
    {
        return airportCodeField;
    }

    public JTextField getAirportXField()
    {
        return airportXField;
    }

    public JTextField getAirportYField()
    {
        return airportYField;
    }

    public JButton getAddAirportButton()
    {
        return addAirportButton;
    }

    public JComboBox<String> getOriginAirportCombo()
    {
        return originAirportCombo;
    }

    public JComboBox<String> getDestinationAirportCombo()
    {
        return destinationAirportCombo;
    }

    public JTextField getDepartureTimeField()
    {
        return departureTimeField;
    }

    public JTextField getFlightDurationField()
    {
        return flightDurationField;
    }

    public JButton getAddFlightButton()
    {
        return addFlightButton;
    }
}