package com.utr.match.utr;

import com.utr.match.entity.*;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.*;
import com.utr.parser.UTRParser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Scope("singleton")
public class UTRExcelImporter {

    @Autowired
    UTRTeamRepository teamRepository;

    @Autowired
    UTRTeamMemberRepository memberRepository;

    public void importUTR(League league, boolean force) {

        String fileLocation = "input/2023 HeCares Cup Team Registration.xlsx";
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("Player Match UTR");
            int rowIndex = 1;
            boolean notEmpty = true;
            UTRTeamEntity team = null;
            while(notEmpty) {
                Row row = sheet.getRow(rowIndex);

                String sessionName = row.getCell(0).toString();

                if (sessionName == null || sessionName.trim().equals("")) {
                    notEmpty = false;
                    continue;
                }

                Session session = league.getSession(sessionName);

                String teamName = row.getCell(1).toString();

                if (team == null || !team.getName().equals(teamName)) {
                    UTRTeam utrTeam = session.getTeamByName(teamName);
                    team = teamRepository.findByUtrTeamId(utrTeam.getId());
                }

                if (team == null) {
                    continue;
                }

                String firstName = row.getCell(2).toString().trim();
                String secondName = row.getCell(3).toString().trim();
                UTRTeamMember member = team.getMember(firstName, secondName);

                if (member != null) {
                    if (member.getMatchUTR()> 0.1 && !force) {
                        rowIndex++;
                        continue;
                    }

                    String matchUTR = row.getCell(14).toString();
                    if (!matchUTR.equals("TBD")) {
                        member.setMatchUTR(Double.parseDouble(matchUTR));
                        memberRepository.save(member);
                        System.out.println(member.getName() + " match UTR is saved");
                    }
                }
                rowIndex++;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}