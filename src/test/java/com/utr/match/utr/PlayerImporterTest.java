package com.utr.match.utr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayerImporterTest {

    @Autowired
    PlayerImporter importer;

    @Test
    void importUTR() {
        importer.importUTR("2023 USTA 7.0 18.xlsx");
    }
}