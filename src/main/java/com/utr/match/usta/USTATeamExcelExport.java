package com.utr.match.usta;

import com.utr.match.entity.USTAMatch;
import com.utr.match.entity.USTAMatchLine;
import com.utr.match.entity.USTATeamMember;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
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
        USTATeam team = (USTATeam) model.get("team");

        CreationHelper helper = workbook.getCreationHelper();

        // create one sheet
        Sheet sheet = workbook.createSheet("Team" );

        CellStyle hylinkStyle = workbook.createCellStyle();
        Font hylinkFont = workbook.createFont();
        hylinkFont.setUnderline(Font.U_SINGLE);
        hylinkFont.setColor(IndexedColors.BLUE.index);
        hylinkStyle.setFont(hylinkFont);

        int rowNum = createTeamPlayerInfo(team, sheet, helper, hylinkStyle);

        rowNum = createPlayOffMatchInfo(team, sheet, rowNum + 1);

        rowNum = createTeamSummary(team, sheet, rowNum + 1);

        rowNum = createLineStatInfo(team, sheet, rowNum + 1);

    }

    private int createLineStatInfo(USTATeam team, Sheet sheet, int startRow) {
        int rowNum = startRow;

        for (USTADoubleLineStat doubleStat: team.getDoubleLineStats().values()) {

            Row teamRow = sheet.createRow(rowNum++);
            teamRow.createCell(0).setCellValue("Line:" + doubleStat.getLineName());
            teamRow.createCell(1).setCellValue("W:" + doubleStat.getWinMatchNo() +"/ L:" + doubleStat.getLostMatchNo()
                + " - " + String.format("%.2f", doubleStat.getWinPrecent()*100) + "%");

            teamRow = sheet.createRow(rowNum++);
            teamRow.createCell(0).setCellValue("#");
            teamRow.createCell(1).setCellValue("Pair");
            teamRow.createCell(2).setCellValue("W/L");

            int index=1;
            for (USTATeamPair pair:doubleStat.getPairs()) {
                teamRow = sheet.createRow(rowNum++);
                teamRow.createCell(0).setCellValue(index++);
                if (pair.getPlayer1()!=null) {
                    teamRow.createCell(1).setCellValue(pair.getPlayer1().getPlayerInfo(false));
                }
                teamRow.createCell(2).setCellValue(pair.getWinMatchNo() + " / " + pair.getLostMatchNo());
                teamRow = sheet.createRow(rowNum++);
                if (pair.getPlayer2()!=null) {
                    teamRow.createCell(1).setCellValue(pair.getPlayer2().getPlayerInfo(false));
                }
            }
        }

        if (team.getSingleLineStats() != null) {

            for (USTASingleLineStat singleStat : team.getSingleLineStats().values()) {

                Row teamRow = sheet.createRow(rowNum++);
                teamRow.createCell(0).setCellValue("Line:" + singleStat.getLineName());
                teamRow.createCell(1).setCellValue("W:" + singleStat.getWinMatchNo() + "/ L:" + singleStat.getLostMatchNo()
                        + " - " + String.format("%.2f", singleStat.getWinPrecent() * 100));

                teamRow = sheet.createRow(rowNum++);
                teamRow.createCell(0).setCellValue("#");
                teamRow.createCell(1).setCellValue("Pair");
                teamRow.createCell(2).setCellValue("W/L");

                int index=1;
                for (USTATeamSingle single : singleStat.getSinglers()) {
                    teamRow = sheet.createRow(rowNum++);
                    teamRow.createCell(0).setCellValue(index++);
                    if (single.getPlayer() != null) {
                        teamRow.createCell(1).setCellValue(single.getPlayer().getPlayerInfo(true));
                    }
                    teamRow.createCell(2).setCellValue(single.getWinMatchNo() + " / " + single.getLostMatchNo());
                }
            }
        }
        return rowNum;
    }

    private int createTeamSummary(USTATeam team, Sheet sheet, int startRow) {
        int rowNum = startRow;
        Row teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("W/L");
        teamRow.createCell(1).setCellValue(team.getCurrentWinMatchNo() + "/" + (team.totalMatchNo- team.currentWinMatchNo));
        teamRow.createCell(2).setCellValue("Score");
        teamRow.createCell(3).setCellValue(team.getCurrentScore());

        teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Best Double by UTR");
        USTATeamPair bestUTRDouble = team.getBestUTRDouble();
        if (bestUTRDouble.getPlayer1()!=null) {
            teamRow.createCell(1).setCellValue(bestUTRDouble.getPlayer1().getPlayerInfo(false));
        }
        teamRow.createCell(2).setCellValue("Best Double by DR");
        USTATeamPair bestDRDouble = team.getBestDRDouble();
        if (bestDRDouble.getPlayer1() !=null) {
            teamRow.createCell(3).setCellValue(bestDRDouble.getPlayer1().getPlayerInfo(false));
        }

        teamRow = sheet.createRow(rowNum++);
        if (bestUTRDouble.getPlayer2()!=null) {
            teamRow.createCell(1).setCellValue(bestUTRDouble.getPlayer2().getPlayerInfo(false));
        }
        if (bestDRDouble.getPlayer2() !=null) {
            teamRow.createCell(3).setCellValue(bestDRDouble.getPlayer2().getPlayerInfo(false));
        }

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
            USTATeamPair bestPair = doubleStat.bestPair();
            if (bestPair !=null && bestPair.getPlayer1()!=null) {
                teamRow.createCell(1).setCellValue(bestPair.getPlayer1().getPlayerInfo(false));
            }
            teamRow.createCell(2).setCellValue(doubleStat.getWinMatchNo() + "/" + doubleStat.getLostMatchNo() + "(" + String.format("%.2f", doubleStat.getWinPrecent()*100) + "%)");
            teamRow.createCell(3).setCellValue(String.format("%.2f",doubleStat.averageUTRs()));

            teamRow = sheet.createRow(rowNum++);
            if (bestPair !=null && bestPair.getPlayer2()!=null) {
                teamRow.createCell(1).setCellValue(bestPair.getPlayer2().getPlayerInfo(false));
            }
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

    private int createPlayOffMatchInfo(USTATeam team, Sheet sheet, int startRow) {
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

                    boolean isSingle = line.getType().equals("S");
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(line.getName());
                    USTATeamPair homePair = line.getHomePair();
                    if (homePair.getPlayer1()!=null) {
                        row.createCell(1).setCellValue(homePair.getPlayer1().getPlayerInfo(isSingle));
                    }
                    USTATeamPair guestPair = line.getGuestPair();
                    if (guestPair.getPlayer1()!=null) {
                        row.createCell(2).setCellValue(guestPair.getPlayer1().getPlayerInfo(isSingle));
                    }
                    row.createCell(3).setCellValue(line.getScore());
                    row.createCell(4).setCellValue(line.isHomeTeamWin()? "Home":"Away");

                    if (!isSingle) {
                        row = sheet.createRow(rowNum++);
                        if (homePair.getPlayer2()!=null) {
                            row.createCell(1).setCellValue(homePair.getPlayer2().getPlayerInfo(isSingle));
                        }
                        if (guestPair.getPlayer2()!=null) {
                            row.createCell(2).setCellValue(guestPair.getPlayer2().getPlayerInfo(isSingle));
                        }
                    }
                }
            }
        }

        return rowNum;
    }

    private int createTeamPlayerInfo(USTATeam team, Sheet sheet, CreationHelper helper, CellStyle hylinkSytle) {

        int rowNum = 0;

        Row teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Team Name");
        teamRow.createCell(1).setCellValue(team.getTeamName());
        teamRow.createCell(2).setCellValue("Area");
        teamRow.createCell(3).setCellValue(team.getArea());
        teamRow.createCell(4).setCellValue("");

        teamRow = sheet.createRow(rowNum++);
        teamRow.createCell(0).setCellValue("Captain");
        teamRow.createCell(1).setCellValue(team.getCaptainName());
        Cell ustaLink = teamRow.createCell(2);
        ustaLink.setCellValue("USTA Link");
        Hyperlink link = helper.createHyperlink(HyperlinkType.URL);
        link.setAddress(team.getLink());
        ustaLink.setHyperlink(link);
        ustaLink.setCellStyle(hylinkSytle);

        Cell trLink = teamRow.createCell(3);
        trLink.setCellValue("Tennis Record Link");
        link = helper.createHyperlink(HyperlinkType.URL);
        String trLinkURL = team.getTennisRecordLink().replace(" ", "%20");
        link.setAddress(trLinkURL);
        trLink.setHyperlink(link);
        trLink.setCellStyle(hylinkSytle);
        teamRow.createCell(4).setCellValue("");

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

            if (!member.isQualifiedPo()) {
                continue;
            }
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(index++);
            Cell player = row.createCell(1);
            player.setCellValue(member.getName() + "(" + member.getGender() + ") - " + member.getUSTARating());
            link = helper.createHyperlink(HyperlinkType.URL);
            link.setAddress(member.getNoncalLink());
            player.setHyperlink(link);
            player.setCellStyle(hylinkSytle);

            row.createCell(2).setCellValue(member.getDynamicRating());
            row.createCell(3).setCellValue(member.getWinNo() + "/" + member.getLostNo() + "(" + String.format("%.2f", member.getWinPercent()*100) + "%)");
            Cell utr = row.createCell(4);
            utr.setCellValue(member.getDUTR() + "D(" + member.getDUTRStatus() + ")/" + member.getSUTR()+"S("+member.getSUTRStatus()+")");
            if (member.getUtrId()!=null && !member.getUtrId().trim().equals("")) {
                link = helper.createHyperlink(HyperlinkType.URL);
                link.setAddress("https://app.universaltennis.com/profiles/" + member.getUtrId());
                utr.setHyperlink(link);
                utr.setCellStyle(hylinkSytle);
            }

            row.createCell(5).setCellValue(String.format("%.2f", member.getSuccessRate()*100) + "%/" + String.format("%.2f",member.getWholeSuccessRate()*100)+"%");
            row.createCell(6).setCellValue("");
        }

        return rowNum;
    }

}
