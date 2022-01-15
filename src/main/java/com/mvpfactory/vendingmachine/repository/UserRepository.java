package com.mvpfactory.vendingmachine.repository;

import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET u.deposit = 0 WHERE u.id = :id", nativeQuery = true)
    void resetDeposit(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET u.deposit = u.deposit + :coin WHERE u.id = :id", nativeQuery = true)
    void deposit(@Param("id") Long id, @Param("coin") Integer coin);
}
