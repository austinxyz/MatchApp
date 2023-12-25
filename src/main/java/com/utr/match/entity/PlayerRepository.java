package com.utr.match.entity;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends CrudRepository<PlayerEntity, Long>, JpaSpecificationExecutor<PlayerEntity> {

    List<PlayerEntity> findAll();

    List<PlayerEntity> findByUtrId(String id);

    List<PlayerEntity> findByFirstNameLike(String name);

    List<PlayerEntity> findByLastNameLike(String name);

    List<PlayerEntity> findByFirstNameLikeOrLastNameLike(String firstName, String lastName);

    List<PlayerEntity> findByNameLike(String name);

    List<PlayerEntity> findByUtrFetchedTimeNull();

    List<PlayerEntity> findByNoncalLinkNotNullAndUstaNorcalIdNull();

    List<PlayerEntity> findByUstaIdNull();

    PlayerEntity findByUstaNorcalId(String ustaNorcalId);

    PlayerEntity findByNameIgnoreCase(String name);

    List<PlayerEntity> findByName(String name);

    List<PlayerEntity> findByUtrIdNull();

    List<PlayerEntity> findByUstaNorcalIdNotNullAndUstaRatingNull();



}
