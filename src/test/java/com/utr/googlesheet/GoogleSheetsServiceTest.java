package com.utr.googlesheet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GoogleSheetsServiceTest {

    @Autowired
    GoogleSheetsService service;

    @Test
    void getSpreadsheetValues() {
        try {
            service.getSpreadsheetValues();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}