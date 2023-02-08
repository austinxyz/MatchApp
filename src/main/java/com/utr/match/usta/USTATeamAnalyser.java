package com.utr.match.usta;

import com.utr.match.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Scope("singleton")
public class USTATeamAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(USTATeamAnalyser.class);

    @Autowired
    USTATeamRepository teamRepository;

    @Autowired
    USTATeamMatchRepository matchRepository;

    public USTATeamAnalysisResult compareTeam(String teamId1, String teamId2) {

        USTATeam team1 = getUstaTeam(teamId1);
        USTATeam team2 = getUstaTeam(teamId2);

        USTATeamAnalysisResult result = new USTATeamAnalysisResult(team1, team2);

        Map<String, List<USTATeamScoreCard>> candidateCards = new HashMap<>();

        Set<String> team2Opponents = team2.getOpponentTeams();
        for (String teamName: team1.getOpponentTeams()) {
            if (team2Opponents.contains(teamName)) {
                List<USTATeamScoreCard> cards = candidateCards.getOrDefault(teamName, new ArrayList<>());
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

    private USTATeam getUstaTeam(String teamId) {
        logger.debug("Get team from db by id :" + teamId);
        USTATeamEntity teamEntity = teamRepository.findById(Long.valueOf(teamId)).get();
        USTATeam team = new USTATeam(teamEntity);

        logger.debug("Find all matches for team :" + teamEntity.getName());
        for (USTATeamMatch match: matchRepository.findByTeamOrderByMatchDateAsc(teamEntity)) {
            if (match.getScoreCard() != null) {
                team.addScore(match.getScoreCard());
            }
        }
        return team;
    }
}
