package com.utr.match.usta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTATeamImportorTest {

    @Autowired
    USTATeamImportor importor;

    @Test
    void importUSTATeam() {
        importor.importUSTATeam("https://www.ustanorcal.com/Teaminfo.asp?id=96443");
    }

    @Test
    void updateUTRId() {
        importor.updateTeamPlayerUTRID("SUNNYVALE TC/SUNNYVALE MTC 40AM3.5B");
    }
}