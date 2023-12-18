package com.utr.match.batch;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.entity.USTADivision;
import com.utr.match.usta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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

        List<PlayerEntity> members = service.searchByUTR("2.5", "16.0",
                "0.0", "double", "M", "18+", "false", 0, 150);


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

        List<USTADivision> divisions = service.getDivisionsByYear("2023");
        Collections.shuffle(divisions);
        for (USTADivision division : divisions) {
            LOG.debug("Start to update match score for division:" + division.getName());
            updateTeamScores(division);
            LOG.debug("Complete updating match score for division:" + division.getName());
        }
        LOG.debug("Complete to update all team's match score........");
    }

    private void updateTeamScores(USTADivision division) {
        List<USTATeam> teams = service.getTeamsByDivision(String.valueOf(division.getId()));
        Collections.shuffle(teams);
        for (USTATeam team : teams) {
            team = service.loadMatch(team);
            if (team.requiredUpdateScore(matchImportor.getMatchNumber(team))) {
                LOG.debug("Refresh team: " + team.getName() + " players' info");
                importor.importUSTATeam(team.getLink());
                LOG.debug("Start to update team:" + team.getName() + "'s match score");
                matchImportor.refreshMatchesScores(team, division);
                LOG.debug("Team:" + team.getName() + "'s match score is updated");
            } else {
                LOG.debug("Team:" + team.getName() + " has no new match, no need to update");
            }
        }
    }


}
