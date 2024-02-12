package com.utr.match.usta;

import com.utr.match.entity.USTADivision;
import com.utr.match.entity.USTADivisionRepository;
import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class USTAMatchImportorTest {

    @Autowired
    private USTADivisionRepository divisionRepository;

    @Autowired
    USTAMatchImportor importor;

    @Autowired
    USTATeamRepository teamRepository;

    final String teamName = "RINCONADA PK 18MX6.0C";

    String divisionName = "2024 Mixed 18 & Over 6.0";

    @Test
    void importTeamMatchs() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTADivision division = divisionRepository.findByName(divisionName);
        importor.refreshMatchesScores(new USTATeam(team), division);
    }

}