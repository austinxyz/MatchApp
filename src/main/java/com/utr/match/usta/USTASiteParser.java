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

        logger.debug("Doules ");
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
                    PlayerEntity playerEntity = new PlayerEntity();
                    playerEntity.setLastName(getLastName(name));
                    playerEntity.setFirstName(getFirstName(name));
                    playerEntity.setName(playerEntity.getLastName() + " " + playerEntity.getFirstName());
                    playerEntity.setArea(area);
                    playerEntity.setGender(gender);
                    playerEntity.setUstaRating(rating);
                    playerEntity.setNoncalLink(noncalLink);

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
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if (href.startsWith("/adult/profile.aspx?playername")) {
                Element tr = link.parent().parent();
                if (tr.parent().parent().parent().parent().className().equals("large")) {
                    continue;
                }
                String dr = tr.children().get(5).text();
                if (dr.trim().equals("") || dr.indexOf("-") > 0) {
                    if (tr.children().size() > 8) {
                        // try to get #7.
                        dr = tr.children().get(7).text();

                        if ((dr.trim().equals("") || dr.indexOf("-") > 0)) {
                            continue;
                        }
                    }
                }
                PlayerEntity player = new PlayerEntity();
                String name = link.text();
                player.setDynamicRating(Double.parseDouble(dr));
                player.setName(name);
                player.setArea(tr.children().get(1).text());
                player.setTennisRecordLink("https://www.tennisrecord.com/" + href);

                System.out.println(player.getName() + "|" + player.getArea() + "|" + player.getDynamicRating()
                        + "|" + player.getTennisRecordLink());
                players.add(player);
            }
        }

        return players;
    }

    private String getLastName(String name) {
        if (name.indexOf(",") > 0) {
            return name.split(",")[0].trim();
        }
        return name;
    }

    private String getFirstName(String name) {
        if (name.indexOf(",") > 0) {
            return name.split(",")[1].trim();
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
