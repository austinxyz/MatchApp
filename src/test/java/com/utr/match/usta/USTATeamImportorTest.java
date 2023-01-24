package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.entity.USTATeam;
import com.utr.match.entity.USTATeamRepository;
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


    final String teamURL = "https://www.ustanorcal.com/TeamInfo.asp?id=96400";
    final String teamName = "SUNNYVALE TC/SUNNYVALE MTC 18MX7.0A";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-lp&l=17669:2605&r=L";

    final String scoreCardURL = "https://www.ustanorcal.com/scorecard.asp?id=753886&l=17624:2624";
    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void importUSTATeam() {
        importor.importUSTATeam(teamURL);
    }

    @Test
    void updateAllUTRId() {

        List<USTATeam> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(2L);

        for (USTATeam team: teams) {
            //System.out.println(team.getName());
            //importor.updateTeamPlayersUTRID(team);
            importor.updateTeamUTRInfo(team);
        }
    }


    @Test
    void updateUTRId() {
        importor.updateTeamPlayerUTRID(teamName);
    }

    @Test
    void updateTeamUTR() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.updateTeamUTRInfo(team);
    }

    @Test
    void importUSTAFlight() {
        importor.importUSTAFlight(flightURL);
    }
    @Test
    void updateFlightUTRID() {
        List<USTATeam> teams = teamRepository.findByUstaFlight_Id(23L);

        for (USTATeam team: teams) {
            importor.updateTeamPlayersUTRID(team);
        }
    }

    @Test
    void updateFlightUTRInfo() {
        List<USTATeam> teams = teamRepository.findByUstaFlight_Id(1L);

        for (USTATeam team: teams) {
            importor.updateTeamUTRInfo(team);
        }
    }
    @Test
    void importScoreCard() {
        importor.importScoreCard(scoreCardURL, 6L);
    }


    @Test
    void importTeamMatchs() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.refreshTeamMatchesScores(team);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.updateTeamPlayersDR(team);
    }

    @Test
    void updatePlayerUTRInfo() {
        PlayerEntity player = playerRepository.findByUtrId("750749");

        importor.updatePlayerUTRInfo(player, true);
    }
}