package com.utr.match.strategy;

import com.utr.match.TeamLoader;
import com.utr.match.model.Team;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TeamStrategyFactoryTest {

    @Test
    void getStrategy() {
        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(1);

        Team team = TeamLoader.getInstance().initTeam("ZJU-BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testFixedStrategy() {
        FixedPairTeamStrategy strategy = (FixedPairTeamStrategy) TeamStrategyFactory.getStrategy(3);
        Map<String, Set<String>> pairs = new HashMap<>();
        Set<String> mdPairs = new HashSet<>();
        //mdPairs.add("Xu  Peng,Tian Lu");
        //mdPairs.add("Tian Lu,Xu  Peng");
        mdPairs.add("Tian Lu");
        pairs.put("MD", mdPairs);

        strategy.setFixedPairs(pairs);

        Team team = TeamLoader.getInstance().initTeam("ZJU-BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testFixedMoreVarableStrategy() {
        FixedPairTeamStrategy strategy = (FixedPairWithMoreVariableTeamStrategy) TeamStrategyFactory.getStrategy(4);
        Map<String, Set<String>> pairs = new HashMap<>();
        Set<String> mdPairs = new HashSet<>();
//        mdPairs.add("Xu  Peng,Tian Lu");
        //mdPairs.add("Tian Lu,Xu  Peng");
        mdPairs.add("Tian Lu");
        pairs.put("MD", mdPairs);

        Set<String> d2Pairs = new HashSet<>();
        d2Pairs.add("Dai  Ian,Li Haoyang");
        pairs.put("D2", d2Pairs);

        strategy.setFixedPairs(pairs);

        Team team = TeamLoader.getInstance().initTeam("ZJU-BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testRestAPI() throws JSONException {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "https://app.universaltennis.com/api/v1/tms/events/123233";
        HttpHeaders headers = new HttpHeaders();
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6IjIwODkxNCIsImVtYWlsIjoiemhvdXpob25neWkuc2hAZ21haWwuY29tIiwiVmVyc2lvbiI6IjEiLCJEZXZpY2VMb2dpbklkIjoiMTI4NjAyNjMiLCJuYmYiOjE2Njg3MjQyMDAsImV4cCI6MTY3MTMxNjIwMCwiaWF0IjoxNjY4NzI0MjAwfQ.HxVRVfhpbSNqnVX1v_ZWTud1Nx0OVgG4KUnz67Ne1aU";
        headers.set("Authorization", "Bearer " + accessToken); //accessToken can be the secret key you generate.
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = "{}";
        HttpEntity<String> entity = new HttpEntity <> (requestJson, headers);
        ResponseEntity <String> response = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity, String.class);

        System.out.println(response.getBody());

        Map<String, Object> object = JsonParserFactory.getJsonParser().parseMap(response.getBody());

        System.out.println("---------------------------------------");

        System.out.println(object.get("creatingMemberId"));

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}