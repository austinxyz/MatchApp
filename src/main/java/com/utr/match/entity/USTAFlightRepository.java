package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTAFlightRepository extends CrudRepository<USTAFlight, Long> {
    USTAFlight findByDivision_IdAndFlightNoAndArea(long id, int flightNo, String area);

    List<USTAFlight> findByDivision_Id(long id);

    List<USTAFlight> findAll();
}
