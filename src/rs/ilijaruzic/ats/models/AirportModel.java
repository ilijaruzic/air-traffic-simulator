package rs.ilijaruzic.ats.models;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class AirportModel
{
    private String name;
    private String code;
    private double x, y;
    private boolean isVisible = true;

    private final Queue<FlightModel> takeoffQueue = new LinkedList<>();
    private LocalTime lastTakeoffTime = null;

    public AirportModel(String name, String code, double x, double y)
    {
        this.name = name;
        this.code = code;
        this.x = x;
        this.y = y;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name.trim();
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public void setVisible(boolean visible)
    {
        this.isVisible = visible;
    }

    public Queue<FlightModel> getTakeoffQueue()
    {
        return takeoffQueue;
    }

    public LocalTime getLastTakeoffTime()
    {
        return lastTakeoffTime;
    }

    public void setLastTakeoffTime(LocalTime lastTakeoffTime)
    {
        this.lastTakeoffTime = lastTakeoffTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AirportModel airport)) return false;
        return Objects.equals(code, airport.code);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(code);
    }

    @Override
    public String toString()
    {
        return code + " (" + name + ")";
    }
}
