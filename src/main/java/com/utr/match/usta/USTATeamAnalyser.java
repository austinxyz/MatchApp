package com.utr.match.usta;

import com.utr.match.entity.USTAMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("singleton")
public class USTATeamAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(USTATeamAnalyser.class);

    @Autowired
    USTAService service;

    public USTATeamAnalysisResult compareTeam(String teamId1, String teamId2) {

        USTATeam team1 = service.getTeam(teamId1, true);
        USTATeam team2 = service.getTeam(teamId2, true);

        USTATeamAnalysisResult result = new USTATeamAnalysisResult(team1, team2);

        Map<String, List<USTAMatch>> candidateCards = new HashMap<>();

        Set<String> team2Opponents = team2.getOpponentTeams();
        for (String teamName: team1.getOpponentTeams()) {
            if (team2Opponents.contains(teamName)) {
                List<USTAMatch> cards = candidateCards.getOrDefault(teamName, new ArrayList<>());
                cards.addAll(team1.getScores(teamName));
                cards.addAll(team2.getScores(teamName));
                candidateCards.put(teamName, cards);
                result.getMatchesWithSameTeam().put(teamName, cards);
            }
        }

        if (team1.getOpponentTeams().contains(team2.getTeamName())) {
            result.getPastScores().addAll(team1.getScores(team2.getTeamName()));
        }

        return result;

    }

}
