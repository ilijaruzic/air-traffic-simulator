package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.IObserveNotificationModel;
import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.views.FilterView;

import javax.swing.JCheckBox;

public class FilterController implements IObserveNotificationModel
{
    private final SimulationModel model;
    private final FilterView view;

    public FilterController(SimulationModel model, FilterView view)
    {
        this.model = model;
        this.view = view;
        this.model.addObserver(this);
    }

    private void wireUpCheckboxes()
    {
        for (JCheckBox checkBox : view.getAirportCheckboxes().values())
        {
            for (var listener : checkBox.getActionListeners())
            {
                checkBox.removeActionListener(listener);
            }
        }

        view.getAirportCheckboxes().forEach((code, checkBox) ->
        {
            checkBox.addActionListener(e ->
            {
                model.getAirportByCode(code).ifPresent(airport ->
                {
                    airport.setVisible(checkBox.isSelected());
                    model.notifyObservers();
                });
            });
        });
    }

    @Override
    public void sendNotification()
    {
        wireUpCheckboxes();
    }
}
