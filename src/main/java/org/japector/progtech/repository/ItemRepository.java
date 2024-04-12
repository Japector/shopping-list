package org.japector.progtech.repository;

import java.util.List;

import org.japector.progtech.entity.ItemEntity;
import org.japector.progtech.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findByUser(UserEntity user);

    void deleteAllByUser(UserEntity user);
}
