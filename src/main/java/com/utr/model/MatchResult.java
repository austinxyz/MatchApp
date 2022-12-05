package com.utr.model;

import java.time.LocalDateTime;

public class MatchResult {
    String name; //draw.name + round.name
    LocalDateTime matchTime;

    String type; // single or double

    Player winner1;
    Player winner2;
    Player loser1;
    Player loser2;

    MatchScore score;

}
