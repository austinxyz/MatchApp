package com.utr.parser;

import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.model.Player;
import com.utr.model.PlayerResult;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

import java.util.List;

class UTRParserTest {

    @Test
    void parseEvent() {

        UTRParser parser = new UTRParser();
        Event event = parser.parseEvent("123233");

        System.out.println(event.getName());

        for (Division team : event.getDivisions()) {
            System.out.println(team);
        }

    }

    @Test
    void parsePlayerResult() {

        UTRParser parser = new UTRParser();
        PlayerResult result = parser.parsePlayerResult("1316122");

        System.out.println(result.getWinsNumber());
        System.out.println(result.getLossesNumber());
        System.out.println(result.getWithdrawsNumber());
    }

    @Test
    void searchPlayer() {

        UTRParser parser = new UTRParser();
        List<Player> results = parser.searchPlayers("yanzhao xu", 5);

        System.out.println(results);
    }
}