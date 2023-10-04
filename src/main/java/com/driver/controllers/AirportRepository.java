package com.driver.controllers;


import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class AirportRepository {

    HashMap<String, Airport> AirportDb = new HashMap<>();
    HashMap<Integer, Flight> FlightDb = new HashMap<>();
    HashMap<Integer, Passenger> PassengerDb = new HashMap<>();
    HashMap<Integer, List<Integer>> ticketDb = new HashMap<>();


    public void addAirport(Airport airport) {
        AirportDb.put(airport.getAirportName(), airport);
    }

    public String getLargestAirportName() {
        int MaxTerminals = Integer.MIN_VALUE;
        String LargestAirportName = null;

        for (Airport airport : AirportDb.values()) {
            if (airport.getNoOfTerminals() > MaxTerminals) {
                MaxTerminals = airport.getNoOfTerminals();
                LargestAirportName = airport.getAirportName();
            } else if (airport.getNoOfTerminals() == MaxTerminals) {
                if (airport.getAirportName().compareTo(LargestAirportName) < 0) {
                    LargestAirportName = airport.getAirportName();
                }
            }
        }
        return LargestAirportName;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double durationn = Double.MAX_VALUE;
        for (Flight flight : FlightDb.values()) {
            if (flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)) {
                if (flight.getDuration() < durationn) {
                    durationn = flight.getDuration();
                }
            }
        }
        if (durationn == Double.MAX_VALUE) {
            return -1;
        }
        return durationn;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int ans = 0;
        if (AirportDb.containsKey(airportName)) {
            City city = AirportDb.get(airportName).getCity();
            for (Integer flightId : ticketDb.keySet()) {
                Flight flight = FlightDb.get(flightId);
                if (flight.getFlightDate().equals(date) && (flight.getToCity().equals(city) || flight.getFromCity().equals(city))) {
                    ans += ticketDb.get(flightId).size();
                }
            }
        }
        return ans;
    }

    public int calculateFlightFare(Integer flightId) {
        int NoOfPeople = ticketDb.get(flightId).size();
        return 3000 + (NoOfPeople * 50);
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        if(ticketDb.containsKey(flightId)) {
            List<Integer> list = ticketDb.get(flightId);
            Flight flight = FlightDb.get(flightId);
            if (list.size() >= flight.getMaxCapacity()) {
                return "FAILURE";
            }
            if (list.contains(passengerId)) {
                return "FAILURE";
            }
            list.add(passengerId);
            ticketDb.put(flightId, list);
            return "SUCCESS";
        } else {
            List<Integer> list = new ArrayList<>();
            list.add(passengerId);
            ticketDb.put(flightId, list);
            return "SUCCESS";
        }
    }


    public String cancelATicket(Integer flightId, Integer passengerId) {
        if(ticketDb.containsKey(flightId)){
            boolean removed = false;
            List<Integer> list = ticketDb.get(flightId);
                if(list.contains(passengerId)){
                    list.remove(passengerId);
                    removed = true;
                }
                if(removed){
                    ticketDb.put(flightId, list);
                    return "SUCCESS";
                }
        }
        return "FAILURE";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int count = 0;
        for(List<Integer> list : ticketDb.values()){
            for(Integer i : list) {
                if (i == passengerId) {
                    count++;
                }
            }
        }
        return count;
    }

    public void addFlight(Flight flight) {
        FlightDb.put(flight.getFlightId(), flight);
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        for(Flight flight : FlightDb.values()){
            if(flight.getFlightId() == flightId) {
                City city = flight.getFromCity();
                for(Airport airport : AirportDb.values()){
                    if(airport.getCity().equals(city)){
                        return airport.getAirportName();
                    }
                }
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        if(ticketDb.containsKey(flightId)) {
            int count = ticketDb.get(flightId).size();
            int revenue = 0;
            for (int i = 0; i < count; i++) {
                revenue += 3000 + (i * 50);
            }
            return revenue;
        }
        return 0;
    }

    public void addPassenger(Passenger passenger) {
        PassengerDb.put(passenger.getPassengerId(), passenger);
    }
}