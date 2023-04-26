package com.utr.match.utr;

import com.utr.match.TeamLoader;
import com.utr.match.entity.*;
import com.utr.match.usta.USTASiteParser;
import com.utr.match.usta.USTATeam;
import com.utr.match.usta.USTATeamImportor;
import com.utr.match.usta.po.*;
import com.utr.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class UTRService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private USTATeamImportor importor;

    @Autowired
    TeamLoader loader;

    public List<EventEntity> getEvents() {
        return eventRepository.findAll();
    }

    public EventEntity getEvent(String id) {
        return eventRepository.findByEventId(id);
    }

    public DivisionEntity getDivision(Long id) {
        Optional<DivisionEntity>  div = divisionRepository.findById(id);
        if (div.isPresent()) {
            return div.get();
        }
        return null;
    }
    public void addCandidate(DivisionEntity division, String playerUTRId) {
        System.out.println("add canidate:" + playerUTRId);
        PlayerEntity player = playerRepository.findByUtrId(playerUTRId);
        if (player == null) {

            Player candidate = null;
            candidate = loader.getPlayer(playerUTRId, true);

            if (candidate == null) {
                System.out.println("UTR ID is not valid, skip");
                return;
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
    }

    public void updateCandidatesUTRValue(DivisionEntity division, boolean forceUpdate, boolean includesWinPercent) {
        importor.updateCandidateListUTRInfo(division.getCandidates(), forceUpdate, includesWinPercent);
    }

    public void updatePlayersUTRValue(DivisionEntity division, boolean forceUpdate, boolean includesWinPercent) {
        importor.updatePlayerListUTRInfo(division.getPlayers(), forceUpdate, includesWinPercent);
    }
}