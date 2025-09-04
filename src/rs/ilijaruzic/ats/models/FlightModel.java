package rs.ilijaruzic.ats.models;

import java.time.LocalTime;
import java.util.Objects;

public class FlightModel
{
    private AirportModel originAirport;
    private AirportModel destinationAirport;
    private LocalTime departureTime;
    private int durationInMinutes;

    public FlightModel(AirportModel originAirport, AirportModel destinationAirport, LocalTime departureTime, int durationInMinutes)
    {
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.durationInMinutes = durationInMinutes;
    }

    public AirportModel getOriginAirport()
    {
        return originAirport;
    }

    public void setOriginAirport(AirportModel originAirport)
    {
        this.originAirport = originAirport;
    }

    public AirportModel getDestinationAirport()
    {
        return destinationAirport;
    }

    public void setDestinationAirport(AirportModel destinationAirport)
    {
        this.destinationAirport = destinationAirport;
    }

    public LocalTime getDepartureTime()
    {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime)
    {
        this.departureTime = departureTime;
    }

    public int getDurationInMinutes()
    {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes)
    {
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FlightModel flight)) return false;
        return durationInMinutes == flight.durationInMinutes &&
                Objects.equals(originAirport, flight.originAirport) &&
                Objects.equals(destinationAirport, flight.destinationAirport) &&
                Objects.equals(departureTime, flight.departureTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(originAirport, destinationAirport, departureTime, durationInMinutes);
    }

    @Override
    public String toString()
    {
        return getOriginAirport().getCode() + " -> " + getDestinationAirport().getCode() + " @ " + departureTime;
    }
}