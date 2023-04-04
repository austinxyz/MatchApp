package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utr.match.usta.NewUSTATeamPair;

import javax.persistence.*;

@Entity
@Table(name = "usta_match_line")
public class USTAMatchLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type")
    private String type;  // S for single, D for double

    @Column(name = "name")
    private String name;  // D1, D2, D3 etc.

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "match_id")
    private USTAMatch match;


    @ManyToOne
    @JoinColumn(name = "home_player1_id")
    private PlayerEntity homePlayer1;


    @ManyToOne
    @JoinColumn(name = "home_player2_id")
    private PlayerEntity homePlayer2;

    @ManyToOne
    @JoinColumn(name = "guest_player1_id")
    private PlayerEntity guestPlayer1;


    @ManyToOne
    @JoinColumn(name = "guest_player2_id")
    private PlayerEntity guestPlayer2;


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

    public USTAMatchLine(PlayerEntity homePlayer1, PlayerEntity homePlayer2,
                         PlayerEntity guestPlayer1, PlayerEntity guestPlayer2,
                         String type, String name)
    {
        this.homePlayer1 = homePlayer1;
        this.homePlayer2 = homePlayer2;
        this.guestPlayer1 = guestPlayer1;
        this.guestPlayer2 = guestPlayer2;
        this.type = type;
        this.name = name;
    }

    public USTAMatchLine(
                         String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public USTAMatchLine() {

    }

    public PlayerEntity getHomePlayer1() {
        return homePlayer1;
    }

    public void setHomePlayer1(PlayerEntity homePlayer1) {
        this.homePlayer1 = homePlayer1;
    }

    public PlayerEntity getHomePlayer2() {
        return homePlayer2;
    }

    public void setHomePlayer2(PlayerEntity homePlayer2) {
        this.homePlayer2 = homePlayer2;
    }

    public PlayerEntity getGuestPlayer1() {
        return guestPlayer1;
    }

    public void setGuestPlayer1(PlayerEntity guestPlayer1) {
        this.guestPlayer1 = guestPlayer1;
    }

    public PlayerEntity getGuestPlayer2() {
        return guestPlayer2;
    }

    public void setGuestPlayer2(PlayerEntity guestPlayer2) {
        this.guestPlayer2 = guestPlayer2;
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

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public USTAMatch getMatch() {
        return match;
    }

    public void setMatch(USTAMatch match) {
        this.match = match;
    }

    public boolean isHomePair(PlayerEntity player1, PlayerEntity player2) {
        return isPair(player1,player2,this.homePlayer1, this.homePlayer2);
    }

    public boolean isGuestPair(PlayerEntity player1, PlayerEntity player2) {
        return isPair(player1,player2,this.guestPlayer1, this.guestPlayer2);
    }

    public boolean isPair(PlayerEntity player1, PlayerEntity player2, PlayerEntity thisPlayer1, PlayerEntity thisPlayer2) {
        if (thisPlayer1 == null || thisPlayer2 == null) {
            return false;
        }
        return type.equals("D") && ((player1.getId() == thisPlayer1.getId()
                && player2.getId() == thisPlayer2.getId())
                || (player2.getId() == thisPlayer1.getId()
                && player1.getId() == thisPlayer2.getId()));
    }

    @JsonIgnore
    public double getHomeTotalUTR() {
        return getTotalUTR(this.homePlayer1, this.homePlayer2);
    }

    @JsonIgnore
    public double getGuestTotalUTR() {
        return getTotalUTR(this.guestPlayer1, this.guestPlayer2);
    }
    @JsonIgnore
    public double getTotalUTR(PlayerEntity player1, PlayerEntity player2) {
        if (player1 == null ) {
            return 0;
        } else if (type.equals("S")) {
            return player1.getSUTR();
        } else {
            if (player2 !=null) {
                return player1.getDUTR() + player2.getDUTR();
            } else {
                return player1.getDUTR();
            }
        }
    }

    public boolean isWinner(PlayerEntity player1, PlayerEntity player2) {
        if (isHomePair(player1, player2)) {
            return this.homeTeamWin;
        }

        if (isGuestPair(player1, player2)) {
            return !this.homeTeamWin;
        }
        return false;
    }

    public boolean isWinner(PlayerEntity player1) {
        if (this.homePlayer1 != null && this.homePlayer1.getId() == player1.getId()
                && this.homePlayer2 == null) {
            return this.homeTeamWin;
        }
        if (this.guestPlayer1 != null && this.guestPlayer1.getId() == player1.getId()
                && this.guestPlayer2 == null) {
            return !this.homeTeamWin;
        }
        return false;
    }

    public boolean isWinnerTeam(String teamName) {
        if (this.getMatch().getHomeTeam().getName().equals(teamName)) {
            return this.homeTeamWin;
        }

        if (this.getMatch().getGuestTeam().getName().equals(teamName)) {
            return !this.homeTeamWin;
        }

        return false;
    }

    @JsonIgnore
    public NewUSTATeamPair getWinnerPair() {
        if (this.homeTeamWin) {
            return new NewUSTATeamPair(this.homePlayer1, this.homePlayer2);
        } else {
            return new NewUSTATeamPair(this.guestPlayer1, this.guestPlayer2);
        }
    }

    @JsonIgnore
    public NewUSTATeamPair getLoserPair() {
        if (!this.homeTeamWin) {
            return new NewUSTATeamPair(this.homePlayer1, this.homePlayer2);
        } else {
            return new NewUSTATeamPair(this.guestPlayer1, this.guestPlayer2);
        }
    }

    public NewUSTATeamPair getPair(String teamName) {
        if (this.match.getHomeTeam().getName().equals(teamName)) {
            return new NewUSTATeamPair(this.homePlayer1, this.homePlayer2);
        }

        if (this.match.getGuestTeam().getName().equals(teamName)) {
            return new NewUSTATeamPair(this.guestPlayer1, this.guestPlayer2);
        }

        return null;
    }

    @JsonIgnore
    public int isSurprisedResult(String teamName) { // 1 surprised win, -1 surprised lost, 0: no surprise

        boolean isHomeTeam = this.match.getHomeTeam().getName().equals(teamName);

        if (this.homeTeamWin) {
            if (this.getHomeTotalUTR() <= this.getGuestTotalUTR()) {
                // home team win while home team utr <= guest team utr, it is a surprised win
                return isHomeTeam? 1: -1;
            } else {
                // home team win and home team utr > guest team utr, no surprise
                return 0;
            }
        } else {
            if( this.getHomeTotalUTR() >= this.getGuestTotalUTR()) {
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
        return "USTAMatchLine{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                (homePlayer1==null? "": ", home player1=" + homePlayer1.getName() )+
                (homePlayer2==null?  "":" home player2=" + homePlayer2.getName()) +
                '}';
    }
}
