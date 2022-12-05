package com.utr.parser;

import com.utr.model.PlayerResult;
import org.springframework.boot.json.JsonParserFactory;

import java.util.Map;

public class PlayerResultParser {

    String playerId;

    public PlayerResultParser(String playerId) {
        this.playerId = playerId;
    }

    PlayerResult parseResult(String resultJsonString) {
       PlayerResult result = new PlayerResult(playerId);
       Map<String, Object> resultJson = JsonParserFactory.getJsonParser().parseMap(resultJsonString);

       result.setWinsNumber((Integer) resultJson.get("wins"));
       result.setLossesNumber((Integer) resultJson.get("losses"));
       result.setWithdrawsNumber((Integer) resultJson.get("withdrawls"));

       return result;
    }
}
