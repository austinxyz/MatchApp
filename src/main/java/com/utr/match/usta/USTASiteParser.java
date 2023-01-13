package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeam;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class USTASiteParser {

    private static final Logger logger = LoggerFactory.getLogger(USTASiteParser.class);

    public USTASiteParser() {
    }

    public USTATeam parseUSTATeam(String teamURL) throws IOException {
        Document doc = Jsoup.connect(teamURL).get();
        String title = doc.title();
        USTATeam team = new USTATeam();
        team.setName(getName(title));
        team.setAlias(getAlias(title));
        team.setLink(teamURL);

        String divisionName = getDivisionName(doc);
        team.setDivisionName(divisionName);

        logger.debug(team.toString());

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
                }
            }
        }

        return team;
    }

    private String getDivisionName(Document doc) {
        Element ele = doc.body().children().get(6);
        String divisionName = ele.children().get(0).children().get(0).children().get(1).children().get(0).text();
        if (!divisionName.endsWith(".0")) {
            int end = divisionName.indexOf(".0");
            divisionName = divisionName.substring(0, end+2);
        }
        logger.debug(divisionName);
        return divisionName;
    }

    public String parseUSTANumber(String noncalLink) throws IOException {
        Document doc = Jsoup.connect(noncalLink).get();
        Elements playerInfos = doc.getElementsByClass("PlayerInfo");
        String ustaNubmer="";
        for (Element playerInfo: playerInfos) {
            ustaNubmer = playerInfo.children().get(0).text();

            int start = ustaNubmer.indexOf("#:");
            if (start > 0) {
                ustaNubmer = ustaNubmer.substring(start+2).trim();
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
        for(Element tr: trs) {
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

    private String getName(String title) {
        int i = title.indexOf("|");
        String teamName = title;
        if (i > 0) {
            teamName = title.substring(i + 1);
        }
        int index = teamName.indexOf("[");
        if (index > 0) {
            teamName = teamName.substring(0, index-1 );
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
            alias = alias.substring(0, index );
        }
        return alias;
    }
}
