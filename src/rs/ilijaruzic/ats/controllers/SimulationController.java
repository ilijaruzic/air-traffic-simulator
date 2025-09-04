package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.*;
import rs.ilijaruzic.ats.views.ControlCenterView;
import rs.ilijaruzic.ats.views.MainView;
import rs.ilijaruzic.ats.views.MapView;

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SimulationController implements IObserveNotificationModel
{
    private static final int ANIMATION_TICK_MS = 200;
    private static final int LOGIC_TICK_MS = 100;
    private static final int SIM_MINUTES_PER_LOGIC_TICK = 1;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final SimulationModel model;
    private final ControlCenterView controlCenterView;
    private final MapView mapView;
    private final InactivityController inactivityController;

    private Thread thread;
    private Timer timer;
    private volatile boolean isRunning = false;


    public SimulationController(SimulationModel model, MainView mainView, InactivityController inactivityController)
    {
        this.model = model;
        this.controlCenterView = mainView.getControlCenterView();
        this.inactivityController = inactivityController;
        this.mapView = mainView.getMapView();
        this.model.addObserver(this);
        controlCenterView.getStartResumeButton().addActionListener(e -> startOrResume());
        controlCenterView.getPauseButton().addActionListener(e -> pause());
        controlCenterView.getResetButton().addActionListener(e -> reset());
        updateButtonStates();
    }

    private void startOrResume()
    {
        if (model.getSimulationState() == SimulationModel.State.IDLE)
        {
            start();
        } else if (model.getSimulationState() == SimulationModel.State.PAUSED)
        {
            resume();
        }
    }

    private void start()
    {
        inactivityController.pause();
        model.setSimulationState(SimulationModel.State.RUNNING);
        isRunning = true;

        thread = new Thread(this::run);
        thread.start();

        timer = new Timer(ANIMATION_TICK_MS, e -> tick());
        timer.start();

        updateButtonStates();
        mapView.requestFocusInWindow();
    }

    private void resume()
    {
        model.setSimulationState(SimulationModel.State.RUNNING);
        timer.start();
        synchronized (this)
        {
            notify();
        }
        updateButtonStates();
        mapView.requestFocusInWindow();
    }

    private void pause()
    {
        model.setSimulationState(SimulationModel.State.PAUSED);
        if (timer != null)
        {
            timer.stop();
        }
        updateButtonStates();
        mapView.requestFocusInWindow();
    }

    private void reset()
    {
        inactivityController.resume();
        isRunning = false;
        if (thread != null)
        {
            thread.interrupt();
        }
        if (timer != null)
        {
            timer.stop();
        }
        model.resetSimulation();
        mapView.requestFocusInWindow();
    }

    private void run()
    {
        while (isRunning)
        {
            try
            {
                if (model.getSimulationState() == SimulationModel.State.PAUSED)
                {
                    synchronized (this)
                    {
                        wait();
                    }
                }
                Thread.sleep(LOGIC_TICK_MS);

                LocalTime currentTime = model.getSimulationTime();
                LocalTime updatedTime = currentTime.plusMinutes(SIM_MINUTES_PER_LOGIC_TICK);
                model.setSimulationTime(updatedTime);

                enqueuePendingFlights(currentTime, updatedTime);
                launchQueuedFlights();
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                isRunning = false;
            }
        }
    }

    private void tick()
    {
        updateAirplanePositions();
        setTimeLabel();
        model.notifyObservers();
    }

    private void enqueuePendingFlights(LocalTime previousTime, LocalTime currentTime)
    {
        for (FlightModel flight : model.getFlights())
        {
            LocalTime departureTime = flight.departureTime();
            if (!departureTime.isBefore(previousTime) && departureTime.isBefore(currentTime))
            {
                AirportModel originAirport = flight.originAirport();
                if (!originAirport.getTakeoffQueue().contains(flight))
                {
                    originAirport.getTakeoffQueue().add(flight);
                }
            }
        }
    }

    private void launchQueuedFlights()
    {
        for (AirportModel airport : model.getAirports())
        {
            if (!airport.getTakeoffQueue().isEmpty())
            {
                LocalTime lastTakeoffTime = airport.getLastTakeoffTime();
                if (lastTakeoffTime == null || ChronoUnit.MINUTES.between(lastTakeoffTime, model.getSimulationTime()) >= 10)
                {
                    FlightModel flight = airport.getTakeoffQueue().poll();
                    if (flight != null)
                    {
                        AirplaneModel airplane = new AirplaneModel(flight);
                        airplane.setActualTakeoffTime(model.getSimulationTime());
                        model.addActiveAirplane(airplane);
                        airport.setLastTakeoffTime(model.getSimulationTime());
                    }
                }
            }
        }
    }

    private void updateAirplanePositions()
    {
        List<AirplaneModel> airplanes = new ArrayList<>();
        for (AirplaneModel airplane : model.getActiveAirplanes())
        {
            FlightModel flight = airplane.getFlight();
            long secondsSinceTakeoff = ChronoUnit.SECONDS.between(airplane.getActualTakeoffTime(), model.getSimulationTime());
            long durationInSeconds = flight.durationInMinutes() * 60L;
            if (secondsSinceTakeoff < 0)
            {
                secondsSinceTakeoff = 0;
            }
            if (secondsSinceTakeoff >= durationInSeconds)
            {
                airplanes.add(airplane);
                continue;
            }
            double progress = (double) secondsSinceTakeoff / durationInSeconds;
            double startX = flight.originAirport().getX();
            double startY = flight.originAirport().getY();
            double endX = flight.destinationAirport().getX();
            double endY = flight.destinationAirport().getY();
            airplane.setCurrentX(startX + (endX - startX) * progress);
            airplane.setCurrentY(startY + (endY - startY) * progress);
        }
        airplanes.forEach(model::removeActiveAirplane);
    }

    private void updateButtonStates()
    {
        SwingUtilities.invokeLater(() ->
        {
            switch (model.getSimulationState())
            {
                case IDLE:
                    controlCenterView.getStartResumeButton().setText("Start");
                    boolean canStart = model.getAirports().size() >= 2 && !model.getFlights().isEmpty();
                    controlCenterView.getStartResumeButton().setEnabled(canStart);
                    controlCenterView.getPauseButton().setEnabled(false);
                    controlCenterView.getResetButton().setEnabled(false);
                    break;
                case RUNNING:
                    controlCenterView.getStartResumeButton().setEnabled(false);
                    controlCenterView.getPauseButton().setEnabled(true);
                    controlCenterView.getResetButton().setEnabled(true);
                    break;
                case PAUSED:
                    controlCenterView.getStartResumeButton().setText("Resume");
                    controlCenterView.getStartResumeButton().setEnabled(true);
                    controlCenterView.getPauseButton().setEnabled(false);
                    controlCenterView.getResetButton().setEnabled(true);
                    break;
            }
        });
    }

    private void setTimeLabel()
    {
        controlCenterView.getTimeLabel().setText("Time: " + model.getSimulationTime().format(TIME_FORMATTER));
    }

    @Override
    public void sendNotification()
    {
        updateButtonStates();
    }
}
