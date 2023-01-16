package com.utr.parser;

import com.utr.model.Player;
import com.utr.model.PlayerResult;
import org.springframework.boot.json.JsonParserFactory;

import java.sql.Timestamp;
import java.util.Map;

public class PlayerParser {
    String playerId;

    public PlayerParser(String playerId) {
        this.playerId = playerId;
    }


    public Player parseResult(String playerJsonString) {

        Map<String, Object> playerJson = JsonParserFactory.getJsonParser().parseMap(playerJsonString);

        return parsePlayer(playerJson);

    }

    private Player parsePlayer(Map<String, Object> playerJson) {
        if (playerJson == null) {
            return null;
        }

        String firstName = (String) playerJson.get("firstName");
        String lastName = (String) playerJson.get("lastName");
        String gender = (String) playerJson.get("gender");
        if (gender != null && gender.length() > 0) {
            gender = gender.substring(0,1);
        }
        String UTR = (String) playerJson.get("doublesUtrDisplay");

        Player player = new Player(firstName, lastName, gender, UTR);

        player.setId(String.valueOf(playerJson.get("id")));
        player.setdUTR(getUtr(playerJson, "doublesUtr"));
        player.setsUTR(getUtr(playerJson, "singlesUtr"));
        player.setdUTRStatus((String) playerJson.get("ratingStatusDoubles"));
        player.setsUTRStatus((String) playerJson.get("ratingStatusSingles"));
        player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

        return player;
    }

    private static Double getUtr(Map<String, Object> playerJson, String utrName) {
        return (Double) playerJson.get(utrName);
    }
}
