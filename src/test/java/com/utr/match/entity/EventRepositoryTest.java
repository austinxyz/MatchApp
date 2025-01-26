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
        //EventEntity event = new EventEntity("2023 Zijing Cup Alumni Tennis Team Championships Silver Group", "UTR", "2222");
        //EventEntity event = new EventEntity("2024 Forever Young Doubles", "UTR", "226892");
        EventEntity event = new EventEntity("2024 Zijing Cup Alumni Tennis Team Championships Gold Group", "UTR", "2024");
        eventRepository.save(event);
    }

    @Test
    void createDivision() {
        EventEntity event = eventRepository.findByEventId("2026");

        DivisionEntity division = new DivisionEntity("ZJU", event);
        division.setEnglishName("ZJU");
        division.setChineseName("浙大");
        divisionRepository.save(division);
    }


}