package com.utr.match.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTAFlightRepositoryTest {

    @Autowired
    private USTAFlightRepository flightRepository;

    @Autowired
    private USTADivisionRepository divisionRepository;

    @Test
    void createFlight() {
        Optional<USTADivision> div = divisionRepository.findById(23L);

        if(div.isPresent()) {
            USTADivision division = div.get();
            USTAFlight flight = new USTAFlight(1, division);
            flight.setArea("East Bay");
            flight.setDivision(division);
            flight.setLink("https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-eb&l=18480:2706&r=L");

            flightRepository.save(flight);

        }
    }
}