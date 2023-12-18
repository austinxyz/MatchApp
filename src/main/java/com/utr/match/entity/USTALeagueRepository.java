package com.utr.match.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface USTALeagueRepository extends CrudRepository<USTALeague, Long> {

    List<USTALeague> findByNameLike(String name);

    List<USTALeague> findByYear(String year);

    List<USTALeague> findByYearIn(Collection<String> years);

    USTALeague findByName(String name);

}
