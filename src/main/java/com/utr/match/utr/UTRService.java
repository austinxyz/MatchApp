package com.utr.match.utr;

import com.utr.match.entity.*;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Scope("singleton")
public class UTRService {

    @Autowired
    TeamFactory teamFactory;
    @Autowired
    UTRParser parser;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private DivisionRepository divisionRepository;
    @Autowired
    private USTATeamImportor importor;

    public List<EventEntity> getEvents(boolean active) {

        if (active) {
            return eventRepository.findByStatusNot("Completed");
        }
        return eventRepository.findAll();
    }

    public EventEntity getEvent(String id) {
        return eventRepository.findByEventId(id);
    }

    public DivisionEntity getDivision(Long id) {
        Optional<DivisionEntity> div = divisionRepository.findById(id);
        if (div.isPresent()) {
            return div.get();
        }
        return null;
    }

    public CandidateTeam getCandidateTeam(Long id) {
        DivisionEntity div = getDivision(id);
        if (div != null) {
            return teamFactory.createTeam(div);
        }
        return null;
    }

    public PlayerEntity addCandidate(DivisionEntity division, String playerUTRId) {
        System.out.println("add canidate:" + playerUTRId);
        PlayerEntity player = playerRepository.findByUtrId(playerUTRId);
        if (player == null) {

            Player candidate = null;
            candidate = parser.getPlayer(playerUTRId, true);

            if (candidate == null) {
                System.out.println("UTR ID is not valid, skip");
                return null;
            }
            System.out.println("candidate" + candidate.getName() + " is not in database, add into DB");
            player = new PlayerEntity();
            player.setName(candidate.getName());
            player.setFirstName(candidate.getFirstName());
            player.setLastName(candidate.getLastName());
            player.setUtrId(playerUTRId);
            player.setDUTR(candidate.getdUTR());
            player.setDUTRStatus(candidate.getdUTRStatus());
            player.setSUTR(candidate.getsUTR());
            player.setSUTRStatus(candidate.getsUTRStatus());
            player.setSuccessRate(candidate.getSuccessRate());
            player.setWholeSuccessRate(candidate.getWholeSuccessRate());
            player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));
            player.setArea(candidate.getLocation());
            player.setGender(candidate.getGender());
            player = playerRepository.save(player);

        }

        division.addCandidate(player);
        divisionRepository.save(division);
        System.out.println("candidate " + player.getName() + " is added into team");

        return player;
    }

    public CandidateTeam updateCandidatesUTRValue(DivisionEntity div, boolean forceUpdate, boolean includesWinPercent) {
        importor.updateCandidateListUTRInfo(div.getCandidates(), forceUpdate, includesWinPercent);
        return teamFactory.createTeam(div, true);
    }

    public void updatePlayersUTRValue(DivisionEntity division, boolean forceUpdate, boolean includesWinPercent) {
        importor.updatePlayerListUTRInfo(division.getPlayers(), forceUpdate, includesWinPercent);
    }
}