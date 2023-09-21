package com.utr.match.utr;

import com.utr.match.entity.*;
import com.utr.match.model.Team;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Division;
import com.utr.model.League;
import com.utr.model.Player;
import com.utr.model.UTRTeam;
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

    @Autowired
    private UTRTeamRepository teamRepository;

    public League getLeague(String id) {
        return parser.getLeague(id);
    }

    public List<EventEntity> getEvents(boolean active) {

        if (active) {
            return eventRepository.findByStatusNot("Completed");
        }
        return eventRepository.findAll();
    }

    public Team getTeam(String teamId) {
        UTRTeamEntity teamEntity = teamRepository.findByUtrTeamId(teamId);
        if (teamEntity!=null) {
            return teamFactory.createTeam(teamEntity);
        }
        return null;
    }

    public UTRTeamEntity importTeam(Division division) {

        UTRTeamEntity teamEntity = teamRepository.findByUtrTeamId(division.getId());

        if (teamEntity == null) {

            System.out.println("division is not in db, init team: " + division.getName());
            teamEntity = new UTRTeamEntity(division.getName(), division.getId());

            teamEntity = teamRepository.save(teamEntity);

            for(Player player: division.getPlayers()) {
                PlayerEntity playerEntity = createOrGetPlayer(player.getId());
                UTRTeamMember member = new UTRTeamMember(playerEntity);
                member.setTeam(teamEntity);
                teamEntity.getPlayers().add(member);
                System.out.println("add member: " + member.getName());
            }

        } else {
            for (Player player: division.getPlayers()) {
                UTRTeamMember member = teamEntity.getMember(player.getId());
                if (member == null) {
                    PlayerEntity playerEntity = createOrGetPlayer(player.getId());
                    if (playerEntity == null) {
                        continue;
                    }
                    member = new UTRTeamMember(playerEntity);
                    member.setTeam(teamEntity);
                    teamEntity.getPlayers().add(member);
                    System.out.println("add member: " + member.getName());
                }
            }
        }

        teamEntity = teamRepository.save(teamEntity);
        System.out.println("division: " + division.getName() + " is updated");

        return teamEntity;
    }

    public UTRTeamEntity importTeam(String teamId, League league) {

        UTRTeamEntity teamEntity = teamRepository.findByUtrTeamId(teamId);

        UTRTeam team = parser.parseTeamMembers(league, teamId);

        if (team == null) {
            System.out.println("team id" + teamId + " is not existed in UTR");
            return null;
        }

        if (teamEntity == null) {

            System.out.println("team is not in db, init team: " + team.getName());
            teamEntity = new UTRTeamEntity(team.getName(), team.getId());

            if (team.getCaptains().size()>0) {
                PlayerEntity captain = createOrGetPlayer(team.getCaptains().get(0).getId());
                if (captain!=null) {
                    teamEntity.setCaptain(captain);
                }
            }

            teamEntity = teamRepository.save(teamEntity);

            for(Player player: team.getPlayers()) {
                PlayerEntity playerEntity = createOrGetPlayer(player.getId());
                UTRTeamMember member = new UTRTeamMember(playerEntity);
                member.setTeam(teamEntity);
                teamEntity.getPlayers().add(member);
                System.out.println("add member: " + member.getName());
            }

        } else {
            for (Player player: team.getPlayers()) {
                UTRTeamMember member = teamEntity.getMember(player.getId());
                if (member == null) {
                    PlayerEntity playerEntity = createOrGetPlayer(player.getId());
                    if (playerEntity == null) {
                        continue;
                    }
                    member = new UTRTeamMember(playerEntity);
                    member.setTeam(teamEntity);
                    teamEntity.getPlayers().add(member);
                    System.out.println("add member: " + member.getName());
                }
            }
        }

        teamEntity = teamRepository.save(teamEntity);
        System.out.println("team: " + team.getName() + " is updated");

        return teamEntity;
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
            return teamFactory.createCandidateTeam(div);
        }
        return null;
    }

    public DivisionEntity addCandidate(DivisionEntity division, String playerUTRId) {
        System.out.println("add canidate:" + playerUTRId);
        PlayerEntity player = createOrGetPlayer(playerUTRId);

        if (player == null) {
            return null;
        }

        division.addCandidate(player);
        DivisionEntity result = divisionRepository.save(division);
        System.out.println("candidate " + player.getName() + " is added into team");

        return result;
    }

    private PlayerEntity createOrGetPlayer(String playerUTRId) {
        List<PlayerEntity> players = playerRepository.findByUtrId(playerUTRId);

        PlayerEntity player = null;
        if (players.size() == 0) {
            player = createPlayerEntity(playerUTRId);
        } else {
            player = players.get(0);
        }
        return player;
    }

    private PlayerEntity createPlayerEntity(String playerUTRId) {
        PlayerEntity player;

        Player candidate = null;
        candidate = parser.getPlayer(playerUTRId, true);

        if (candidate == null) {
            System.out.println("UTR ID is not valid, skip");
            return null;
        }

        System.out.println("candidate " + candidate.getName() + " is not in database, add into DB");

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

        System.out.println("player:"  + player.getName() + " is created");

        return player;
    }

    public CandidateTeam updateCandidatesUTRValue(DivisionEntity div, boolean forceUpdate, boolean includesWinPercent) {
        importor.updateCandidateListUTRInfo(div.getCandidates(), forceUpdate, includesWinPercent);
        return teamFactory.createCandidateTeam(div, true);
    }

    public void updatePlayersUTRValue(DivisionEntity division, boolean forceUpdate, boolean includesWinPercent) {
        importor.updatePlayerListUTRInfo(division.getPlayers(), forceUpdate, includesWinPercent);
    }
}