package rs.ilijaruzic.ats.models;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SimulationModel
{
    public enum State
    {
        IDLE,
        RUNNING,
        PAUSED
    }

    private final List<AirportModel> airports = new ArrayList<>();
    private final List<FlightModel> flights = new ArrayList<>();
    private final List<IObserveNotificationModel> observers = new ArrayList<>();
    private AirportModel selectedAirport = null;

    private LocalTime simulationTime = LocalTime.of(0, 0);
    private State simulationState = State.IDLE;
    private final List<AirplaneModel> activeAirplanes = new ArrayList<>();
    private final List<FlightModel> completedFlights = new ArrayList<>();

    public SimulationModel()
    {
    }

    public void addAirport(AirportModel airport)
    {
        if (airports.contains(airport))
        {
            throw new IllegalArgumentException("Airport with code '" + airport.getCode() + "' already exists.");
        }
        airports.add(airport);
        notifyObservers();
    }

    public void addFlight(FlightModel flight)
    {
        flights.add(flight);
        notifyObservers();
    }

    public List<AirportModel> getAirports()
    {
        return Collections.unmodifiableList(airports);
    }

    public List<FlightModel> getFlights()
    {
        return Collections.unmodifiableList(flights);
    }

    public AirportModel getSelectedAirport()
    {
        return selectedAirport;
    }

    public void setSelectedAirport(AirportModel selectedAirport)
    {
        this.selectedAirport = selectedAirport;
        notifyObservers();
    }

    public Optional<AirportModel> getAirportByCode(String code)
    {
        return airports.stream()
                .filter(a -> a.getCode().equals(code))
                .findFirst();
    }

    public LocalTime getSimulationTime()
    {
        return simulationTime;
    }

    public void setSimulationTime(LocalTime time)
    {
        this.simulationTime = time;
    }

    public State getSimulationState()
    {
        return simulationState;
    }

    public void setSimulationState(State state)
    {
        this.simulationState = state;
    }

    public List<AirplaneModel> getActiveAirplanes()
    {
        return Collections.unmodifiableList(activeAirplanes);
    }

    public void addActiveAirplane(AirplaneModel airplane)
    {
        activeAirplanes.add(airplane);
    }

    public void removeActiveAirplane(AirplaneModel airplane)
    {
        activeAirplanes.remove(airplane);
        completedFlights.add(airplane.getFlight());
    }

    public void resetSimulation()
    {
        simulationTime = LocalTime.of(0, 0);
        simulationState = State.IDLE;
        activeAirplanes.clear();
        completedFlights.clear();
        for (AirportModel airport : airports)
        {
            airport.getTakeoffQueue().clear();
            airport.setLastTakeoffTime(null);
        }
        notifyObservers();
    }

    public void loadData(List<AirportModel> newAirports, List<FlightModel> newFlights)
    {
        this.airports.clear();
        this.flights.clear();

        this.airports.addAll(newAirports);
        this.flights.addAll(newFlights);

        notifyObservers();
    }

    public void addObserver(IObserveNotificationModel observer)
    {
        observers.add(observer);
    }

    public void removeObserver(IObserveNotificationModel observer)
    {
        observers.remove(observer);
    }

    public void notifyObservers()
    {
        SwingUtilities.invokeLater(() ->
        {
            for (IObserveNotificationModel observer : observers)
            {
                observer.sendNotification();
            }
        });
    }
}