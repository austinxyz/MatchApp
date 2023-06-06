package com.utr.match.utr;

import com.utr.model.League;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UTRExcelImporterTest {
    @Autowired
    UTRExcelImporter importer;

    @Autowired
    UTRService service;

    @Test
    void importUTR() {
        League league = null;
        league = service.getLeague("26");
        importer.importUTR(league, false);
    }
}