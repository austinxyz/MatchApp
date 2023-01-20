package com.utr.match.usta;

import com.utr.match.entity.USTATeam;
import com.utr.match.entity.USTATeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    @Autowired
    USTATeamRepository teamRepository;

    final String teamURL = "https://www.ustanorcal.com/TeamInfo.asp?id=96400";
    final String teamName = "SUNNYVALE MTC 40AM3.5C";
    final String flightURL = "https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-sb&l=17834:2624&r=L";

    final String scoreCardURL = "https://www.ustanorcal.com/scorecard.asp?id=753766&l=17647:2625";

    @Test
    void importUSTAFlight() {
        importor.importUSTAFlight(flightURL);
    }
    @Test
    void importUSTATeam() {
        importor.importUSTATeam(teamURL);
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
    void importScoreCard() {
        importor.importScoreCard(scoreCardURL, 4L);
    }


    @Test
    void importTeamMatchs() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.refreshTeamMatcheScores(team);
    }

    @Test
    void updateTeamPlayersDR() {
        USTATeam team = teamRepository.findByName(teamName);
        importor.updateTeamPlayersDR(team);
    }
}