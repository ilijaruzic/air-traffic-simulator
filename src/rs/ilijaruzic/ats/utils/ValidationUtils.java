package rs.ilijaruzic.ats.utils;


import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.FlightModel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class ValidationUtils
{
    private ValidationUtils()
    {
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalTime parseTime(String timeStr)
    {
        if (timeStr == null)
        {
            throw new IllegalArgumentException("Time string cannot be null.");
        }
        try
        {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e)
        {
            throw new IllegalArgumentException("Invalid time format. Please use HH:mm.");
        }
    }

    public static <T> List<String> Validate(Class<T> modelClass, String... args)
    {
        List<String> errors = new ArrayList<>();

        if (modelClass == AirportModel.class)
        {
            String name = args[0];
            String code = args[1];
            String xStr = args[2];
            String yStr = args[3];

            if (name == null || name.isBlank())
            {
                errors.add("Airport name cannot be null or blank.");
            }
            if (code == null || !code.matches("[A-Z]{3}"))
            {
                errors.add("Airport code must be exactly 3 uppercase letters.");
            }
            try
            {
                double x = Double.parseDouble(xStr);
                if (x < -90 || x > 90)
                {
                    errors.add("X coordinate must be between -90 and 90.");
                }
            } catch (NumberFormatException | NullPointerException e)
            {
                errors.add("X coordinate must be a valid number between -90 and 90.");
            }
            try
            {
                double y = Double.parseDouble(yStr);
                if (y < -90 || y > 90)
                {
                    errors.add("Y coordinate must be between -90 and 90.");
                }
            } catch (NumberFormatException | NullPointerException e)
            {
                errors.add("Y coordinate must be a valid number between -90 and 90.");
            }
        } else if (modelClass == FlightModel.class)
        {
            String originAirportCode = args[0];
            String destinationAirportCode = args[1];
            String departureTimeStr = args[2];
            String durationInMinutesStr = args[3];

            if (originAirportCode == null)
            {
                errors.add("Origin airport must be selected.");
            }
            if (destinationAirportCode == null)
            {
                errors.add("Destination airport must be selected.");
            }
            if (originAirportCode != null && originAirportCode.equals(destinationAirportCode))
            {
                errors.add("Destination airport cannot be the same as origin airport.");
            }
            try
            {
                LocalTime.parse(departureTimeStr);
            } catch (DateTimeParseException | NullPointerException e)
            {
                errors.add("Departure time must be in HH:mm format.");
            }
            try
            {
                int duration = Integer.parseInt(durationInMinutesStr);
                if (duration <= 0)
                {
                    errors.add("Duration in minutes must be greater than 0.");
                }
            } catch (NumberFormatException | NullPointerException e)
            {
                errors.add("Duration must be a valid integer greater than 0.");
            }
        }
        return errors;
    }

    public static boolean isAirportOnPath(AirportModel originAirport, AirportModel destinationAirport, AirportModel intermediateAirport)
    {
        if (intermediateAirport.equals(originAirport) || intermediateAirport.equals(destinationAirport))
        {
            return false;
        }

        double distanceOriginDestination = distance(originAirport.getX(), originAirport.getY(), destinationAirport.getX(), destinationAirport.getY());
        double distanceOriginIntermediate = distance(originAirport.getX(), originAirport.getY(), intermediateAirport.getX(), intermediateAirport.getY());
        double distanceIntermediateDestination = distance(intermediateAirport.getX(), intermediateAirport.getY(), destinationAirport.getX(), destinationAirport.getY());

        return Math.abs((distanceOriginIntermediate + distanceIntermediateDestination) - distanceOriginDestination) < 1e-6;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
