package com.utr.parser;

import com.utr.model.*;
import org.springframework.boot.json.JsonParserFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerResultParser extends UTRJSONHandler {

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    String playerId;

    public PlayerResultParser(String playerId) {
        this.playerId = playerId;
    }

    PlayerResult parseResult(String resultJsonString) {
        PlayerResult result = new PlayerResult(playerId);

        if (resultJsonString == null || resultJsonString.equals("")) {
            return result;
        }

        Map<String, Object> resultJson = parseJsonMap(resultJsonString);

        if (resultJson == null) {
            return result;
        }

        result.setWinsNumber((Integer) resultJson.get("wins"));
        result.setLossesNumber((Integer) resultJson.get("losses"));
        result.setWithdrawsNumber((Integer) resultJson.get("withdrawls"));

        List events = (List) resultJson.get("events");

        for (Object eventJson : events) {
            parsePlayerEvent((Map<String, Object>) eventJson, result);
        }

        return result;
    }

    private PlayerEvent parsePlayerEvent(Map<String, Object> eventJson, PlayerResult result) {

        if (eventJson.get("id") == null) {
            return null;
        }

        String eventName = (String) eventJson.get("name");

        PlayerEvent event = null;
        if (eventName != null && eventName.startsWith("USTA")) {
            event = result.getEventByName(eventName);
        }

        if (event == null) {
            event = new PlayerEvent(((Integer) eventJson.get("id")).toString(), eventName);
            result.getPlayerEvents().add(event);
        }

        List<Map<String, Object>> draws = (List<Map<String, Object>>) eventJson.get("draws");

        for (Map<String, Object> draw : draws) {
            event.getResults().addAll(parsePlayerResults(draw));
        }

        return event;
    }

    private List<MatchResult> parsePlayerResults(Map<String, Object> drawJson) {
        List<MatchResult> results = new ArrayList<>();

        String drawName = (String) drawJson.get("name");

        List<Map<String, Object>> resultsJson = (List<Map<String, Object>>) drawJson.get("results");

        for (Map<String, Object> resultJson : resultsJson) {
            Map<String, Object> roundJson = (Map<String, Object>) resultJson.get("round");
            LocalDateTime date = LocalDateTime.parse((String) resultJson.get("date"), formatter);
            String name = drawName + " " + (roundJson == null ? "" : (String) roundJson.get("name"));
            MatchResult result = new MatchResult(name, date, this.playerId);

            Map<String, Object> playersJson = (Map<String, Object>) resultJson.get("players");

            result.setWinner1(parsePlayer((Map<String, Object>) playersJson.get("winner1")));
            result.setWinner2(parsePlayer((Map<String, Object>) playersJson.get("winner2")));
            result.setLoser1(parsePlayer((Map<String, Object>) playersJson.get("loser1")));
            result.setLoser2(parsePlayer((Map<String, Object>) playersJson.get("loser2")));

            result.setScore(parseScore((Map<String, Object>) resultJson.get("score")));

            results.add(result);
        }

        return results;
    }

    private MatchScore parseScore(Map<String, Object> scoreJson) {

        MatchScore score = new MatchScore();

        for (int i = 1; i <= scoreJson.size(); i++) {
            Map<String, Object> scoreRound = (Map<String, Object>) scoreJson.get(String.valueOf(i));

            if (scoreRound != null) {
                Integer tiebreak = (Integer) scoreRound.get("tiebreak");
                Integer winnerTiebreak = (Integer) scoreRound.get("winnerTiebreak");
                Integer winner = (Integer) scoreRound.get("winner");
                Integer loser = (Integer) scoreRound.get("loser");
                score.addRound(
                        winner == null ? -1 : winner,
                        loser == null ? -1 : loser,
                        tiebreak == null ? -1 : tiebreak,
                        winnerTiebreak == null ? -1 : winnerTiebreak
                );
            }
        }

        return score;
    }

    private Player parsePlayer(Map<String, Object> playerJson) {
        if (playerJson == null) {
            return null;
        }

        String firstName = (String) playerJson.get("firstName");
        String lastName = (String) playerJson.get("lastName");
        String gender = (String) playerJson.get("gender");
        String UTR = (String) playerJson.get("doublesUtrDisplay");

        Player player = new Player(firstName, lastName, gender, UTR);

        player.setId((String) playerJson.get("id"));
        player.setdUTR((Double) playerJson.get("doublesUtr"));
        player.setsUTR((Double) playerJson.get("singlesUtr"));
        player.setdUTRStatus((String) playerJson.get("ratingStatusDoubles"));
        player.setsUTRStatus((String) playerJson.get("ratingStatusSingles"));

        return player;
    }
}
