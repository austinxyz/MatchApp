package com.utr.match.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventRepositoryTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    DivisionRepository divisionRepository;

    @Test
    void createEvents() {
        EventEntity event = new EventEntity("2023 HeCares Cup Doubles League and Tournament", "UTR", "1111");
        eventRepository.save(event);
    }

    @Test
    void createDivision() {
        EventEntity event = eventRepository.findByEventId("1111");

        DivisionEntity division = new DivisionEntity("EVL", event);
        division.setEnglishName("Ever Victorious Land");
        division.setChineseName("常胜谷");
        divisionRepository.save(division);
    }


}