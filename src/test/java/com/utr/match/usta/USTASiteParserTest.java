package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeam;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class USTASiteParserTest {

    @Test
    void parseUSTAFlight() {
        USTASiteParser util = new USTASiteParser();

        List<String> teams = null;

        try {
            teams = util.parseUSTAFlight("https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-sb&l=17840:2605&r=L");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    void parseUSTATeam() {
        USTASiteParser util = new USTASiteParser();
        UTRParser parser = new UTRParser();

        USTATeam team = null;

        try {
            team = util.parseUSTATeam("https://www.ustanorcal.com/Teaminfo.asp?id=97084");

/*            for (PlayerEntity player: team.getPlayers()) {
                System.out.println(player.getName() + "'s DR =  " + util.getDynamicRating(player.getTennisRecordLink()));
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void parseUSTANumber() {
        USTASiteParser util = new USTASiteParser();
        try {
            util.parseUSTANumber("https://www.ustanorcal.com/playermatches.asp?id=217977");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void getDynamicRating() {
        USTASiteParser util = new USTASiteParser();
        try {
            util.getDynamicRating("https://www.tennisrecord.com/adult/profile.aspx?playername=YANZHAO%20XU");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getTeamDynamicRating() {
        USTASiteParser util = new USTASiteParser();
        try {
            util.getTeamDynamicRating("https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=RINCONADA%20PK%2018AM3.5A&year=2022");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");
            util.getTeamDynamicRating("https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=RINCONADA%20PK%2018MX7.0B&year=2022");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parseScoreCard() {

        USTASiteParser util = new USTASiteParser();

        try {
            System.out.println(util.parseScoreCard("https://www.ustanorcal.com/scorecard.asp?id=749919&l=17840:2605").toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}