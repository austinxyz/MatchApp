package com.utr.match.usta;

import com.utr.match.entity.DivisionCandidate;
import com.utr.match.entity.DivisionEntity;
import com.utr.match.entity.USTACandidate;
import com.utr.match.entity.USTACandidateTeam;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class USTACandidateTeamExcelExport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // define excel file name to be exported
        response.addHeader("Content-Disposition", "attachment;fileName=division.xlsx");

        // read data provided by controller
        @SuppressWarnings("unchecked")
        USTACandidateTeam team = (USTACandidateTeam) model.get("candidateTeam");

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

    private int createDivisionPlayerInfo(USTACandidateTeam team, Sheet sheet, CreationHelper helper, CellStyle hylinkSytle) {

        int rowNum = 0;

        Row teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Team Name");
        teamRow.createCell(1).setCellValue(team.getName());
        teamRow.createCell(2).setCellValue("Division Name");
        teamRow.createCell(3).setCellValue(team.getDivisionName());

        // create row0 as a header
        Row row0 = sheet.createRow(rowNum++);
        row0.createCell(0).setCellValue("#");
        row0.createCell(1).setCellValue("Player");
        row0.createCell(2).setCellValue("UTR");
        row0.createCell(3).setCellValue("M/F");
        row0.createCell(4).setCellValue("Required");
        row0.createCell(5).setCellValue("USTA Rating");
        row0.createCell(6).setCellValue("UTR WPct");

        int index = 1;
        // create row1 onwards from List<T>
        for(USTACandidate member: team.getCandidates()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(index++);
            Cell player = row.createCell(1);
            player.setCellValue(member.getName());
            Cell utr = row.createCell(2);
            utr.setCellValue(member.getDUTR() + "D(" + member.getDUTRStatus() + ")");
            if (member.getUtrId()!=null && !member.getUtrId().trim().equals("")) {
                Hyperlink link = helper.createHyperlink(HyperlinkType.URL);
                link.setAddress("https://app.universaltennis.com/profiles/" + member.getUtrId());
                utr.setHyperlink(link);
                utr.setCellStyle(hylinkSytle);
            }
            row.createCell(3).setCellValue(member.getGender());
            row.createCell(4).setCellValue(member.getRequiredMatchNo());
            row.createCell(5).setCellValue(member.getUSTARating());
            row.createCell(6).setCellValue(String.format("%.2f", member.getSuccessRate()*100) + "%/" + String.format("%.2f",member.getWholeSuccessRate()*100)+"%");
        }

        return rowNum;
    }

}
