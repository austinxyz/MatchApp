package com.utr.parser;

import com.utr.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchPlayerParser extends UTRJSONHandler {
    private static Player parsePlayer(Map<String, Object> source) {
        String firstName = (String) source.get("firstName");
        String lastName = (String) source.get("lastName");
        String gender = source.get("gender").equals("Male") ? "M" : "F";
        String UTR = (String) source.get("doublesUtrDisplay");

        Player player = new Player(firstName, lastName, gender, UTR);

        player.setId(((Integer) source.get("id")).toString());
        player.setdUTR((Double) source.get("doublesUtr"));
        player.setsUTR((Double) source.get("singlesUtr"));
        player.setdUTRStatus((String) source.get("ratingStatusDoubles"));
        player.setsUTRStatus((String) source.get("ratingStatusSingles"));

        Map<String, Object> location = (Map<String, Object>) source.get("location");

        if (location != null) {
            player.setLocation((String) location.get("display"));
        }

        return player;
    }

    public List<Player> buildPlayers(String searchPlayersJson) {
        Map<String, Object> resultJson = parseJsonMap(searchPlayersJson);

        List<Player> results = new ArrayList<>();

        if (resultJson == null) {
            return results;
        }

        List hits = (List) resultJson.get("hits");

        if (hits == null || hits.size() == 0) {
            return results;
        }

        for (Object hit : hits) {
            Map<String, Object> hitPlayer = (Map<String, Object>) hit;

            Map<String, Object> source = (Map<String, Object>) hitPlayer.get("source");

            results.add(parsePlayer(source));
        }

        return results;
    }
}
