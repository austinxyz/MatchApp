package com.utr.match.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface USTATeamLineScoreRepository extends JpaRepository<USTATeamLineScore, Long> {
    List<USTATeamLineScore> findByGuestLine_Player1OrHomeLine_Player2OrHomeLine_Player1OrGuestLine_Player2(PlayerEntity hPlayer1, PlayerEntity hPlayer2,
                                                                                                           PlayerEntity gPlayer1, PlayerEntity gPlayer2);
    
}