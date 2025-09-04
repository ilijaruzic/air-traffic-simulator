package rs.ilijaruzic.ats.views.tablemodels;

import rs.ilijaruzic.ats.models.FlightModel;
import rs.ilijaruzic.ats.models.IObserveNotificationModel;
import rs.ilijaruzic.ats.models.SimulationModel;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FlightTableModel extends AbstractTableModel implements IObserveNotificationModel
{

    private final String[] columnNames = {"Origin", "Destination", "Departure", "Duration (min)"};
    private final SimulationModel model;
    private List<FlightModel> flights;

    public FlightTableModel(SimulationModel model)
    {
        this.model = model;
        this.model.addObserver(this);
        this.flights = model.getFlights();
    }

    @Override
    public void sendNotification()
    {
        this.flights = model.getFlights();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        return flights.size();
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column)
    {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        FlightModel flight = flights.get(rowIndex);

        return switch (columnIndex)
        {
            case 0 -> flight.originAirport().getCode();
            case 1 -> flight.destinationAirport().getCode();
            case 2 -> flight.departureTime();
            case 3 -> flight.durationInMinutes();
            default -> null;
        };
    }
}
