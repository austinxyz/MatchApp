package com.utr.match.parser;

import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.parser.UTRParser;
import org.junit.jupiter.api.Test;

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
}