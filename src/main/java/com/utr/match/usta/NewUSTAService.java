package com.utr.match.usta;

import com.utr.match.entity.*;
import com.utr.match.usta.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class NewUSTAService {

    @Autowired
    USTATeamRepository teamRepository;

    @Autowired
    private USTADivisionRepository divisionRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private USTATeamImportor importor;

    @Autowired
    private USTAMatchRepository matchRepository;

    @Autowired
    private USTAFlightRepository flightRepository;

    @Autowired
    private USTAMatchLineRepository lineScoreRepository;

    @Autowired
    private USTALeagueRepository leagueRepository;
    @Autowired
    private USTATeamMemberRepository uSTATeamMemberRepository;

    @Autowired
    private USTASiteParser siteParser;

    private Map<String, USTADivisionPO> divisions;

    private List<USTALeaguePO> leagues;

    public NewUSTATeam getTeam(String id) {
        return getTeam(id, false);
    }

    public NewUSTATeam getTeam(String id, boolean loadMatch) {
        Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

        if (team.isPresent()) {
            NewUSTATeam ustaTeam = new NewUSTATeam(team.get());

            if (loadMatch) {
                ustaTeam = loadMatch(ustaTeam);
            }
            return ustaTeam;
        }

        return null;
    }

    public NewUSTATeam loadMatch(NewUSTATeam ustaTeam) {
        List<USTAMatch> matches = matchRepository.findByHomeTeam_IdOrGuestTeam_IdOrderByMatchDateAsc(
                ustaTeam.getId(), ustaTeam.getId()
        );

        for (USTAMatch match : matches) {
            ustaTeam.addMatch(match);
        }

        return ustaTeam;
    }


    public USTATeamMember getMember(String id) {
        Optional<USTATeamMember> player = uSTATeamMemberRepository.findById(Long.valueOf(id));

        if (player.isPresent()) {
            return player.get();
        }
        return null;
    }

    public PlayerEntity getPlayer(String id) {
        Optional<PlayerEntity> player = playerRepository.findById(Long.valueOf(id));

        if (player.isPresent()) {
            return player.get();
        }
        return null;
    }

    public List<NewUSTATeam> searchTeam(String name) {
        List<USTATeamEntity> teams = teamRepository.findByNameLike("%" + name + "%");
        return toUSTATeamList(teams);
    }

    public List<USTATeamMemberPO> getTeamMembersByPlayer(String id) {
        PlayerEntity member = getPlayer(id);

        if (member == null) {
            return new ArrayList<>();
        }

        Set<USTATeamMember> teams = member.getTeamMembers();
        return toUSTATeamMemberPOList(new ArrayList<>(teams));
    }

    public List<PlayerEntity> searchPlayersByName(String name) {
        List<PlayerEntity> players = new ArrayList<>();

        if (isUTRId(name)) {
            PlayerEntity player = getPlayerByUTRId(name);
            if (player != null) {
                players.add(player);
            }
        } else {
            String likeString = "%" + name + "%";
            players.addAll(playerRepository.findByNameLike(likeString));

            String reverseString = "%" + reverseName(name) + "%";

            if (!likeString.equals(reverseString)) {
                players.addAll(playerRepository.findByNameLike(reverseString));
            }
        }

        return players;
    }

    public PlayerEntity createPlayer(PlayerEntity player) {

        if (player.getUtrId() != null) {
            PlayerEntity existedPlayer = playerRepository.findByUtrId(player.getUtrId());
            if (existedPlayer != null) {
                return existedPlayer;
            }
        }

        PlayerEntity entity = playerRepository.save(player);

        return entity;
    }

    public List<PlayerEntity> searchByUTR(String ustaRating,
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
        return players.stream().toList();

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
            PlayerEntity topPlayer = players.get().collect(Collectors.toList()).get(0);
            result.put("topPlayer", topPlayer);

            Pageable midPage = PageRequest.of(players.getTotalPages() / 2, 1);

            players = playerRepository.findAll(spec, midPage);

            PlayerEntity midPlayer = players.get().collect(Collectors.toList()).get(0);
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

    public PlayerEntity getPlayerByUTRId(String utrId) {
        return playerRepository.findByUtrId(utrId);
    }

    public PlayerEntity updatePlayerUTRId(String utrId) {

        PlayerEntity member = getPlayerByUTRId(utrId);
        member = importor.updatePlayerUTRID(member);

        return member;
    }

    public PlayerEntity updatePlayerUTRValue(String utrId) {

        PlayerEntity member = getPlayerByUTRId(utrId);
        member = importor.updatePlayerUTRInfo(member, true);

        return member;
    }

    public PlayerEntity updatePlayer(String id, PlayerEntity player) {
        PlayerEntity member = getPlayer(id);

        if (member != null) {
            member.setUstaId(player.getUstaId());
            member.setUtrId(player.getUtrId());
            member.setUstaNorcalId(player.getUstaNorcalId());
            member.setSummary(player.getSummary());
            member.setMemo(player.getMemo());
            member.setLefty(player.isLefty());
            member = playerRepository.save(member);
        }
        return member;
    }

    public PlayerEntity getPlayerByNorcalId(String norcalId) {
        return playerRepository.findByUstaNorcalId(norcalId);
    }

    public List<NewUSTATeam> getTeamsByDivision(String divId) {
        List<USTATeamEntity> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(Long.valueOf(divId));
        return toUSTATeamList(teams);
    }

    public USTADivisionPO importDivisionFromUSTASite(String id, String leagueName) {
        if (this.divisions == null || this.divisions.isEmpty()) {
            this.getLeaguesFromUSTASite();
        }

        USTADivisionPO div = divisions.get(id);

        USTADivision division = divisionRepository.findByName(div.getName());
        if (division != null) {
            division.setLink(div.getLink());
            divisionRepository.save(division);

            div.setInDB(true);
            return div;
        }

        division = initDivision(div.getName(), leagueName);
        if (division == null) {
            return div;
        }

        division.setLink(div.getLink());

        division = divisionRepository.save(division);
        div.setInDB(true);
        div.setId(division.getId());

        return div;
    }

    public List<USTATeamPO> importTeamsFromUSTASite(String id, String flightURL) {
        ArrayList<USTATeamPO> result = new ArrayList<>();
        if (flightURL == null || flightURL.trim().length() == 0) {
            return result;
        }

        List<USTATeamEntity> teams = importor.importUSTAFlight(flightURL);

        for (USTATeamEntity team: teams) {
            USTATeamPO teamPO = getUstaTeamPO(team);
            result.add(teamPO);
        }

        return result;
    }

    private USTADivision initDivision(String name, String leagueName) {
        USTALeague league = leagueRepository.findByName(leagueName);

        if (league == null) {
            return null;
        }

        String level = getLevel(name);

        USTADivision div = new USTADivision(name, level, league);

        div.setAgeRange(getAgeRange(name));

        return div;

    }

    private String getAgeRange(String name) {
        String[] ages = {"18","40","55","65"};

        for (String age: ages) {
            if (name.indexOf(age) > 0) {
                return age + "+";
            }
        }

        return "18+";
    }

    private static String getLevel(String name) {
        String[] w = name.split(" ");
        String level = w[w.length-1];
        if (level.length() > 3) {
            level = level.substring(0,3);
        }
        return level;
    }

    public List<USTATeamPO> getTeamsFromUSTASite(String id) {

        if (this.divisions == null || this.divisions.isEmpty()) {
            this.getLeaguesFromUSTASite();
        }

        USTADivisionPO div = divisions.get(id);

        if (div == null) {
            return new ArrayList<>();
        }

        USTADivision division = divisionRepository.findByName(div.getName());

        List<USTATeamPO> result = new ArrayList<>();
        List<USTATeamEntity> teamFromDB = new ArrayList<>();
        if (division != null) {
            teamFromDB = teamRepository.findByDivision_IdOrderByUstaFlightAsc(Long.valueOf(division.getId()));
        }

        try {
            List<USTATeamEntity> teamFromSite = siteParser.parseDivision(div.getLink());

            Set<String> addedTeams = new HashSet<>();

            for (USTATeamEntity team: teamFromDB) {

                USTATeamPO teamPO = getUstaTeamPO(team);

                addedTeams.add(team.getName());
                result.add(teamPO);
            }

            for (USTATeamEntity team: teamFromSite) {
                if (!addedTeams.contains(team.getName())) {
                    USTATeamPO teamPO = new USTATeamPO(team.getName());
                    teamPO.setArea(team.getArea());
                    teamPO.setAlias(team.getAlias());
                    teamPO.setCaptainName(team.getCaptainName());
                    teamPO.setLink(team.getLink());
                    teamPO.setInDB(false);
                    result.add(teamPO);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    private static USTATeamPO getUstaTeamPO(USTATeamEntity team) {
        USTATeamPO teamPO = new USTATeamPO(team.getName());
        teamPO.setArea(team.getAreaCode());
        teamPO.setAlias(team.getAlias());
        teamPO.setCaptainName(team.getCaptainName());
        teamPO.setLink(team.getLink());
        teamPO.setInDB(true);
        teamPO.setId(team.getId());
        teamPO.setFlightLink(team.getUstaFlight().getLink());
        return teamPO;
    }

    public List<USTAFlight> getFlightsByDivision(String divId) {
        return flightRepository.findByDivision_Id(Long.valueOf(divId));
    }

    public List<NewUSTATeam> getTeamsByFlight(String flightId) {
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

    public List<USTALeaguePO> getLeaguesFromUSTASite() {
        if (leagues != null && leagues.size() > 0) {
            return leagues;
        }

        List<USTALeague> leaguesInDB = getLeagues("2023");
        String url = "https://www.ustanorcal.com/listdivisions.asp";
        try {
            Map<String, Map> leaguesFromSite = siteParser.parseLeagues(url);

            leagues = new ArrayList<>();
            divisions = new HashMap<>();

            Set<String> addedLeague = new HashSet<>();

            for (USTALeague league: leaguesInDB) {
                USTALeaguePO leaguePO = new USTALeaguePO(league.getName(), league.getYear());
                leaguePO.setInDB(true);

                Map<String, USTADivisionPO> leaugeDivisions = leaguesFromSite.getOrDefault(league.getName(), new HashMap<String, USTADivisionPO>());

                List<USTADivision> divisions1 = divisionRepository.findByLeague_Id(league.getId());

                for (USTADivisionPO divisionPO: leaugeDivisions.values()) {
                    divisionPO = initFromDB(divisions1, divisionPO);
                    leaguePO.getDivisions().add(divisionPO);
                }

                divisions.putAll(leaugeDivisions);

                addedLeague.add(league.getName());
                leagues.add(leaguePO);
            }

            for (String leagueName: leaguesFromSite.keySet()) {
                if (!addedLeague.contains(leagueName)) {
                    Map<String, USTADivisionPO> leaugeDivisions = leaguesFromSite.get(leagueName);
                    USTALeaguePO leaguePO = new USTALeaguePO(leagueName, "2023");
                    leaguePO.setInDB(false);
                    leaguePO.getDivisions().addAll(leaugeDivisions.values());
                    divisions.putAll(leaugeDivisions);
                    leagues.add(leaguePO);
                }
            }

            return leagues;

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private USTADivisionPO initFromDB(List<USTADivision> divisions1, USTADivisionPO divisionPO) {
        for (USTADivision division: divisions1) {
            if (division.getName().equals(divisionPO.getName())) {
                divisionPO.setId(division.getId());
                divisionPO.setInDB(true);
                return divisionPO;
            }
        }
        return divisionPO;
    }

    public List<USTAMatchLinePO> getPlayerScores(String id) {

        Optional<PlayerEntity> player = playerRepository.findById(Long.valueOf(id));

        if (player.isPresent()) {

            PlayerEntity thisPlayer = player.get();

            return toUSTAMatchLinePoList(lineScoreRepository.
                    findByHomePlayer1OrHomePlayer2OrGuestPlayer1OrGuestPlayer2(
                    thisPlayer, thisPlayer, thisPlayer, thisPlayer));

        }
        return new ArrayList<>();

    }



    public USTAMatchLine updateLineScoreInfo(long id, USTAMatchLine score) {
        Optional<USTAMatchLine> scoreData = lineScoreRepository.findById(id);

        if (scoreData.isPresent()) {
            USTAMatchLine _score = scoreData.get();
            _score.setVideoLink(score.getVideoLink());
            _score.setComment(score.getComment());

            return lineScoreRepository.save(_score);
        }

        return null;
    }

    private List<NewUSTATeam> toUSTATeamList(List<USTATeamEntity> teams) {
        List<NewUSTATeam> result = new ArrayList<>();
        if (teams == null) {
            return result;
        }

        for (USTATeamEntity entity : teams) {
            result.add(new NewUSTATeam(entity));
        }
        return result;
    }

   private List<USTATeamMemberPO> toUSTATeamMemberPOList(List<USTATeamMember> members) {
        List<USTATeamMemberPO> result = new ArrayList<>();
        if (members == null) {
            return result;
        }

        for (USTATeamMember entity : members) {
            result.add(new USTATeamMemberPO(entity));
        }
        result.sort((USTATeamMemberPO o1, USTATeamMemberPO o2) -> o2.getDivisionName().compareTo(o1.getDivisionName()));
        return result;
    }

    private List<USTAMatchLinePO> toUSTAMatchLinePoList(List<USTAMatchLine> scores) {
        List<USTAMatchLinePO> result = new ArrayList<>();
        if (scores == null) {
            return result;
        }

        for (USTAMatchLine entity : scores) {
            result.add(new USTAMatchLinePO(entity));
        }
        result.sort((USTAMatchLinePO o1, USTAMatchLinePO o2) -> o2.getMatchDate().compareTo(o1.getMatchDate()));
        return result;

    }
}
