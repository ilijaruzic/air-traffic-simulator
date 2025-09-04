package rs.ilijaruzic.ats.models;

import java.time.LocalTime;
import java.util.Objects;

public record FlightModel(AirportModel originAirport, AirportModel destinationAirport, LocalTime departureTime,
                          int durationInMinutes)
{

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
    public String toString()
    {
        return originAirport().getCode() + " -> " + destinationAirport().getCode() + " @ " + departureTime;
    }
}