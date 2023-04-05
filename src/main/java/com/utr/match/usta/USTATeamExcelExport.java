package com.utr.match.usta;

import com.utr.match.entity.USTAMatch;
import com.utr.match.entity.USTAMatchLine;
import com.utr.match.entity.USTATeamMember;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class USTATeamExcelExport extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // define excel file name to be exported
        response.addHeader("Content-Disposition", "attachment;fileName=team.xlsx");

        // read data provided by controller
        @SuppressWarnings("unchecked")
        NewUSTATeam team = (NewUSTATeam) model.get("team");

        // create one sheet
        Sheet sheet = workbook.createSheet("Team" );

        int rowNum = createTeamPlayerInfo(team, sheet);

        rowNum = createPlayOffMatchInfo(team, sheet, rowNum + 1);

        rowNum = createTeamSummary(team, sheet, rowNum + 1);

    }

    private int createTeamSummary(NewUSTATeam team, Sheet sheet, int startRow) {
        int rowNum = startRow;
        Row teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("W/L");
        teamRow.createCell(1).setCellValue(team.getCurrentWinMatchNo() + "/" + (team.totalMatchNo- team.currentWinMatchNo));
        teamRow.createCell(2).setCellValue("Score");
        teamRow.createCell(3).setCellValue(team.getCurrentScore());

        teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Best Double by UTR");
        teamRow.createCell(1).setCellValue(team.getBestUTRDouble().getPairInfo());
        teamRow.createCell(2).setCellValue("Best Double by DR");
        teamRow.createCell(3).setCellValue(team.getBestDRDouble().getPairInfo());

        if (team.getBestUTRSingle()!=null) {
            teamRow = sheet.createRow(rowNum++);
            teamRow.createCell(0).setCellValue("Best Single by UTR");
            teamRow.createCell(1).setCellValue(team.getBestUTRSingle().getPlayer().getPlayerInfo(true));
            teamRow.createCell(2).setCellValue("Best Single by DR");
            teamRow.createCell(3).setCellValue(team.getBestDRSingle().getPlayer().getPlayerInfo(true));
        }

        teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Line");
        teamRow.createCell(1).setCellValue("Best Player/s");
        teamRow.createCell(2).setCellValue("Team W/L");
        teamRow.createCell(3).setCellValue("Team Average UTR");

        for (USTADoubleLineStat doubleStat: team.getDoubleLineStats().values()) {
            teamRow = sheet.createRow(rowNum++);
            teamRow.createCell(0).setCellValue(doubleStat.getLineName());
            teamRow.createCell(1).setCellValue(doubleStat.bestPair().getPairInfo());
            teamRow.createCell(2).setCellValue(doubleStat.getWinMatchNo() + "/" + doubleStat.getLostMatchNo() + "(" + String.format("%.2f", doubleStat.getWinPrecent()*100) + "%)");
            teamRow.createCell(3).setCellValue(String.format("%.2f",doubleStat.averageUTRs()));
        }

        if (team.getBestUTRSingle()!=null) {
            for (USTASingleLineStat singleStat: team.getSingleLineStats().values()) {
                teamRow = sheet.createRow(rowNum++);
                teamRow.createCell(0).setCellValue(singleStat.getLineName());
                teamRow.createCell(1).setCellValue(singleStat.getBestSingle().getInfo());
                teamRow.createCell(2).setCellValue(singleStat.getWinMatchNo() + "/" + singleStat.getLostMatchNo() + "(" + String.format("%.2f", singleStat.getWinPrecent()*100) + "%)");
                teamRow.createCell(3).setCellValue(String.format("%.2f", singleStat.averageUTR()));
            }
        }
        return rowNum;
    }

    private int createPlayOffMatchInfo(NewUSTATeam team, Sheet sheet, int startRow) {
        // create row1 onwards from List<T>
        int rowNum = startRow;
        int index = 0;
        for(USTAMatch match: team.getMatches()) {
            if (!match.getType().equals(USTAMatch.LOCAL)) {

                List<USTAMatchLine> lines = match.getSortLines();
                if (lines.isEmpty()) {
                    continue;
                }
                Row matchRow = sheet.createRow(rowNum++);
                index++;
                matchRow.createCell(0).setCellValue(match.getMatchDate().toString() + " - PlayOff " + index );
                matchRow.createCell(1).setCellValue("Home(" + (match.getHomeWin()?"Win":"Lost") + ")- " + match.getHomeTeamName() );
                matchRow.createCell(2).setCellValue("Guest(" + (match.getHomeWin()?"Lost":"Win") + ")- " + match.getGuestTeamName());
                matchRow.createCell(3).setCellValue("Winner Score");
                matchRow.createCell(4).setCellValue("Win Team");

                for (USTAMatchLine line : lines) {
                    Row row = sheet.createRow(rowNum++);
                    boolean isSingle = line.getType().equals("S");
                    row.createCell(0).setCellValue(line.getName());
                    row.createCell(1).setCellValue(line.getHomePair().getPairInfo(isSingle));
                    row.createCell(2).setCellValue(line.getGuestPair().getPairInfo(isSingle));
                    row.createCell(3).setCellValue(line.getScore());
                    row.createCell(4).setCellValue(line.isHomeTeamWin()? "Home":"Away");
                }
            }
        }

        return rowNum;
    }

    private int createTeamPlayerInfo(NewUSTATeam team, Sheet sheet) {

        int rowNum = 0;

        Row teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Team Name");
        teamRow.createCell(1).setCellValue(team.getTeamName());
        teamRow.createCell(2).setCellValue("Area");
        teamRow.createCell(3).setCellValue(team.getArea());
        teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Captain");
        teamRow.createCell(1).setCellValue(team.getCaptainName());
        teamRow.createCell(2).setCellValue("USTA Link");
        teamRow.createCell(3).setCellValue(team.getLink());
        teamRow.createCell(4).setCellValue("Tennis Record Link");
        teamRow.createCell(5).setCellValue(team.getTennisRecordLink());

        // create row0 as a header
        Row row0 = sheet.createRow(rowNum++);
        row0.createCell(0).setCellValue("#");
        row0.createCell(1).setCellValue("Player");
        row0.createCell(2).setCellValue("DR");
        row0.createCell(3).setCellValue("W/L");
        row0.createCell(4).setCellValue("UTR");
        row0.createCell(5).setCellValue("UTR WPct");

        int index = 1;
        // create row1 onwards from List<T>
        for(USTATeamMember member: team.players) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(index++);
            row.createCell(1).setCellValue(member.getName() + "(" + member.getGender() + ") - " + member.getUSTARating());
            row.createCell(2).setCellValue(member.getDynamicRating());
            row.createCell(3).setCellValue(member.getWinNo() + "/" + member.getLostNo());
            row.createCell(4).setCellValue(member.getDUTR() + "D(" + member.getDUTRStatus() + ")/" + member.getSUTR()+"S("+member.getSUTRStatus()+")");
            row.createCell(5).setCellValue(String.format("%.2f", member.getSuccessRate()*100) + "%/" + String.format("%.2f",member.getWholeSuccessRate()*100)+"%");
        }

        return rowNum;
    }

}
