package com.utr.match.usta;

import com.utr.match.entity.*;
import com.utr.player.SingleAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("singleton")
public class USTATeamAnalyser {

    @Autowired
    USTATeamRepository teamRepository;

    @Autowired
    USTATeamMatchRepository matchRepository;

    public USTATeamAnalysisResult compareTeam(String teamId1, String teamId2) {


        USTATeam team1 = teamRepository.findById(Long.valueOf(teamId1)).get();
        List<USTATeamMatch> team1Matches = matchRepository.findByTeamOrderByMatchDateAsc(team1);

        USTATeam team2 = teamRepository.findById(Long.valueOf(teamId2)).get();
        List<USTATeamMatch> team2Matches = matchRepository.findByTeamOrderByMatchDateAsc(team2);

        USTATeamAnalysisResult result = new USTATeamAnalysisResult(team1, team2);

        Map<String, List<USTATeamScoreCard>> candidateCards = new HashMap<>();

        for (USTATeamMatch match: team1Matches) {
            if (match.getScoreCard() != null ) {
                USTATeamScoreCard card = match.getScoreCard();
                if (card.getGuestTeam().getName().equals(team1.getName())) {
                    String teamName = card.getHomeTeam().getName();
                    if (teamName.equals(team2.getName())) {
                        result.getPastScores().add(card);
                    } else {
                        List<USTATeamScoreCard> cards = candidateCards.getOrDefault(teamName, new ArrayList<>());
                        cards.add(card);
                        candidateCards.put(teamName, cards);
                    }
                }
                if (card.getHomeTeam().getName().equals(team1.getName())) {
                    String teamName = card.getGuestTeam().getName();
                    if (teamName.equals(team2.getName())) {
                        result.getPastScores().add(card);
                    } else {
                        List<USTATeamScoreCard> cards = candidateCards.getOrDefault(teamName, new ArrayList<>());
                        cards.add(card);
                        candidateCards.put(teamName, cards);
                    }
                }
            }
        }

        for (USTATeamMatch match: team2Matches) {
            if (match.getScoreCard() != null ) {
                USTATeamScoreCard card = match.getScoreCard();
                if (card.getGuestTeam().getName().equals(team2.getName())) {
                    String teamName = card.getHomeTeam().getName();
                    if (teamName.equals(team1.getName())) {
                        continue;
                    } else {
                        if (candidateCards.containsKey(teamName)) {
                            List<USTATeamScoreCard> cards = candidateCards.get(teamName);
                            if (!result.getMatchesWithSameTeam().containsKey(teamName)) {
                                result.getMatchesWithSameTeam().put(teamName, cards);
                            }
                            result.getMatchesWithSameTeam().get(teamName).add(card);
                        }
                    }
                }
                if (card.getHomeTeam().getName().equals(team2.getName())) {
                    String teamName = card.getGuestTeam().getName();
                    if (teamName.equals(team2.getName())) {
                        result.getPastScores().add(card);
                    } else {
                        if (candidateCards.containsKey(teamName)) {
                            List<USTATeamScoreCard> cards = candidateCards.get(teamName);
                            if (!result.getMatchesWithSameTeam().containsKey(teamName)) {
                                result.getMatchesWithSameTeam().get(teamName).addAll(cards);
                            }
                            result.getMatchesWithSameTeam().get(teamName).add(card);
                        }
                    }
                }
            }
        }

        return result;

    }
}
