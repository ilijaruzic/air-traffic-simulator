package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.IObserveNotificationModel;
import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.views.FilterView;

import javax.swing.JCheckBox;

public record FilterController(SimulationModel model, FilterView view) implements IObserveNotificationModel
{
    public FilterController(SimulationModel model, FilterView view)
    {
        this.model = model;
        this.model.addObserver(this);
        this.view = view;
    }

    @Override
    public void sendNotification()
    {
        for (JCheckBox checkbox : view.getAirportCheckboxes().values())
        {
            for (var listener : checkbox.getActionListeners())
            {
                checkbox.removeActionListener(listener);
            }
        }

        view.getAirportCheckboxes().forEach((code, checkbox) ->
        {
            checkbox.addActionListener(e ->
            {
                model.getAirportByCode(code).ifPresent(airport ->
                {
                    airport.setVisible(checkbox.isSelected());
                    model.notifyObservers();
                });
            });
        });
    }
}
