package rs.ilijaruzic.ats.utils;

import rs.ilijaruzic.ats.exceptions.CSVFormatException;
import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.FlightModel;
import rs.ilijaruzic.ats.models.SimulationModel;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CSVHandler
{
    private CSVHandler()
    {
    }

    public record DataContainer(List<AirportModel> airports, List<FlightModel> flights)
    {
    }

    public static void saveData(File file, SimulationModel model) throws IOException
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file)))
        {
            for (AirportModel airport : model.getAirports())
            {
                writer.println(String.join(",", "AIRPORT", airport.getName(), airport.getCode(), String.valueOf(airport.getX()), String.valueOf(airport.getY())));
            }
            for (FlightModel flight : model.getFlights())
            {
                writer.println(String.join(",", "FLIGHT", flight.getOriginAirport().getCode(), flight.getDestinationAirport().getCode(), flight.getDepartureTime().toString(), String.valueOf(flight.getDurationInMinutes())));
            }
        }
    }

    public static DataContainer loadData(File file) throws IOException, CSVFormatException
    {
        List<String> allErrors = new ArrayList<>();
        Map<String, AirportModel> airportsMap = new HashMap<>();
        Map<String[], Integer> rawFlightDataWithLines = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (line.isBlank() || line.trim().startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                Arrays.setAll(parts, i -> parts[i].trim());

                String type = parts.length > 0 ? parts[0] : "";

                if ("AIRPORT".equalsIgnoreCase(type))
                {
                    if (parts.length != 5)
                    {
                        allErrors.add("Line #" + lineNumber + ": Airport entry requires 5 columns.");
                        continue;
                    }
                    String name = parts[1];
                    String code = parts[2];
                    String xStr = parts[3];
                    String yStr = parts[4];

                    List<String> validationErrors = ValidationUtils.Validate(AirportModel.class, name, code, xStr, yStr);
                    if (!validationErrors.isEmpty())
                    {
                        for (String error : validationErrors)
                        {
                            allErrors.add("Line #" + lineNumber + ": " + error);
                        }
                    }
                    if (airportsMap.containsKey(code.toUpperCase()))
                    {
                        allErrors.add("Line #" + lineNumber + ": Duplicate airport code '" + code.toUpperCase() + "' found.");
                    }

                    if (validationErrors.isEmpty() && !airportsMap.containsKey(code.toUpperCase()))
                    {
                        AirportModel airport = new AirportModel(name, code, Double.parseDouble(xStr), Double.parseDouble(yStr));
                        airportsMap.put(airport.getCode(), airport);
                    }
                } else if ("FLIGHT".equalsIgnoreCase(type))
                {
                    if (parts.length != 5)
                    {
                        allErrors.add("Line #" + lineNumber + ": Flight entry requires 5 columns.");
                        continue;
                    }
                    rawFlightDataWithLines.put(parts, lineNumber);
                }
            }
        }

        for (Map.Entry<String[], Integer> entry : rawFlightDataWithLines.entrySet())
        {
            String[] flightParts = entry.getKey();
            int flightLineNumber = entry.getValue();

            String originCode = flightParts[1];
            String destCode = flightParts[2];
            String timeStr = flightParts[3];
            String durationStr = flightParts[4];

            List<String> validationErrors = ValidationUtils.Validate(FlightModel.class, originCode, destCode, timeStr, durationStr);
            if (!validationErrors.isEmpty())
            {
                for (String error : validationErrors)
                {
                    allErrors.add("Line #" + flightLineNumber + ": " + error);
                }
            }

            AirportModel origin = airportsMap.get(originCode.toUpperCase());
            AirportModel dest = airportsMap.get(destCode.toUpperCase());
            boolean airportsExist = true;

            if (origin == null)
            {
                allErrors.add("Line #" + flightLineNumber + ": Flight references unknown origin airport code: " + originCode);
                airportsExist = false;
            }
            if (dest == null)
            {
                allErrors.add("Line #" + flightLineNumber + ": Flight references unknown destination airport code: " + destCode);
                airportsExist = false;
            }

            if (validationErrors.isEmpty() && airportsExist)
            {
                for (AirportModel otherAirport : airportsMap.values())
                {
                    if (ValidationUtils.isAirportOnPath(origin, dest, otherAirport))
                    {
                        allErrors.add(String.format(
                                "Line #%d: Flight path from %s to %s collides with airport %s.",
                                flightLineNumber,
                                origin.getCode(),
                                dest.getCode(),
                                otherAirport.getCode()
                        ));
                    }
                }
            }
        }

        if (!allErrors.isEmpty())
        {
            String combinedErrorMessage = String.join("\n", allErrors);
            throw new CSVFormatException(combinedErrorMessage);
        }

        List<FlightModel> flights = new ArrayList<>();
        for (Map.Entry<String[], Integer> entry : rawFlightDataWithLines.entrySet())
        {
            String[] flightParts = entry.getKey();
            String originCode = flightParts[1].toUpperCase();
            String destCode = flightParts[2].toUpperCase();
            LocalTime time = LocalTime.parse(flightParts[3]);
            int duration = Integer.parseInt(flightParts[4]);

            AirportModel origin = airportsMap.get(originCode);
            AirportModel dest = airportsMap.get(destCode);
            flights.add(new FlightModel(origin, dest, time, duration));
        }

        return new DataContainer(new ArrayList<>(airportsMap.values()), flights);
    }
}
