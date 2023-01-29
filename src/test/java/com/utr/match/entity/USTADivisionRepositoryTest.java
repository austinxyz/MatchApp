package com.utr.match.entity;

import com.utr.match.entity.USTADivision;
import com.utr.match.entity.USTADivisionRepository;
import com.utr.match.entity.USTALeague;
import com.utr.match.entity.USTALeagueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class USTADivisionRepositoryTest {


    @Autowired
    private USTADivisionRepository divisionRepository;

    @Autowired
    private USTALeagueRepository leagueRepository;

    @Test
    void createDivision() {
        Optional<USTALeague> league = leagueRepository.findById(3L);

        if(league.isPresent()) {

            USTADivision division = new USTADivision("2022 Adult 40 & Over Mens 4.0", "4.0", league.get());
            division.setAgeRange("40+");

            divisionRepository.save(division);

        }

    }


}