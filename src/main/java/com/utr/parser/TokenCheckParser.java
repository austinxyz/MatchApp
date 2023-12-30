package com.utr.parser;

import com.utr.model.Player;

import java.sql.Timestamp;
import java.util.Map;

public class TokenCheckParser extends UTRJSONHandler {
    String playerId;

    public TokenCheckParser(String playerId) {
        this.playerId = playerId;
    }


    public boolean parseResult(String playerJsonString) {

        Map<String, Object> playerJson = parseJsonMap(playerJsonString);

        return parsePlayer(playerJson);

    }

    private boolean parsePlayer(Map<String, Object> playerJson) {
        if (playerJson == null) {
            return true;
        }

        String utr = (String)playerJson.get("doublesUtrDisplay");

        return utr.indexOf(".xx") >0;

    }

}
