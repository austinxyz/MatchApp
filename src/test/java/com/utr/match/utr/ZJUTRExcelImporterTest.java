package com.utr.match.utr;

import com.utr.model.League;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ZJUTRExcelImporterTest {

    @Autowired
    ZJUTRExcelImporter importer;


    @Test
    void importUTR() {

        importer.importUTR( true);
    }

    @Test
    void updateTeamChineseName() {
        importer.updateTeamChineseName();
    }
}