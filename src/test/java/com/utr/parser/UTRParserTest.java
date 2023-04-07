package com.utr.parser;

import com.utr.model.*;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

class UTRParserTest {

    @Test
    void parseEvent() {

        UTRParser parser = new UTRParser();
        Event event = parser.parseEvent("123233", false);

        System.out.println(event.getName());

        for (Division team : event.getDivisions()) {
            System.out.println(team);
        }

    }

    @Test
    void parsePlayerResult() {

        UTRParser parser = new UTRParser();
        PlayerResult result = parser.parsePlayerResult("1316122", false);

        System.out.println(result.getWinsNumber());
        System.out.println(result.getLossesNumber());
        System.out.println(result.getWithdrawsNumber());
    }

    @Test
    void searchPlayer() {

        UTRParser parser = new UTRParser();
        List<Player> results = parser.searchPlayers("Wang Shan", 5, false);

        System.out.println(results);
    }

    @Test
    void searchClubEvents() {

        UTRParser parser = new UTRParser();
        List<Event> results = parser.getClubEvents("3156", false);

        System.out.println(results);
    }

    @Test
    void getClub() {

        UTRParser parser = new UTRParser();
        Club club = parser.getClub("3156", false);

        System.out.println(club);
    }

    @Test
    void parsePlayer() {
        UTRParser parser = new UTRParser();
        Player player = parser.getPlayer("2547696", true);

        System.out.println(player);
    }

    @Test
    void TestDate() {

            LocalDate date = LocalDate.now();
            LocalDate fetchDate = date.minusDays(1);
            System.out.println(Duration.between(date.atTime(0, 0), fetchDate.atTime(0, 0)).toDays());
            System.out.println(ChronoUnit.DAYS.between(fetchDate, date));

    }
}