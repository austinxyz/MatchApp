package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTAFlightRepository extends CrudRepository<USTAFlight, Long> {
    USTAFlight findByDivision_IdAndFlightNo(long id, int flightNo);
    List<USTAFlight> findByDivision_Id(long id);

    List<USTAFlight> findAll();
}
