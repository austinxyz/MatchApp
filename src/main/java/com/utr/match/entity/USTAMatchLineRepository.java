package com.utr.match.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface USTAMatchLineRepository extends JpaRepository<USTAMatchLine, Long> {
    List<USTAMatchLine> findByHomePlayer1OrHomePlayer2OrGuestPlayer1OrGuestPlayer2(PlayerEntity homePlayer1, PlayerEntity homePlayer2, PlayerEntity guestPlayer1, PlayerEntity guestPlayer2);

    
}