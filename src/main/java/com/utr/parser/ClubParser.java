package com.utr.parser;

import com.utr.model.Club;
import com.utr.model.Player;
import org.springframework.boot.json.JsonParserFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClubParser {
    public Club buildClub(String clubJsonString) {
        Map<String, Object> clubJson = JsonParserFactory.getJsonParser().parseMap(clubJsonString);

        Club club = new Club();

        String id = clubJson.get("id").toString();
        String name = clubJson.get("name").toString();
        Map<String, Object> location = (Map<String, Object>)clubJson.get("location");
        String displayLocation = location.get("display").toString();

        club.setId(id);
        club.setName(name);
        club.setLocation(displayLocation);

        return club;
    }


}
