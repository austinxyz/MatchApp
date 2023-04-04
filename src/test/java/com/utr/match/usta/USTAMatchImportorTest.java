package com.utr.match.usta;

import com.utr.match.entity.USTADivision;
import com.utr.match.entity.USTADivisionRepository;
import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTAMatchImportorTest {

    @Autowired
    private USTADivisionRepository divisionRepository;

    @Autowired
    USTAMatchImportor importor;

    @Autowired
    USTATeamRepository teamRepository;

    final String teamName = "VALLEY CHURCH 40AM3.5A";

    String divisionName = "2023 Adult 40 & Over Mens 3.5";

    @Test
    void importTeamMatchs() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTADivision division = divisionRepository.findByName(divisionName);
        importor.refreshMatchesScores(new NewUSTATeam(team), division);
    }

}