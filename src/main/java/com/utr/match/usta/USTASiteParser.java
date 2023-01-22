package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class USTASiteParser {

    private static final Logger logger = LoggerFactory.getLogger(USTASiteParser.class);

    public USTASiteParser() {
    }

    public JSONArray parseTeamMatches(USTATeam team) throws IOException {
        JSONArray result = new JSONArray();

        Document doc = Jsoup.connect(team.getLink()).get();

        Elements tables = doc.select("table:contains(Team Schedule)");

        for (Element table: tables) {
            Element scheduleTable = table.nextElementSibling();

            Element tbody = scheduleTable.child(0).child(0).child(0).child(0).child(0);

            for (Element tr: tbody.children()) {
                if (tr.children().size() == 0) {
                    continue;
                }
                String status = tr.child(0).text();

                if (!status.startsWith("Confirmed") && !status.startsWith("Scheduled")) {
                    continue;
                }

                JSONObject scoreCard;
                if (status.startsWith("Confirmed")) {
                    String href = tr.child(2).child(0).attr("href");
                    scoreCard = parseScoreCard("https://www.ustanorcal.com/" + href);
                } else {
                    scoreCard = new JSONObject();
                    String matchDate = tr.child(2).text();

                    if (matchDate == null || matchDate.trim().equals("")) {
                        continue;
                    }
                    scoreCard.put("matchDate", matchDate);

                    String home = tr.child(6).text();
                    boolean isHome = home.equals("Home");

                    if (isHome) {
                        scoreCard.put("homeTeamName", team.getName());
                        String opponentTeam = tr.child(5).text();
                        scoreCard.put("guestTeamName", getTeamName(opponentTeam));
                    } else {
                        scoreCard.put("guestTeamName", team.getName());
                        String opponentTeam = tr.child(5).text();
                        scoreCard.put("homeTeamName", getTeamName(opponentTeam));
                    }
                }
                result.put(scoreCard);
            }

        }

        return result;

    }

    public JSONObject parseScoreCard(String scoreCardURL) throws IOException {

        JSONObject result = new JSONObject();

        Document doc = Jsoup.connect(scoreCardURL).get();

        Elements trs = doc.select("tr:contains(Match Date)");

        boolean home = true;

        for (Element tr : trs) {

            Element matchTr = tr.nextElementSibling();

            result.put("matchDate", matchTr.children().get(0).text());
            result.put("homeTeamName", getTeamName(matchTr.children().get(1).text()));
            result.put("homePoint", matchTr.children().get(2).text());
            result.put("guestTeamName", getTeamName(matchTr.children().get(3).text()));
            result.put("guestPoint", matchTr.children().get(4).text());

        }

        Elements b = doc.select("b:contains(Singles)");
        for (Element e: b) {
            Element singleResultTable = e.parent().parent().parent().parent().nextElementSibling();

            Element body = singleResultTable.children().get(0);

            JSONArray singles = new JSONArray();

            for (Element res: body.children()) {
                parseResult(res, true, singles);
            }

            result.put("singles", singles);
        }

        b = doc.select("b:contains(Doubles)");
        for (Element e: b) {
            Element douleResultTable = e.parent().parent().parent().parent().nextElementSibling();

            Element body = douleResultTable.children().get(0);

            JSONArray doubles = new JSONArray();

            for (Element res: body.children()) {
                parseResult(res, false, doubles);
            }

            result.put("doubles", doubles);
        }
        return result;
    }

    private void parseResult(Element tr, boolean isSingle, JSONArray array) {
        JSONObject result = new JSONObject();

        if (isResultTr(tr)) {
            result.put("lineName", (isSingle?"S":"D") + tr.children().get(0).text());
            result.put("homePlayers", parsePlayers(tr.children().get(1)));
            result.put("guestPlayers", parsePlayers(tr.children().get(2)));
            result.put("score", tr.children().get(3).text());
            result.put("winTeam", tr.children().get(4).text());

            array.put(result);
        }

    }

    private JSONArray parsePlayers(Element td) {
        JSONArray result = new JSONArray();

        Elements links = td.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("playermatches.asp")) {
                String player = link.text();
                int last = href.indexOf("=");
                String id = href.substring(last + 1, href.length());
                JSONObject playerJSON = new JSONObject();
                playerJSON.put("name", getLastName(player) + " " + getFirstName(player));
                playerJSON.put("norcalId", id);
                result.put(playerJSON);
            }
        }
        return result;
    }

    private boolean isResultTr(Element tr) {
        String text = tr.text().trim();
        return text.length() > 1 && text.charAt(0) > '0' && text.charAt(0) <='9';
    }

    public List<String> parseUSTAFlight(String flightURL) throws IOException {

        List<String> teams = new ArrayList<>();

        Document doc = Jsoup.connect(flightURL).get();

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("TeamInfo.asp")) {

                String teamURL = "https://www.ustanorcal.com/" + href;

                teams.add(teamURL);
            }
        }
        return teams;
    }

    public USTATeam parseUSTATeam(String teamURL) throws IOException {
        Document doc = Jsoup.connect(teamURL).get();
        String title = doc.title();
        USTATeam team = new USTATeam();
        team.setName(getTeamName(title));
        team.setAlias(getAlias(title));
        team.setLink(teamURL);

        parseDivisionInfo(doc, team);

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("playermatches")) {

                Element tr = link.parent().parent();
                if (tr.tagName().equals("tr")) {
                    Elements player = tr.children();
                    String name = player.get(0).text();
                    String area = player.get(1).text();
                    String gender = player.get(2).text();
                    String rating = player.get(3).text();
                    String noncalLink = "https://www.ustanorcal.com/" + link.attr("href");
                    int last = noncalLink.indexOf("=");
                    String noncalId = noncalLink.substring(last + 1, noncalLink.length());
                    PlayerEntity playerEntity = new PlayerEntity();
                    playerEntity.setLastName(getLastName(name));
                    playerEntity.setFirstName(getFirstName(name));
                    playerEntity.setName(playerEntity.getLastName() + " " + playerEntity.getFirstName());
                    playerEntity.setArea(area);
                    playerEntity.setGender(gender);
                    playerEntity.setUstaRating(rating);
                    playerEntity.setNoncalLink(noncalLink);
                    playerEntity.setUstaNorcalId(noncalId);

                    team.getPlayers().add(playerEntity);

                    logger.debug(playerEntity.getName() + "|"
                            + playerEntity.getFirstName() + "|"
                            + playerEntity.getLastName() + "|"
                            + playerEntity.getArea() + "|"
                            + playerEntity.getGender() + "|"
                            + playerEntity.getUstaRating() + "|"
                            + playerEntity.getNoncalLink() + " | "
                            + playerEntity.getTennisRecordLink());
                } else {
                    String captainInfo = tr.text();
                    if (captainInfo.trim().startsWith("Captain:")) {
                        String name = getCaptainName(captainInfo);
                        team.setCaptainName(name);
                        parseAreaInfo(tr, team);
                    }
                }
            }
        }
        logger.debug(team.toString());
        return team;
    }

    private void parseAreaInfo(Element tr, USTATeam team) {
        Element parent = tr.parent().parent();
        Element td = parent.children().get(1);
        String text = td.text();
        int start = text.indexOf("Area:") + 5;
        int hindex = text.indexOf("Home");
        int orgIndex = text.indexOf("Org");
        int end = hindex;
        if (orgIndex != -1 && orgIndex < hindex) {
            end = orgIndex;
        }
        String area = text.substring(start, end).trim();
        team.setArea(area);
    }

    private String getCaptainName(String text) {
        int start = text.indexOf("Captain:") + 8;
        int end = text.indexOf("Email");
        String name = text.substring(start, end).trim();
        if (name.indexOf(',') > 0) {
            String[] names = name.split(",");
            return names[0].trim() + " " + names[1].trim();
        }
        return name;
    }

    private void parseDivisionInfo(Document doc, USTATeam team) {
        Element ele = doc.body().children().get(6);
        String divisionName = ele.children().get(0).children().get(0).children().get(1).children().get(0).text();
        String flight = "1";
        if (!divisionName.endsWith(".0") && !divisionName.endsWith(".5")) {
            int end = divisionName.indexOf(".0");

            if (end == -1) {
                end = divisionName.indexOf(".5");
            }

            if (end != -1) {
                flight = divisionName.substring(end + 2).trim();
                divisionName = divisionName.substring(0, end + 2);
            }
        }
        team.setDivisionName(divisionName);
        team.setFlight(flight);
        logger.debug("Division Name: " + divisionName + " Flight: " + flight);
    }

    public String parseUSTANumber(String noncalLink) throws IOException {
        Document doc = Jsoup.connect(noncalLink).get();
        Elements playerInfos = doc.getElementsByClass("PlayerInfo");
        String ustaNubmer = "";
        for (Element playerInfo : playerInfos) {
            ustaNubmer = playerInfo.children().get(0).text();

            int start = ustaNubmer.indexOf("#:");
            if (start > 0) {
                ustaNubmer = ustaNubmer.substring(start + 2).trim();
            }

            int end = ustaNubmer.indexOf(" ");
            ustaNubmer = ustaNubmer.substring(0, end);
        }
        return ustaNubmer;
    }

    public String getDynamicRating(String tennisRecordLink) throws IOException {
        Document doc = Jsoup.connect(tennisRecordLink).get();
        Elements trs = doc.select("*[style*='height:60px; border-top:1px solid #ddd;']");
        String dr = "";
        for (Element tr : trs) {
            String label = tr.children().get(0).text();
            if (label.equals("Estimated Dynamic Rating")) {
                dr = tr.children().get(1).text();

                int index = dr.indexOf(" ");
                dr = dr.substring(0, index);
                return dr;
            }
        }

        return dr;
    }

    public List<PlayerEntity> getTeamDynamicRating(String teamTennisRecordLink) throws IOException {
        List<PlayerEntity> players = new ArrayList<>();
        Document doc = Jsoup.connect(teamTennisRecordLink).get();

        Elements tables = doc.select("table:contains(Rating)");
        Element table = tables.get(0);

        int ratingIndex = getRatingIndex(table);


        Elements links = table.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("/adult/profile.aspx?playername")) {
                Element tr = link.parent().parent();
                String dr = tr.children().get(ratingIndex).text();
                PlayerEntity player = new PlayerEntity();
                String name = link.text();
                if (!dr.trim().equals("") && !dr.startsWith("-")) {
                    if (dr.trim().endsWith("M")) {
                        dr = dr.trim().substring(0, dr.length()-1).trim();
                    }
                    player.setDynamicRating(Double.parseDouble(dr));
                } else {
                    player.setDynamicRating(0.0D);
                }
                player.setName(getLastName(name) + " " + getFirstName(name) );
                player.setArea(tr.children().get(1).text());
                player.setTennisRecordLink("https://www.tennisrecord.com/" + href);

                System.out.println(player.getName() + "|" + player.getArea() + "|" + player.getDynamicRating()
                        + "|" + player.getTennisRecordLink());
                players.add(player);
            }
        }

        return players;
    }

    private int getRatingIndex(Element table) {
        Element tr = table.child(0).child(0);

        for (int i=0; i<tr.children().size(); i++) {
            if (tr.child(i).text().equals("Rating")) {
                return i;
            }
        }
        return 0;
    }

    private String getLastName(String name) {

        if (name.indexOf(",") > 0) { // name in USTA, split with ,
            return name.split(",")[0].trim();
        } else { // name in tennis record, split with " ", consider first part as first Name
            String trimedName = name.trim();
            int firstBlank = trimedName.indexOf(" ");

            if (firstBlank > 0) {
                return trimedName.substring(firstBlank+1, trimedName.length());
            }
        }

        return name;
    }

    private String getFirstName(String name) {
        if (name.indexOf(",") > 0) {
            return name.split(",")[1].trim();
        } else {
            String trimedName = name.trim();
            int firstBlank = trimedName.indexOf(" ");

            if (firstBlank > 0) {
                return trimedName.substring(0, firstBlank);
            }
        }
        return name;
    }

    private String getTeamName(String title) {
        int i = title.indexOf("|");
        String teamName = title;
        if (i > 0) {
            teamName = title.substring(i + 1);
        }

        int index = teamName.indexOf("[");
        if (index > 0) {
            teamName = teamName.substring(0, index - 1).trim();
        }

        char lastC = teamName.charAt(teamName.length()-1);

        if (lastC > '0' && lastC <= '9') {
            index = teamName.lastIndexOf(" ");

            if (index > 0) {
                teamName = teamName.substring(0, index);
            }
        }

        return teamName.trim();
    }

    private String getAlias(String title) {
        int i = title.indexOf("[");
        String alias = title;
        if (i > 0) {
            alias = title.substring(i + 1);
        } else {
            return "";
        }
        int index = alias.indexOf("]");
        if (index > 0) {
            alias = alias.substring(0, index);
        }
        return alias;
    }
}
