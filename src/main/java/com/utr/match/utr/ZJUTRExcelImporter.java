package com.utr.match.utr;

import com.utr.match.entity.*;
import com.utr.model.League;
import com.utr.model.Session;
import com.utr.model.UTRTeam;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Scope("singleton")
public class ZJUTRExcelImporter {

    @Autowired
    UTRTeamRepository teamRepository;

    @Autowired
    UTRTeamMemberRepository memberRepository;

    @Autowired
    DivisionRepository divisionRepository;

    public void updateTeamChineseName() {
        String fileLocation = "input/196830/2023 Zijing Cup Team Captain Registration.xlsx";
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("Silver Group");
            int rowIndex = 1;
            boolean notEmpty = true;

            while(notEmpty) {
                Row row = sheet.getRow(rowIndex);

                if (row == null || row.getCell(2) == null) {
                    notEmpty = false;
                    continue;
                }

                String chineseName = row.getCell(2).toString();

                if (chineseName == null || chineseName.trim().equals("")) {
                    notEmpty = false;
                    continue;
                }

                String teamName = row.getCell(3).toString();

                List<DivisionEntity> divs = divisionRepository.findByEvent_IdAndName(8L, teamName);

                if (divs.size()>0) {
                    DivisionEntity div = divs.get(0);
                    div.setChineseName(chineseName);
                    divisionRepository.save(div);
                }

                System.out.println(teamName + " is updated!");
                rowIndex++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void importUTR(boolean force) {

        String fileLocation = "input/196830/2023 Zijing Cup Team Captain Registration.xlsx";
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("Player UTR (Silver)");
            int rowIndex = 9;
            boolean notEmpty = true;

            UTRTeamEntity team = null;

            while(notEmpty) {
                Row row = sheet.getRow(rowIndex);

                if (row == null || row.getCell(0) == null) {
                    notEmpty = false;
                    continue;
                }

                String teamName = row.getCell(0).toString();

                if (teamName == null || teamName.trim().equals("")) {
                    notEmpty = false;
                    continue;
                }

                if (team ==null || !team.getName().equals(teamName)) {
                    List<UTRTeamEntity> teams = teamRepository.findByName(teamName);
                    if (teams.size() > 0) {
                        team = teams.get(0);
                    } else {
                        continue;
                    }
                }

                String lastName = row.getCell(1).toString();
                String firstName = row.getCell(2).toString();

                UTRTeamMember member = team.getMember(firstName, lastName);

                if (member != null) {
                    if (member.getMatchUTR()> 0.1 && !force) {
                        rowIndex++;
                        continue;
                    }

                    String matchUTR = row.getCell(5).toString();
                    if (!matchUTR.trim().equals("") && !matchUTR.equals(String.valueOf(member.getMatchUTR()))) {
                        member.setMatchUTR(Double.parseDouble(matchUTR));
                        memberRepository.save(member);
                        System.out.println(member.getName() + " match UTR:" + member.getMatchUTR() + " is saved");
                    }

                }
                rowIndex++;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}