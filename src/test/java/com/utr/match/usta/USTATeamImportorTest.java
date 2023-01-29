package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.entity.USTATeam;
import com.utr.match.entity.USTATeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    @Autowired
    USTATeamRepository teamRepository;


    final String teamURL = "https://www.ustanorcal.com/teaminfo.asp?id=92360";
    final String teamName = "VALLEY CHURCH 40AM3.5A";

    String divisionName = "2022 Adult 40 & Over Mens 3.5";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-lp&l=17728:2625&r=L";

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

        importor.updateTeamPlayerUTRID(teamName, divisionName);
    }

    @Test
    void updateTeamUTR() {
        USTATeam team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        importor.updateTeamUTRInfo(team);
    }

    @Test
    void updateTeamUSTAInfo() {
        USTATeam team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        importor.updatePlayerUSTANumber(team.getLink());
    }

    @Test
    void importUSTAFlight() {
        importor.importUSTAFlight(flightURL);
    }
    @Test
    void updateFlightUTRID() {
        List<USTATeam> teams = teamRepository.findByUstaFlight_Id(28L);

        for (USTATeam team: teams) {
            importor.updateTeamPlayersUTRID(team);
        }
    }

    @Test
    void updateFlightUTRInfo() {
        List<USTATeam> teams = teamRepository.findByUstaFlight_Id(28L);

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
        USTATeam team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
        importor.refreshTeamMatchesScores(team);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeam team = teamRepository.findByNameAndDivision_Name(teamName, divisionName);
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
        List<USTATeam> teams = teamRepository.findByUstaFlight_Id(2L);
        System.out.println("it takes " + (System.currentTimeMillis() - begin)/1000 + " second");
    }

    @Test
    void updateAllTeamsDR() {

        for (USTATeam team : teamRepository.findByDivision_IdOrderByUstaFlightAsc(5L)) {
            importor.updateTeamPlayersDR(team);
        }
    }
}