package com.utr.match.parser;

import com.utr.match.model.Event;
import com.utr.match.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UTRParserTest {

    @Test
    void parseEvent() {

        UTRParser parser = new UTRParser();
        Event event = parser.parseEvent("123233");

        System.out.println(event.getName());

        for (Team team: event.getTeams()) {
            System.out.println(team);
        }

    }
}