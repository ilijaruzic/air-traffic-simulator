package rs.ilijaruzic.ats.views.tablemodels;

import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.IObserveNotificationModel;
import rs.ilijaruzic.ats.models.SimulationModel;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AirportTableModel extends AbstractTableModel implements IObserveNotificationModel
{

    private final String[] columnNames = {"Name", "Code", "X", "Y"};
    private final SimulationModel model;
    private List<AirportModel> airports;

    public AirportTableModel(SimulationModel model)
    {
        this.model = model;
        this.model.addObserver(this);
        this.airports = model.getAirports();
    }

    @Override
    public void sendNotification()
    {
        this.airports = model.getAirports();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        return airports.size();
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
        AirportModel airport = airports.get(rowIndex);

        return switch (columnIndex)
        {
            case 0 -> airport.getName();
            case 1 -> airport.getCode();
            case 2 -> airport.getX();
            case 3 -> airport.getY();
            default -> null;
        };
    }
}
