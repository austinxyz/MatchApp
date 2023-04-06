package com.utr.match.usta;

import com.utr.match.entity.USTATeamMember;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;

public class USTATeamAnalyserExcelExport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      org.apache.poi.ss.usermodel.Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // define excel file name to be exported
        response.addHeader("Content-Disposition", "attachment;fileName=analysis.xlsx");

        // read data provided by controller
        @SuppressWarnings("unchecked")
        NewUSTATeamAnalysisResult result = (NewUSTATeamAnalysisResult) model.get("analysisresult");

        NewUSTATeam team1 = result.getTeam1();
        NewUSTATeam team2 = result.getTeam2();

        // create one sheet
        Sheet sheet = workbook.createSheet("Team");

        createTeamPlayerInfo(team1, sheet);
    }

    private void createTeamPlayerInfo(NewUSTATeam team1, Sheet sheet) {
        // create row0 as a header
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("Name");
        row0.createCell(1).setCellValue("Gender");
        row0.createCell(2).setCellValue("NTRP");
        row0.createCell(3).setCellValue("UTR");
        row0.createCell(4).setCellValue("DR");
        row0.createCell(5).setCellValue("UTR WPct");

        // create row1 onwards from List<T>
        int rowNum = 1;
        for(USTATeamMember member: team1.players) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(member.getName());
            row.createCell(1).setCellValue(member.getGender());
            row.createCell(2).setCellValue(member.getUSTARating());
            row.createCell(3).setCellValue(member.getDUTR() + "/" + member.getSUTR());
            row.createCell(4).setCellValue(member.getDynamicRating());
            row.createCell(5).setCellValue(member.getSuccessRate() + "/" + member.getWholeSuccessRate());
        }
    }

}
