package com.utr.match.utr;

import com.utr.match.entity.*;
import com.utr.model.League;
import com.utr.model.Player;
import com.utr.model.Session;
import com.utr.model.UTRTeam;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;

@Service
@Scope("singleton")
public class PlayerImporter {

    @Autowired
    UTRParser parser;

    public void importUTR(String fileName) {

        String fileLocation = "input/" + fileName;
        FileInputStream file = null;

        try {
            file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet("Sheet1");
            int rowIndex = 1;
            boolean notEmpty = true;

            while(notEmpty) {
                Row row = sheet.getRow(rowIndex);

                if (row==null || row.getCell(0) == null) {
                    notEmpty = false;
                    continue;
                }

                String name = row.getCell(0).toString();

                if (row.getCell(1) == null) {
                    rowIndex++;
                    continue;
                }


                String area = row.getCell(2).toString().trim();
                String gender = row.getCell(1).toString().trim();

                if (name == null || name.trim().equals("")) {
                    notEmpty = false;
                    continue;
                }

                String utrID = findUTRID(name, gender, area);

                if (!utrID.equals("")) {
                    System.out.println("Find " + name + "UTRID " + utrID);
                    Player player = parser.getPlayer(utrID, true);
                    row.createCell(4).setCellValue(utrID);
                    row.createCell(5).setCellValue(player.getdUTR());
                    row.createCell(6).setCellValue(player.getdUTRStatus());
                } else {
                    System.out.println("Can not find " + name + "'s UTRID ");
                }

                Thread.sleep(5000);
                rowIndex++;

            }

            FileOutputStream outputStream = new FileOutputStream(new File("out/" + fileName));
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private String findUTRID(String name, String area, String gender) {

        List<Player> players = parser.searchPlayers(name, 10, true);

        if (players.size() == 1) {
            return players.get(0).getId();
        }

        List<String> candidateUTRIds = new ArrayList<>();
        for (Player utrPlayer : players) {
            if (!utrPlayer.getGender().equals(gender)) {
                continue;
            }

            if (utrPlayer.getUTR() < 0.1) {
                continue;
            }

/*            if (utrPlayer.getLocation() == null || !utrPlayer.getLocation().startsWith(area)) {
                continue;
            }*/

            candidateUTRIds.add(utrPlayer.getId());
        }

        return candidateUTRIds.size() > 0 ? candidateUTRIds.get(0) : "";
    }
}