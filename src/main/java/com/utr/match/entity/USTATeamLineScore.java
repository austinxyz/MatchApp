package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utr.match.usta.USTATeamPair;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usta_team_line_score")
public class USTATeamLineScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "score")
    private String score;

    @Column(name = "status")
    private String status;

    @Column(name = "home_win")
    private boolean homeTeamWin;

    @Column(name = "comment")
    private String comment;

    @Column(name = "video_link")
    private String videoLink;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "scorecard_id")
    private USTATeamScoreCard scoreCard;


    @ManyToOne
    @JoinColumn(name = "home_line_id")
    private USTATeamMatchLine homeLine;


    @ManyToOne
    @JoinColumn(name = "guest_line_id")
    private USTATeamMatchLine guestLine;

    public USTATeamLineScore() {

    }

    public long getId() {
        return id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHomeTeamWin() {
        return homeTeamWin;
    }

    public void setHomeTeamWin(boolean homeTeamWin) {
        this.homeTeamWin = homeTeamWin;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public USTATeamScoreCard getScoreCard() {
        return scoreCard;
    }

    public void setScoreCard(USTATeamScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    public USTATeamMatchLine getHomeLine() {
        return homeLine;
    }

    public void setHomeLine(USTATeamMatchLine homeLine) {
        this.homeLine = homeLine;
    }

    public USTATeamMatchLine getGuestLine() {
        return guestLine;
    }

    public void setGuestLine(USTATeamMatchLine guestLine) {
        this.guestLine = guestLine;
    }

    public boolean isWinner(PlayerEntity player1, PlayerEntity player2) {
        if (homeLine.isPair(player1, player2)) {
            return this.homeTeamWin;
        }

        if (guestLine.isPair(player1, player2)) {
            return !this.homeTeamWin;
        }
        return false;
    }

    public boolean isWinner(PlayerEntity player1) {
        if (homeLine.getPlayer1() != null && homeLine.getPlayer1().getId() == player1.getId() && homeLine.getPlayer2() == null) {
            return this.homeTeamWin;
        }
        if (guestLine.getPlayer1() != null && guestLine.getPlayer1().getId() == player1.getId() && guestLine.getPlayer2() == null) {
            return !this.homeTeamWin;
        }
        return false;
    }

    public boolean isWinnerTeam(String teamName) {
        if (this.getScoreCard().getHomeTeamName().equals(teamName)) {
            return this.homeTeamWin;
        }

        if (this.getScoreCard().getGuestTeamName().equals(teamName)) {
            return !this.homeTeamWin;
        }

        return false;
    }

    @JsonIgnore
    public USTATeamPair getWinnerPair() {
        if (this.homeTeamWin) {
            return new USTATeamPair(this.getHomeLine().getPlayer1(), this.getHomeLine().getPlayer2());
        } else {
            return new USTATeamPair(this.getGuestLine().getPlayer1(), this.getGuestLine().getPlayer2());
        }
    }

    @JsonIgnore
    public USTATeamPair getLoserPair() {
        if (!this.homeTeamWin) {
            return new USTATeamPair(this.getHomeLine().getPlayer1(), this.getHomeLine().getPlayer2());
        } else {
            return new USTATeamPair(this.getGuestLine().getPlayer1(), this.getGuestLine().getPlayer2());
        }
    }

    public USTATeamPair getPair(String teamName) {
        if (this.getScoreCard().getHomeTeamName().equals(teamName)) {
            return new USTATeamPair(this.getHomeLine().getPlayer1(), this.getHomeLine().getPlayer2());
        }

        if (this.getScoreCard().getGuestTeamName().equals(teamName)) {
            return new USTATeamPair(this.getGuestLine().getPlayer1(), this.getGuestLine().getPlayer2());
        }

        return null;
    }

    @JsonIgnore
    public int isSurprisedResult(String teamName) { // 1 surprised win, -1 surprised lost, 0: no surprise

        boolean isHomeTeam = this.getScoreCard().getHomeTeamName().equals(teamName);

        if (this.homeTeamWin) {
            if (this.getHomeLine().getTotalUTR() <= this.getGuestLine().getTotalUTR()) {
                // home team win while home team utr <= guest team utr, it is a surprised win
                return isHomeTeam? 1: -1;
            } else {
                // home team win and home team utr > guest team utr, no surprise
                return 0;
            }
        } else {
            if( this.getHomeLine().getTotalUTR() >= this.getGuestLine().getTotalUTR()) {
                // home team lost while home team utr >= guest team utr, it is a surprised lost
                return isHomeTeam? -1: 1;
            } else {
                // home team lost while home team utr < guest team utr, no surprise
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return "USTATeamLineScore{" +
                "score='" + score + '\'' +
                ", status='" + status + '\'' +
                ", homeTeamWin=" + homeTeamWin +
                ", homeLine=" + homeLine +
                ", guestLine=" + guestLine +
                '}';
    }
}
