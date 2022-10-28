package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Team {

    String name;
    List<Player> players;

    @JsonIgnore
    List<Line> lines;

    @JsonIgnore
    List<Lineup> lineups;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.lines = new ArrayList<>();
        lines.add(new Line("D3", (float)11.0, 0));
        lines.add(new Line("MD", (float)10.5,  1));
        lines.add(new Line("D2", (float)12.0,  0));
        lines.add(new Line("D1", (float)13.0,  0));
        lines.add(new Line("WD", (float)9.5,  2));
    }

    public void matchingPairs() {
        for (Line line: this.lines) {
            possiblePairs(line);
        }
    }

    public void matchingLineup(int index) {
        if (index == 5) {
            return;
        }

        List<Lineup> newLineups = new ArrayList<>();

        Line line = lines.get(index);

        if (this.lineups == null) {
            for (PlayerPair pair: line.getTopNPairs(20)) {
                Lineup newLineup = new Lineup(this.name);
                newLineup.setLinePair(line, pair);
                if (newLineup.isValid() && newLineup.completedPairNumber() == index+1) {
                    newLineups.add(newLineup);
                }
            }
        } else {
            for (Lineup lineup : this.lineups) {
                for (PlayerPair pair : line.getTopNPairs(20)) {
                    Lineup newLineup = lineup.clone();
                    newLineup.setLinePair(line, pair);
                    if (newLineup.isValid() && newLineup.completedPairNumber() == index + 1) {
                        newLineups.add(newLineup);
                    }
                }
            }
        }
        this.lineups = newLineups;
        matchingLineup(index+1);
    }

    public void addPlayer(String name, String gender, String UTR) {
        Player player = new Player(name, gender, UTR);
        players.add(player);
    }
    public void possiblePairs(Line line) {

        int n = players.size();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                PlayerPair pair = new PlayerPair(players.get(i), players.get(j));
                line.addMatchedPair(pair);
            }
        }

    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Lineup> getLineups() {
        return lineups;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================\n");
        sb.append("com.utr.match.model.Team ").append(this.name).append(":").append(this.players.size()).append("\n");

        for (Line line: this.lines) {
            sb.append(line.toString());
        }

        return sb.toString();
    }

}
