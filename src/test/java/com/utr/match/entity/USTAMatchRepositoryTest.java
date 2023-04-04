package com.utr.match.entity;

import com.utr.match.usta.USTATeam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class USTAMatchRepositoryTest {

    @Autowired
    USTAMatchRepository matchRepository;

    @Autowired
    USTATeamMatchRepository teamMatchRepository;

    @Autowired
    USTATeamRepository teamRepository;

    @Test
    void importTeamMatchScores() {

        USTATeam team = getTeam("1", true);

        for (USTATeamMatch match: team.getMatches()) {
            USTAMatch ustaMatch = createMatch(match);
            matchRepository.save(ustaMatch);
        }
    }

    private USTAMatch createMatch(USTATeamMatch match) {

        USTATeamEntity homeTeam = match.getHome()? match.getTeam(): match.getOpponentTeam();
        USTATeamEntity guestTeam = match.getHome()? match.getOpponentTeam(): match.getTeam();

        USTAMatch result = matchRepository.findByMatchDateAndHomeTeam_IdAndGuestTeam_Id(match.getMatchDate(), homeTeam.getId(), guestTeam.getId());

        if (result == null) {
            result = new USTAMatch(homeTeam, guestTeam);
            result.setMatchDate(match.getMatchDate());
        }

        result.setHomePoint(match.getHome()? match.getPoint(): match.getOpponentPoint());
        result.setGuestPoint(match.getHome()? match.getOpponentPoint(): match.getPoint());

        result.setHomeWin(result.getHomePoint() > result.getGuestPoint());

        if (match.getScoreCard() != null) {
            result.setComment(match.getScoreCard().getComment());
            createMatchLines(result, match);
        }

        return result;
    }

    private void createMatchLines(USTAMatch result, USTATeamMatch match) {

        USTATeamScoreCard scoreCard = match.getScoreCard();

        for (USTATeamLineScore score :scoreCard.getLineScores()) {
            USTAMatchLine line = getMatchLine(result, score);
            line.setHomePlayer1(score.getHomeLine().getPlayer1());
            line.setHomePlayer2(score.getHomeLine().getPlayer2());
            line.setGuestPlayer1(score.getGuestLine().getPlayer1());
            line.setGuestPlayer2(score.getGuestLine().getPlayer2());
            line.setMatch(result);
            line.setHomeTeamWin(score.isHomeTeamWin());
            line.setStatus(score.getStatus());
            line.setScore(score.getScore());
            line.setComment(score.getComment());
            line.setVideoLink(score.getVideoLink());
        }
    }

    private USTAMatchLine getMatchLine(USTAMatch result, USTATeamLineScore score) {
        for (USTAMatchLine line: result.getLines()) {
            if (!line.getType().equals(score.getHomeLine().getType())) {
                continue;
            }
            if (!line.getName().equals(score.getHomeLine().getName())) {
                continue;
            }
            return line;
        }

        USTAMatchLine line = new USTAMatchLine(score.getHomeLine().getType(), score.getHomeLine().getName());
        result.getLines().add(line);
        return line;
    }

    public USTATeam getTeam(String id, boolean loadMatch) {
        Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

        if (team.isPresent()) {
            USTATeam ustaTeam = new USTATeam(team.get());

            if (loadMatch) {
                ustaTeam = loadMatch(ustaTeam);
            }
            return ustaTeam;
        }

        return null;
    }

    public USTATeam loadMatch(USTATeam ustaTeam) {
        List<USTATeamMatch> matches = teamMatchRepository.findByTeamOrderByMatchDateAsc(ustaTeam.getTeamEntity());

        for (USTATeamMatch match : matches) {
            ustaTeam.addMatch(match);
        }

        return ustaTeam;
    }
}