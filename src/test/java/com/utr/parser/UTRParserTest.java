package com.utr.parser;

import com.utr.model.*;
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
        List<Player> results = parser.searchPlayers("Wang Shan", 5);

        System.out.println(results);
    }

    @Test
    void searchClubEvents() {

        UTRParser parser = new UTRParser();
        List<Event> results = parser.getClubEvents("3156");

        System.out.println(results);
    }

    @Test
    void getClub() {

        UTRParser parser = new UTRParser();
        Club club = parser.getClub("3156");

        System.out.println(club);
    }

    @Test
    void parsePlayer() {
        UTRParser parser = new UTRParser();
        Player player = parser.parsePlayer("2547696");

        System.out.println(player);
    }
}