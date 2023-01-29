package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface USTATeamRepository extends CrudRepository<USTATeam, Long> {
    List<USTATeam> findByUstaFlightNull();

    List<USTATeam> findByNameLike(String name);

    List<USTATeam> findByName(String name);

    USTATeam findByNameAndDivision_Id(String name, long id);

    USTATeam findByNameAndDivision_Name(String name, String name1);


    List<USTATeam> findAll();


    List<USTATeam> findByDivision_IdOrderByUstaFlightAsc(Long division);

    List<USTATeam> findByUstaFlight_Id(long id);

}
