package com.mvpfactory.vendingmachine.repository;

import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findUserEntityByUsername(String username);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET deposit = 0 WHERE id = :id", nativeQuery = true)
    void resetDeposit(@Param("id") UUID id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET deposit = deposit + :coin WHERE id = :id", nativeQuery = true)
    void deposit(@Param("id") UUID id, @Param("coin") Integer coin);
}
