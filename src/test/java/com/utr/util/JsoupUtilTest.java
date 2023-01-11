package com.utr.util;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeam;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsoupUtilTest {

    @Test
    void parseUSTATeam() {
        JsoupUtil util = new JsoupUtil();
        UTRParser parser = new UTRParser();

        try {
            USTATeam team = util.parseUSTATeam("https://www.ustanorcal.com/teaminfo.asp?id=96702");

            for (PlayerEntity player: team.getPlayers()) {
                List<Player> candidates = parser.searchPlayers(player.getName(), 5);

                if (candidates.size() == 1) {
                    System.out.println(player.getName() + " " + candidates.get(0).getId());
                } else {
                    System.out.println(candidates.size() == 0 ? "Can not find": "Multiple results");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}