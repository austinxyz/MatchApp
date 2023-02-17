package com.utr.match.usta;

import com.utr.match.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class USTAService {

    @Autowired
    USTATeamRepository teamRepository;

    @Autowired
    private USTADivisionRepository divisionRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private USTATeamImportor importor;

    @Autowired
    private USTATeamMatchRepository matchRepository;

    @Autowired
    private USTAFlightRepository flightRepository;

    @Autowired
    private USTATeamLineScoreRepository lineScoreRepository;

    @Autowired
    private USTALeagueRepository leagueRepository;

    public USTATeam getTeam(String id) {
        return getTeam(id, false);
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
        List<USTATeamMatch> matches = matchRepository.findByTeamOrderByMatchDateAsc(ustaTeam.getTeamEntity());

        for (USTATeamMatch match : matches) {
            ustaTeam.addMatch(match);
        }

        return ustaTeam;
    }


    public USTATeamMember getMember(String id) {
        Optional<PlayerEntity> player = playerRepository.findById(Long.valueOf(id));

        if (player.isPresent()) {
            return new USTATeamMember(player.get());
        }
        return null;
    }

    public List<USTATeam> searchTeam(String name) {
        List<USTATeamEntity> teams = teamRepository.findByNameLike("%" + name + "%");
        return toUSTATeamList(teams);
    }

    public List<USTATeam> getTeamsByPlayer(String id) {
        USTATeamMember member = getMember(id);

        if (member == null) {
            return new ArrayList<>();
        }

        Set<USTATeamEntity> teams = member.getPlayer().getTeams();
        return toUSTATeamList(new ArrayList<>(teams));
    }

    public List<USTATeamMember> searchMembersByName(String name) {
        List<USTATeamMember> members;

        if (isUTRId(name)) {
            members = new ArrayList<>();
            USTATeamMember member = getMember(name);
            if (member != null) {
                members.add(member);
            }
        } else {
            String likeString = "%" + name + "%";
            List<PlayerEntity> players = playerRepository.findByNameLike(likeString);

            String reverseString = "%" + reverseName(name) + "%";

            if (!likeString.equals(reverseString)) {
                players.addAll(playerRepository.findByNameLike(reverseString));
            }

            members = toUSTATeamMemberList(players);
        }

        return members;
    }

    public USTATeamMember createPlayer(PlayerEntity player) {

        if (player.getUtrId() != null) {
            PlayerEntity existedPlayer = playerRepository.findByUtrId(player.getUtrId());
            if (existedPlayer != null) {
                return new USTATeamMember(existedPlayer);
            }
        }

        PlayerEntity entity = playerRepository.save(player);

        return new USTATeamMember(entity);
    }

    public List<USTATeamMember> searchByUTR(String ustaRating,
                                            String utrLimitValue,
                                            String utrValue,
                                            String type,
                                            String gender,
                                            String ageRange,
                                            String ratedOnlyStr,
                                            int start,
                                            int size
    ) {
        Pageable firstPage = PageRequest.of(start, size);
        PlayerSpecification ustaRatingSpec = new PlayerSpecification(new SearchCriteria("ustaRating", ":", ustaRating));
        PlayerSpecification UTRSpec;
        PlayerSpecification utrLimitSpec;
        PlayerSpecification ratedOnlySpec = null;
        boolean ratedOnly = !ratedOnlyStr.equals("false");
        if (type.equalsIgnoreCase("double")) {
            UTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", Double.valueOf(utrValue)),
                    new OrderByCriteria("dUTR", false));
            utrLimitSpec = new PlayerSpecification(new SearchCriteria("dUTR", "<", utrLimitValue));
            if (ratedOnly) {
                ratedOnlySpec = new PlayerSpecification(new SearchCriteria("dUTRStatus", ":", "Rated"));
            }
        } else {
            UTRSpec = new PlayerSpecification(new SearchCriteria("sUTR", ">", Double.valueOf(utrValue)),
                    new OrderByCriteria("sUTR", false));
            utrLimitSpec = new PlayerSpecification(new SearchCriteria("sUTR", "<", utrLimitValue));
            if (ratedOnly) {
                ratedOnlySpec = new PlayerSpecification(new SearchCriteria("sUTRStatus", ":", "Rated"));
            }
        }
        PlayerSpecification genderSpec = new PlayerSpecification(new SearchCriteria("gender", ":", gender));
        PlayerSpecification ageRangeSpec = new PlayerSpecification(new SearchCriteria("ageRange", ">", ageRange));
        Specification spec = Specification.where(ustaRatingSpec).and(utrLimitSpec).and(UTRSpec).and(genderSpec).and(ageRangeSpec);

        if (ratedOnly && ratedOnlySpec != null) {
            spec = spec.and(ratedOnlySpec);
        }

        Page<PlayerEntity> players = playerRepository.findAll(spec, firstPage);
        return toUSTATeamMemberList(players.stream().toList());

    }

    public Map<String, Object> statUTR(String ustaRating,
                                       String ratedOnlyStr,
                                       String ignoreZeroUTRStr,
                                       String type,
                                       String gender,
                                       String ageRange
    ) {

        Pageable firstPage = PageRequest.of(0, 1);
        PlayerSpecification ratedOnlySpec = null;
        PlayerSpecification ignoreZeroUTRSpec = null;

        boolean ratedOnly = !ratedOnlyStr.equals("false");
        boolean ignoreZeroUTR = !ignoreZeroUTRStr.equals("false");

        OrderByCriteria orderBy;
        if (type.equalsIgnoreCase("double")) {
            orderBy = new OrderByCriteria("dUTR", false);
            if (ratedOnly) {
                ratedOnlySpec = new PlayerSpecification(new SearchCriteria("dUTRStatus", ":", "Rated"));
            }
            if (ignoreZeroUTR) {
                ignoreZeroUTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", 0.1D));
            }
        } else {
            orderBy = new OrderByCriteria("sUTR", false);
            if (ratedOnly) {
                ratedOnlySpec = new PlayerSpecification(new SearchCriteria("sUTRStatus", ":", "Rated"));
            }
            if (ignoreZeroUTR) {
                ignoreZeroUTRSpec = new PlayerSpecification(new SearchCriteria("sUTR", ">", 0.1D));
            }
        }

        PlayerSpecification ustaRatingSpec = new PlayerSpecification(new SearchCriteria("ustaRating", ":", ustaRating), orderBy);
        PlayerSpecification genderSpec = new PlayerSpecification(new SearchCriteria("gender", ":", gender));
        PlayerSpecification ageRangeSpec = new PlayerSpecification(new SearchCriteria("ageRange", ">", ageRange));

        Specification spec = Specification.where(ustaRatingSpec).and(genderSpec).and(ageRangeSpec);

        if (ratedOnly && ratedOnlySpec != null) {
            spec = spec.and(ratedOnlySpec);
        }

        if (ignoreZeroUTR && ignoreZeroUTRSpec != null) {
            spec = spec.and(ignoreZeroUTRSpec);
        }

        Page<PlayerEntity> players = playerRepository.findAll(spec, firstPage);

        Map<String, Object> result = new HashMap<>();

        if (!players.isEmpty()) {

            result.put("totalNumber", String.valueOf(players.getTotalElements()));
            USTATeamMember topPlayer = new USTATeamMember(players.get().collect(Collectors.toList()).get(0));
            result.put("topPlayer", topPlayer);

            Pageable midPage = PageRequest.of(players.getTotalPages() / 2, 1);

            players = playerRepository.findAll(spec, midPage);

            USTATeamMember midPlayer = new USTATeamMember(players.get().collect(Collectors.toList()).get(0));
            result.put("midPlayer", midPlayer);
        }

        return result;
    }

    private String reverseName(String name) {

        int index = name.indexOf(' ');
        if (index > 0 && index < name.length() - 1) {
            String first = name.substring(0, index);
            String last = name.substring(index + 1);
            return last + " " + first;
        }

        return name;
    }

    private boolean isUTRId(String name) {

        char c = name.charAt(0);

        return c >= '0' && c <= '9';
    }

    public USTATeamMember getMemberByUTRId(String utrId) {
        PlayerEntity playerData = playerRepository.findByUtrId(utrId);
        if (playerData != null) {
            return new USTATeamMember(playerData);
        }
        return null;
    }

    public USTATeamMember updatePlayerUTRId(String utrId) {

        USTATeamMember member = getMemberByUTRId(utrId);
        member = importor.updatePlayerUTRID(member);

        return member;
    }

    public USTATeamMember updatePlayerUTRValue(String utrId) {

        USTATeamMember member = getMemberByUTRId(utrId);
        member = importor.updatePlayerUTRInfo(member, true);

        return member;
    }

    public USTATeamMember updatePlayer(String id, PlayerEntity player) {
        USTATeamMember member = getMember(id);

        if (member != null) {
            PlayerEntity _player = member.getPlayer();
            _player.setUstaId(player.getUstaId());
            _player.setUtrId(player.getUtrId());
            _player.setUstaNorcalId(player.getUstaNorcalId());
            _player.setSummary(player.getSummary());
            _player.setMemo(player.getMemo());
            _player.setLefty(player.isLefty());
            _player = playerRepository.save(_player);
            member.setPlayer(_player);
        }
        return member;
    }

    public USTATeamMember getPlayerByNorcalId(String norcalId) {
        PlayerEntity playerData = playerRepository.findByUstaNorcalId(norcalId);
        if (playerData != null) {
            return new USTATeamMember(playerData);
        }
        return null;
    }

    public List<USTATeam> getTeamsByDivision(String divId) {
        List<USTATeamEntity> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(Long.valueOf(divId));
        return toUSTATeamList(teams);
    }

    public List<USTAFlight> getFlightsByDivision(String divId) {
        return flightRepository.findByDivision_Id(Long.valueOf(divId));
    }

    public List<USTATeam> getTeamsByFlight(String flightId) {
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(Long.valueOf(flightId));
        return toUSTATeamList(teams);
    }

    public List<USTADivision> getDivisions(String id) {
        return divisionRepository.findByLeague_Id(Long.valueOf(id));
    }

    public List<USTADivision> getDivisionsByYear(String year) {
        return divisionRepository.findByLeague_Year(year);
    }

    public List<USTALeague> getLeagues(String year) {
        return leagueRepository.findByYear(year);
    }

    public List<USTATeamLineScore> getPlayerScores(String id) {

        Optional<PlayerEntity> player = playerRepository.findById(Long.valueOf(id));

        if (player.isPresent()) {

            PlayerEntity thisPlayer = player.get();

            return lineScoreRepository.findByGuestLine_Player1OrHomeLine_Player2OrHomeLine_Player1OrGuestLine_Player2(
                    thisPlayer, thisPlayer, thisPlayer, thisPlayer);

        }
        return new ArrayList<>();

    }

    public USTATeamLineScore updateLineScoreInfo(long id, USTATeamLineScore score) {
        Optional<USTATeamLineScore> scoreData = lineScoreRepository.findById(id);

        if (scoreData.isPresent()) {
            USTATeamLineScore _score = scoreData.get();
            _score.setVideoLink(score.getVideoLink());
            _score.setComment(score.getComment());

            return lineScoreRepository.save(_score);
        }

        return null;
    }

    private List<USTATeam> toUSTATeamList(List<USTATeamEntity> teams) {
        List<USTATeam> result = new ArrayList<>();
        if (teams == null) {
            return result;
        }

        for (USTATeamEntity entity : teams) {
            result.add(new USTATeam(entity));
        }
        return result;
    }

    private List<USTATeamMember> toUSTATeamMemberList(List<PlayerEntity> players) {
        List<USTATeamMember> result = new ArrayList<>();
        if (players == null) {
            return result;
        }

        for (PlayerEntity entity : players) {
            result.add(new USTATeamMember(entity));
        }
        return result;
    }
}
