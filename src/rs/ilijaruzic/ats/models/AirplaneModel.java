package rs.ilijaruzic.ats.models;

import java.time.LocalTime;

public class AirplaneModel
{
    private final FlightModel flight;
    private double currentX;
    private double currentY;
    private LocalTime actualTakeoffTime;

    public AirplaneModel(FlightModel flight)
    {
        this.flight = flight;
        this.currentX = flight.getOriginAirport().getX();
        this.currentY = flight.getOriginAirport().getY();
    }

    public FlightModel getFlight()
    {
        return flight;
    }

    public double getCurrentX()
    {
        return currentX;
    }

    public double getCurrentY()
    {
        return currentY;
    }

    public void setCurrentX(double currentX)
    {
        this.currentX = currentX;
    }

    public void setCurrentY(double currentY)
    {
        this.currentY = currentY;
    }

    public LocalTime getActualTakeoffTime()
    {
        return actualTakeoffTime;
    }

    public void setActualTakeoffTime(LocalTime actualTakeoffTime)
    {
        this.actualTakeoffTime = actualTakeoffTime;
    }
}