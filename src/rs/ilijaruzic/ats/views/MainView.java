package rs.ilijaruzic.ats.views;

import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.views.utils.AppMenuBar;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame
{

    private AppMenuBar appMenuBar;
    private MapView mapView;
    private FilterView filterView;
    private DataEntryView dataEntryView;
    private ControlCenterView controlCenterView;

    public MainView(String title, SimulationModel model)
    {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1600, 900));
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        initialize(model);
    }

    private void initialize(SimulationModel model)
    {
        appMenuBar = new AppMenuBar();
        setJMenuBar(appMenuBar);

        mapView = new MapView(model);
        filterView = new FilterView(model);
        dataEntryView = new DataEntryView();
        controlCenterView = new ControlCenterView();
        add(controlCenterView, BorderLayout.PAGE_END);

        JSplitPane sideSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filterView, dataEntryView);
        sideSplitPane.setDividerLocation(150);
        sideSplitPane.setResizeWeight(0.3);
        sideSplitPane.setMinimumSize(new Dimension(380, 400));

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapView, sideSplitPane);
        mainSplitPane.setDividerLocation(1000);
        mainSplitPane.setResizeWeight(0.7);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    public AppMenuBar getAppMenuBar()
    {
        return appMenuBar;
    }

    public DataEntryView getDataEntryView()
    {
        return dataEntryView;
    }

    public MapView getMapView()
    {
        return mapView;
    }

    public FilterView getFilterView()
    {
        return filterView;
    }

    public ControlCenterView getControlCenterView()
    {
        return controlCenterView;
    }
}
