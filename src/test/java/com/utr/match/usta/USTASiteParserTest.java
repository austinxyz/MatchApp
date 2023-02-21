package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamEntity;
import com.utr.parser.UTRParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class USTASiteParserTest {

    @Test
    void parseUSTAFlight() {
        USTASiteParser util = new USTASiteParser();

        List<String> teams = null;

        try {
            teams = util.parseUSTAFlight("https://www.ustanorcal.com/standings.asp?a=usta-nc-nc-sb&l=16677:2567&r=L");
            System.out.println(teams.size());
            for(String teamURL: teams) {
                System.out.println(teamURL);
                util.parseUSTATeam(teamURL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    void parseUSTATeam() {
        USTASiteParser util = new USTASiteParser();
        UTRParser parser = new UTRParser();

        USTATeamEntity team = null;

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
            System.out.println(util.parseUSTANumber("https://www.ustanorcal.com/playermatches.asp?id=169410"));
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
            util.getTeamDynamicRating("https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=MITCHELL%20PK%2018MX7.0A&year=2023");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");
            for (PlayerEntity player: util.getTeamDynamicRating("https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=SUNNYVALE%20TC/SUNNYVALE%20MTC%2040AM3.5B&year=2023")) {
                System.out.println(player.getTennisRecordLink());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parseScoreCard() {

        USTASiteParser util = new USTASiteParser();

        try {
            System.out.println(util.parseScoreCard("https://www.ustanorcal.com/scorecard.asp?id=750078&l=17608:2606").toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    void parseTeamSchedule() {

        USTASiteParser util = new USTASiteParser();

        try {
            USTATeamEntity team = util.parseUSTATeam("https://www.ustanorcal.com/TeamInfo.asp?id=92360");
            System.out.println(team.getDivisionName());
            JSONArray matches = util.parseTeamMatches(team);

            for (int i=0; i< matches.length(); i++) {
                System.out.println(matches.get(i).toString());
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }
}