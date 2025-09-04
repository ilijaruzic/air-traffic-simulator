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
                writer.println(String.join(",", "FLIGHT", flight.originAirport().getCode(), flight.destinationAirport().getCode(), flight.departureTime().toString(), String.valueOf(flight.durationInMinutes())));
            }
        }
    }

    public static DataContainer loadData(File file) throws IOException, CSVFormatException
    {
        List<String> errors = new ArrayList<>();
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
                        errors.add("Line #" + lineNumber + ": Airport entry requires 5 columns.");
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
                            errors.add("Line #" + lineNumber + ": " + error);
                        }
                    }
                    if (airportsMap.containsKey(code.toUpperCase()))
                    {
                        errors.add("Line #" + lineNumber + ": Duplicate airport code '" + code.toUpperCase() + "' found.");
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
                        errors.add("Line #" + lineNumber + ": Flight entry requires 5 columns.");
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

            String originAirportCode = flightParts[1];
            String destinationAirportCode = flightParts[2];
            String departureTimeStr = flightParts[3];
            String durationInMinutesStr = flightParts[4];

            List<String> validationErrors = ValidationUtils.Validate(FlightModel.class, originAirportCode, destinationAirportCode, departureTimeStr, durationInMinutesStr);
            if (!validationErrors.isEmpty())
            {
                for (String error : validationErrors)
                {
                    errors.add("Line #" + flightLineNumber + ": " + error);
                }
            }

            AirportModel originAirport = airportsMap.get(originAirportCode.toUpperCase());
            AirportModel destinationAirport = airportsMap.get(destinationAirportCode.toUpperCase());
            boolean airportsExist = true;

            if (originAirport == null)
            {
                errors.add("Line #" + flightLineNumber + ": Flight references unknown origin airport code: " + originAirportCode);
                airportsExist = false;
            }
            if (destinationAirport == null)
            {
                errors.add("Line #" + flightLineNumber + ": Flight references unknown destination airport code: " + destinationAirportCode);
                airportsExist = false;
            }

            if (validationErrors.isEmpty() && airportsExist)
            {
                for (AirportModel airport : airportsMap.values())
                {
                    if (ValidationUtils.isAirportOnPath(originAirport, destinationAirport, airport))
                    {
                        errors.add(String.format(
                                "Line #%d: Flight path from %s to %s collides with airport %s.",
                                flightLineNumber,
                                originAirport.getCode(),
                                destinationAirport.getCode(),
                                airport.getCode()
                        ));
                    }
                }
            }
        }

        if (!errors.isEmpty())
        {
            throw new CSVFormatException(String.join("\n", errors));
        }

        List<FlightModel> flights = new ArrayList<>();
        for (Map.Entry<String[], Integer> entry : rawFlightDataWithLines.entrySet())
        {
            String[] flightParts = entry.getKey();
            String originAirportCode = flightParts[1].toUpperCase();
            String destinationAirportCode = flightParts[2].toUpperCase();
            LocalTime departureTime = LocalTime.parse(flightParts[3]);
            int durationInMinutes = Integer.parseInt(flightParts[4]);

            AirportModel originAirport = airportsMap.get(originAirportCode);
            AirportModel destinationAirport = airportsMap.get(destinationAirportCode);
            flights.add(new FlightModel(originAirport, destinationAirport, departureTime, durationInMinutes));
        }

        return new DataContainer(new ArrayList<>(airportsMap.values()), flights);
    }
}
