package com.utr.match.batch;

import com.utr.match.entity.*;
import com.utr.match.usta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerJobConfiguration implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerJobConfiguration.class);
    private final int POOL_SIZE = 5;
    @Autowired
    USTATeamImportor importor;
    @Autowired
    USTAMatchImportor matchImportor;
    @Autowired
    USTASiteParser parser;
    @Autowired
    USTAService service;

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }


    //@Scheduled(cron = "*/5 * * * * *")
    @Scheduled(fixedDelayString = "PT12H", initialDelay = 3000)
    public void refreshUSTARating() {
        LOG.debug("Start to refresh Player's USTA Rating........");

        List<PlayerEntity> members = service.searchByUTR("3.5", "16.0",
                "0.0", "double", "F", "18+", "false", 0, 200, false, true);


        try {
            for (PlayerEntity player : members) {
                Map<String, String> playerUSTAInfo = parser.parseUSTANumber(player.getNoncalLink());
                String newRating = playerUSTAInfo.get("Rating");
                if (!newRating.equals(player.getUstaRating())) {
                    LOG.debug("Player " + player.getName() + " has new rating:" + newRating);
                    player.setUstaRating(newRating);
                    playerRepository.save(player);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        LOG.debug("Complete to update all Player's USTA Rating........");
    }

    //@Scheduled(fixedDelayString = "PT12H", initialDelay = 3000)
    public void fixUSTARating() {
        LOG.debug("Start to fix Player's USTA Rating........");

        List<PlayerEntity> members = playerRepository.findByUstaNorcalIdNotNullAndUstaRatingNull();

        try {
            for (PlayerEntity player : members) {
                Map<String, String> playerUSTAInfo = parser.parseUSTANumber("https://leagues.ustanorcal.com/playermatches.asp?id=" + player.getUstaNorcalId());
                String newRating = playerUSTAInfo.get("Rating");
                LOG.debug("Player " + player.getName() + " has rating:" + newRating);
                player.setUstaRating(newRating);
                playerRepository.save(player);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        LOG.debug("Complete to fix all Player's USTA Rating........");
    }
    //@Scheduled(fixedDelayString = "PT12H", initialDelay = 3000)
    public void mergePlayers() {
        LOG.debug("Start to merge USTA players........");

        for (PlayerEntity player : playerRepository.findByUstaIdNull()) {

            LOG.info("Player: " + player.getName());

            if (service.mergePlayer(player.getUtrId())) {

            } else {
                LOG.info("No need to merge");
            }
        }

        LOG.debug("Complete to update all Player's USTA Rating........");
    }

    // @Scheduled(cron = "*/5 * * * * *")
    //@Scheduled(fixedDelayString = "PT12H", initialDelay = 3000)
    //@Scheduled(fixedDelayString = "PT12H", initialDelayString = "PT12H")
    public void refreshUTR() {
        LOG.debug("Start to refresh Player's UTR........");

        boolean forceUpdate = false;
        boolean includeWinPercent = false;

        List<USTADivision> divisions = service.getDivisionsByYear("2023");
        Collections.shuffle(divisions);
        for (USTADivision division : divisions) {
            LOG.debug("Start to update UTR for division:" + division.getName());
            updateTeamsUTRInfoByDivision(division.getId(), forceUpdate, includeWinPercent);
            LOG.debug("Complete updating UTR for division:" + division.getName());
        }

        LOG.debug("Complete to update all Player's UTR........");
    }

    //@Scheduled(fixedDelayString = "PT6H", initialDelay = 3000)
    public void findPlayersUTR() {
        LOG.debug("Start to refresh Player's UTR........");

        boolean forceUpdate = false;
        boolean includeWinPercent = true;

        boolean isTokenExpired = importor.isTokenExpired("1316122");

        if (isTokenExpired) {
            LOG.debug("UTR Token is expired, stop update! please refresh token");
            return;
        } else {
            LOG.debug("UTR Token is valid");
        }

        //List<PlayerEntity> players = playerRepository.findTop100ByUtrIdNullAndUstaRatingLikeAndGenderAndMemoNull("3%", "M");
        List<PlayerEntity> players = playerRepository.findTop100ByUtrIdNullAndMemoNull();
        try {
        for (PlayerEntity player: players) {
            LOG.debug("Start to update UTR for player:" + player.getName());
            player = importor.updatePlayerUTRID(player);
            importor.updatePlayerUTRInfo(player, forceUpdate, includeWinPercent);
            Thread.sleep(60000);
            LOG.debug("Complete updating UTR for player:" + player.getName());
        }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOG.debug("Complete to update all Player's UTR........");
    }

    //@Scheduled(fixedDelayString = "PT36H", initialDelayString = "PT36H")
    public void refreshDR() {
        LOG.debug("Start to refresh Player's DR........");

        List<USTADivision> divisions = service.getDivisionsByYear("2023");
        Collections.shuffle(divisions);
        for (USTADivision division : divisions) {
            LOG.debug("Start to update UTR for division:" + division.getName());
            updateTeamsDRInfoByDivision(division.getId());
            LOG.debug("Complete updating UTR for division:" + division.getName());
        }

        LOG.debug("Complete to update all Player's DR........");
    }

    private void updateTeamsDRInfoByDivision(long divId) {
        List<USTATeam> teams = service.getTeamsByDivision(String.valueOf(divId));
        Collections.shuffle(teams);
        for (USTATeam team : teams) {
            LOG.debug("Start to update DR for team:" + team.getName());
            importor.updateTeamPlayersDR(team);
            LOG.debug("Team:" + team.getName() + " DR update is completed");
        }
    }

    private void updateTeamsUTRInfoByDivision(long divId, boolean forceUpdate, boolean includeWinPercent) {
        List<USTATeam> teams = service.getTeamsByDivision(String.valueOf(divId));
        Collections.shuffle(teams);
        for (USTATeam team : teams) {
            LOG.debug("Start to update UTR for team:" + team.getName());
            importor.updateTeamUTRInfo(team, forceUpdate, includeWinPercent);
            LOG.debug("Team:" + team.getName() + " UTR update is completed");
        }
    }


    //@Scheduled(fixedDelayString = "PT36H", initialDelayString = "PT36H")
    //@Scheduled(fixedDelayString = "PT12H", initialDelay = 6000)
    public void refreshScores() {
        LOG.debug("Start to refresh team's match score........");

        List<USTADivision> divisions = service.getOpenDivisions();
        Collections.shuffle(divisions);
        try {
        for (USTADivision division : divisions) {
            LOG.debug("Start to update match score for division:" + division.getName());
            updateTeamScores(division);
            Thread.sleep(60000);
            LOG.debug("Complete updating match score for division:" + division.getName());
        }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOG.debug("Complete to update all team's match score........");
    }

    //@Scheduled(fixedDelayString = "PT6H", initialDelay = 3000)
    public void refreshExpriedUTR() {

        boolean isTokenExpired = importor.isTokenExpired("1316122");

        if (isTokenExpired) {
            LOG.debug("UTR Token is expired, stop update! please refresh token");
            return;
        } else {
            LOG.debug("UTR Token is valid");
        }

        List<PlayerEntity> members = service.searchByUTR("3.5", "5.5",
                "3.0", "double", "F", "18+", "false", 0, 200, false, true);
        boolean forceUpdate = false;
        boolean includeWinPercent = false;

        try {
            for (PlayerEntity player: members) {
                if (player.isUTRRequriedRefresh()) {
                    LOG.debug("Start to update UTR for player:" + player.getName());
                    player = importor.updatePlayerUTRID(player);
                    importor.updatePlayerUTRInfo(player, forceUpdate, includeWinPercent);
                    Thread.sleep(60000);
                    LOG.debug("Complete updating UTR for player:" + player.getName());
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateTeamScores(USTADivision division) {
        List<USTATeam> teams = service.getTeamsByDivision(String.valueOf(division.getId()));
        Collections.shuffle(teams);
        for (USTATeam team : teams) {
            team = service.loadMatch(team);
            try {
            if (team.requiredUpdateScore(matchImportor.getMatchNumber(team))) {
                LOG.debug("Refresh team: " + team.getName() + " players' info");
                importor.importUSTATeam(team.getLink());
                Thread.sleep(60000);
                LOG.debug("Start to update team:" + team.getName() + "'s match score");
                matchImportor.refreshMatchesScores(team, division);
                Thread.sleep(60000);
                LOG.debug("Team:" + team.getName() + "'s match score is updated");
                Thread.sleep(600000);
            } else {
                LOG.debug("Team:" + team.getName() + " has no new match, no need to update");
            }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
