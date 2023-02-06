package com.utr.match.usta;

import com.utr.match.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    @Autowired
    USTATeamRepository teamRepository;


    final String teamURL = "https://www.ustanorcal.com/teaminfo.asp?id=92360";
    final String teamName = "SUNNYVALE MTC 40AM4.0B";

    String divisionName = "2023 Adult 40 & Over Mens 4.0";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-lp&l=17728:2625&r=L";

    final String scoreCardURL = "https://www.ustanorcal.com/scorecard.asp?id=753886&l=17624:2624";
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private USTADivisionRepository divisionRepository;

    @Test
    void importUSTATeam() {
        importor.importUSTATeam(teamURL);
    }

    @Test
    void updateAllUTRId() {

        List<USTATeamEntity> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(2L);

        for (USTATeamEntity team: teams) {
            //System.out.println(team.getName());
            //importor.updateTeamPlayersUTRID(team);
            importor.updateTeamUTRInfo(team);
        }
    }


    @Test
    void updateUTRId() {

        importor.updateTeamPlayerUTRID(teamName, divisionName);
    }

    @Test
    void updateTeamUTR() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        importor.updateTeamUTRInfo(team);
    }

    @Test
    void updateTeamUSTAInfo() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        importor.updatePlayerUSTANumber(team.getLink());
    }

    @Test
    void importUSTAFlight() {
        importor.importUSTAFlight(flightURL);
    }
    @Test
    void updateFlightUTRID() {
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(28L);

        for (USTATeamEntity team: teams) {
            importor.updateTeamPlayersUTRID(team);
        }
    }

    @Test
    void updateFlightUTRInfo() {
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(28L);

        for (USTATeamEntity team: teams) {
            importor.updateTeamUTRInfo(team);
        }
    }
    @Test
    void importScoreCard() {
        USTADivision division = divisionRepository.findByName(divisionName);
        importor.importScoreCard(scoreCardURL, 6L, division);
    }


    @Test
    void importTeamMatchs() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        USTADivision division = divisionRepository.findByName(divisionName);
        importor.refreshTeamMatchesScores(team, division);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeamEntity team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        importor.updateTeamPlayersDR(team);
    }

    @Test
    void updatePlayerUTRInfo() {
        PlayerEntity player = playerRepository.findByUtrId("257354");

        importor.updatePlayerUTRInfo(player, true);
    }

    @Test
    void findTeamsByFlight() {
        long begin = System.currentTimeMillis();
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(2L);
        System.out.println("it takes " + (System.currentTimeMillis() - begin)/1000 + " second");
    }

    @Test
    void updateAllTeamsDR() {

        for (USTATeamEntity team : teamRepository.findByDivision_IdOrderByUstaFlightAsc(5L)) {
            importor.updateTeamPlayersDR(team);
        }
    }
}