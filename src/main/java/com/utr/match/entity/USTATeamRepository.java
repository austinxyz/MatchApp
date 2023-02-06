package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface USTATeamRepository extends CrudRepository<USTATeamEntity, Long> {
    List<USTATeamEntity> findByUstaFlightNull();

    List<USTATeamEntity> findByNameLike(String name);

    List<USTATeamEntity> findByName(String name);

    USTATeamEntity findByNameAndDivision_Id(String name, long id);

    USTATeamEntity findByNameAndDivision_Name(String name, String name1);


    List<USTATeamEntity> findAll();


    List<USTATeamEntity> findByDivision_IdOrderByUstaFlightAsc(Long division);

    List<USTATeamEntity> findByUstaFlight_Id(long id);

}
