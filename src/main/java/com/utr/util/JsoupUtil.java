package com.utr.util;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeam;
import com.utr.model.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JsoupUtil {

    public USTATeam parseUSTATeam(String teamURL) throws IOException {
        Document doc = Jsoup.connect(teamURL).get();
        String title = doc.title();
        System.out.println(title);
        USTATeam team = new USTATeam();
        team.setName(getName(title));
        team.setAlias(getAlias(title));

        System.out.println("team " + team.getName() + " alias " + team.getAlias());

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

                    System.out.println(playerEntity.getName() + " " +
                            playerEntity.getFirstName() + " "
                            + playerEntity.getLastName() + " "
                            + playerEntity.getArea() + " "
                            + playerEntity.getGender() + " "
                            + playerEntity.getUstaRating() + " "
                            + playerEntity.getNoncalLink());
                }
            }
        }

        return team;
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
        }
        int index = alias.indexOf("]");
        if (index > 0) {
            alias = alias.substring(0, index );
        }
        return alias;
    }
}
