package com.utr.match.utr;

import com.utr.match.entity.UTRTeamCandidate;
import com.utr.match.entity.UTRTeamEntity;
import com.utr.match.entity.UTRTeamMember;
import com.utr.match.model.Team;
import com.utr.model.Player;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class UTRDivisionPlayerExcelExport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // define excel file name to be exported
        response.addHeader("Content-Disposition", "attachment;fileName=division.xlsx");

        // read data provided by controller
        @SuppressWarnings("unchecked")
        UTRTeamEntity team = (UTRTeamEntity) model.get("team");

        CreationHelper helper = workbook.getCreationHelper();

        // create one sheet
        Sheet sheet = workbook.createSheet("Team" );

        CellStyle hylinkStyle = workbook.createCellStyle();
        Font hylinkFont = workbook.createFont();
        hylinkFont.setUnderline(Font.U_SINGLE);
        hylinkFont.setColor(IndexedColors.BLUE.index);
        hylinkStyle.setFont(hylinkFont);

        int rowNum = createDivisionPlayerInfo(team, sheet, helper, hylinkStyle);


    }

    private int createDivisionPlayerInfo(UTRTeamEntity team, Sheet sheet, CreationHelper helper, CellStyle hylinkSytle) {

        int rowNum = 0;

        Row teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Team Name");
        teamRow.createCell(1).setCellValue(team.getName());
        teamRow.createCell(2).setCellValue(team.getName());

        // create row0 as a header
        Row row0 = sheet.createRow(rowNum++);
        row0.createCell(0).setCellValue("#");
        row0.createCell(1).setCellValue("Last Name");
        row0.createCell(2).setCellValue("First Name");
        row0.createCell(3).setCellValue("UTR");
        row0.createCell(4).setCellValue("UTR WPct");
        row0.createCell(5).setCellValue("Double UTR");
        row0.createCell(6).setCellValue("Rating");
        row0.createCell(7).setCellValue("Match UTR");

/*        int index=8;
        for (String key: team.getLines().keySet()) {
            row0.createCell(index).setCellValue(key);
            index++;
        }*/

        int index = 1;
        // create row1 onwards from List<T>
        for(UTRTeamMember member: team.getPlayers()) {

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(index++);
            Cell lastName = row.createCell(1);
            lastName.setCellValue(member.getLastName());
            Cell firstName = row.createCell(2);
            firstName.setCellValue(member.getFirstName());
            Cell utr = row.createCell(3);
            utr.setCellValue(member.getDUTR() + "D(" + member.getDUTRStatus() + ")/" + member.getSUTR()+"S("+member.getSUTRStatus()+")");
            if (member.getUtrId()!=null && !member.getUtrId().trim().equals("")) {
                Hyperlink link = helper.createHyperlink(HyperlinkType.URL);
                link.setAddress("https://app.universaltennis.com/profiles/" + member.getUtrId());
                utr.setHyperlink(link);
                utr.setCellStyle(hylinkSytle);
            }

            row.createCell(4).setCellValue(String.format("%.2f", member.getSuccessRate()*100) + "%/" + String.format("%.2f",member.getWholeSuccessRate()*100)+"%");
            row.createCell(5).setCellValue(member.getDUTR());
            row.createCell(6).setCellValue(member.getRating());
            row.createCell(7).setCellValue(member.getMatchUTR());

/*            int rowIndex=8;
            for (String key: team.getLines().keySet()) {
                row.createCell(rowIndex).setCellValue(member.getLinePartners().get(key));
                rowIndex++;
            }*/
        }

        return rowNum;
    }

}
