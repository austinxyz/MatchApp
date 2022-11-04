package com.utr.match.strategy;

import com.utr.match.model.Line;
import com.utr.match.model.Lineup;
import com.utr.match.model.PlayerPair;
import com.utr.match.model.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BaseTeamStrategy {
    int count = 5;
    String name = "Base Strategy";

    int maxLineupNo = 3000;

    public void analysisLineups(Team team) {
        List<Line> lines = prepareLines(team);

        List<Lineup> lineups = matchingLineup(0, lines, new ArrayList<>() );

        ranking(lineups);

        team.setPreferedLineups(getLineups(lineups));

    }

    protected List<Line> prepareLines(Team team) {
        List<Line> res = new ArrayList<>(team.getLines().values());

        res.sort(Comparator.comparingInt(o -> o.getMatchedPairs().size()));
        return res;
    }

    public String getName() {
        return name;
    }

    private boolean getLineups(List<Lineup> lineups, List<Lineup> candidateLineups, int index) {
        if (candidateLineups.size() == this.count) {
            return true;
        }

        if (lineups.size() == index) {
            return true;
        }

        Lineup newCandidateLineup = lineups.get(index);

        if (isGoodCandidate(candidateLineups, newCandidateLineup)) {
            candidateLineups.add(newCandidateLineup);
        }

        return getLineups(lineups, candidateLineups, index + 1);

    }

    protected boolean isGoodCandidate(List<Lineup> candidateLineups, Lineup newCandidateLineup){
        return true;
    }

    private List<Lineup> getLineups(List<Lineup> lineups) {
        List<Lineup> result = new ArrayList<>();

        getLineups(lineups, result, 0);

        return result;
    }

    private void ranking(List<Lineup> lineups) {
        lineups.sort((o1, o2) -> Float.compare(getScore(o1), getScore(o2)));
    }

    protected float getScore(Lineup lineup) {
        return lineup.getGAPs();
    }

    private List<Lineup> matchingLineup(int index, List<Line> lines, List<Lineup> lineups) {
        if (index == 5) {
            return lineups;
        }

        List<Lineup> newLineups = new ArrayList<>();

        Line line = lines.get(index);

        if (lineups.isEmpty()) {
            for (PlayerPair pair: line.getMatchedPairs()) {
                Lineup newLineup = new Lineup(this.getName());
                newLineup.setLinePair(line, pair);
                if (newLineup.isValid() && newLineup.completedPairNumber() == index+1) {
                    newLineups.add(newLineup);
                }
            }
        } else {
            for (Lineup lineup : lineups) {
                for (PlayerPair pair : line.getMatchedPairs()) {
                    Lineup newLineup = lineup.clone();
                    newLineup.setLinePair(line, pair);
                    if (newLineup.isValid() && newLineup.completedPairNumber() == index + 1) {
                        newLineups.add(newLineup);
                    }
                    if (newLineups.size() > this.maxLineupNo) {
                        break;
                    }
                }
            }
        }

        return matchingLineup(index+1, lines, newLineups);
    }

}
