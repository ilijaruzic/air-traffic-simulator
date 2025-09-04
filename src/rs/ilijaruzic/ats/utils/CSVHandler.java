package rs.ilijaruzic.ats.utils;


import rs.ilijaruzic.ats.exceptions.CSVFormatException;
import rs.ilijaruzic.ats.models.AirportModel;
import rs.ilijaruzic.ats.models.FlightModel;
import rs.ilijaruzic.ats.models.SimulationModel;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
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
        Map<String, AirportModel> airportsMap = new HashMap<>();
        List<FlightModel> flights = new ArrayList<>();
        List<String[]> rawFlightData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2)
                {
                    throw new CSVFormatException("Invalid format on line " + lineNumber + ": not enough columns.");
                }

                try
                {
                    String type = parts[0].trim();
                    if ("AIRPORT".equalsIgnoreCase(type))
                    {
                        if (parts.length != 5)
                        {
                            throw new CSVFormatException("Airport entry requires 5 columns on line " + lineNumber);
                        }
                        String name = parts[1].trim();
                        String code = parts[2].trim();
                        double x = Double.parseDouble(parts[3].trim());
                        double y = Double.parseDouble(parts[4].trim());
                        AirportModel airport = new AirportModel(name, code, x, y);
                        if (airportsMap.containsKey(airport.getCode()))
                        {
                            throw new CSVFormatException("Duplicate airport code '" + code + "' found on line " + lineNumber);
                        }
                        airportsMap.put(airport.getCode(), airport);
                    } else if ("FLIGHT".equalsIgnoreCase(type))
                    {
                        if (parts.length != 5)
                            throw new CSVFormatException("Flight entry requires 5 columns on line " + lineNumber);
                        rawFlightData.add(parts); // Чувамо сирове податке за каснију обраду
                    }
                } catch (NumberFormatException ex)
                {
                    throw new CSVFormatException("Invalid number format on line " + lineNumber + ".");
                } catch (IllegalArgumentException ex)
                {
                    throw new CSVFormatException("Validation error for data on line " + lineNumber + ": " + ex.getMessage());
                }
            }
        }

        for (String[] flightParts : rawFlightData)
        {
            String originCode = flightParts[1].trim();
            String destCode = flightParts[2].trim();
            LocalTime time = LocalTime.parse(flightParts[3].trim());
            int duration = Integer.parseInt(flightParts[4].trim());

            AirportModel origin = airportsMap.get(originCode);
            AirportModel dest = airportsMap.get(destCode);

            if (origin == null)
            {
                throw new CSVFormatException("Flight references unknown origin airport code: " + originCode);
            }
            if (dest == null)
            {
                throw new CSVFormatException("Flight references unknown destination airport code: " + destCode);
            }

            flights.add(new FlightModel(origin, dest, time, duration));
        }

        return new DataContainer(new ArrayList<>(airportsMap.values()), flights);
    }
}
