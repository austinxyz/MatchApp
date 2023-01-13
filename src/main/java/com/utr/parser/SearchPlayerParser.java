package com.utr.parser;

import com.utr.model.Player;
import org.springframework.boot.json.JsonParserFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchPlayerParser {
    public List<Player> buildPlayers(String searchPlayersJson) {
        Map<String, Object> resultJson = JsonParserFactory.getJsonParser().parseMap(searchPlayersJson);

        List hits = (List)resultJson.get("hits");

        List<Player> results = new ArrayList<>();

        if (hits == null || hits.size() == 0) {
            return results;
        }

        for (Object hit: hits) {
            Map<String, Object> hitPlayer = (Map<String, Object>)hit;

            Map<String, Object> source = (Map<String, Object>)hitPlayer.get("source");

            results.add(parsePlayer(source));
        }

        return results;
    }

    private static Player parsePlayer(Map<String, Object> source) {
        String firstName = (String) source.get("firstName");
        String lastName = (String) source.get("lastName");
        String gender = ((String) source.get("gender")).equals("Male")? "M":"F";
        String UTR = (String) source.get("doublesUtrDisplay");

        Player player = new Player(firstName, lastName, gender, UTR);

        player.setId(((Integer) source.get("id")).toString());
        player.setdUTR((Double) source.get("doublesUtr"));
        player.setsUTR((Double) source.get("singlesUtr"));
        player.setdUTRStatus((String) source.get("ratingStatusDoubles"));
        player.setsUTRStatus((String) source.get("ratingStatusSingles"));

        Map<String, Object> location = (Map<String, Object>)source.get("location");

        if (location !=null) {
            player.setLocation((String) location.get("display"));
        }

        return player;
    }
}
