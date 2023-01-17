package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
