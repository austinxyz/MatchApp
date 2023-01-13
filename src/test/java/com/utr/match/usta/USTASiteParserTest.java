package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeam;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class USTASiteParserTest {

    @Test
    void parseUSTATeam() {
        USTASiteParser util = new USTASiteParser();
        UTRParser parser = new UTRParser();

        USTATeam team = null;

        try {
            team = util.parseUSTATeam("https://www.ustanorcal.com/teaminfo.asp?id=96953");

            for (PlayerEntity player: team.getPlayers()) {
                System.out.println(player.getName() + "'s DR =  " + util.getDynamicRating(player.getTennisRecordLink()));
            }

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
}