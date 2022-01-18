package com.mvpfactory.vendingmachine.repository;

import com.mvpfactory.vendingmachine.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findProductEntitiesByProductName(String productName);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE products SET amount_available = amount_available - :amount WHERE id = :id", nativeQuery = true)
    void decrementAmountBy(@Param("id") UUID id, @Param("amount") Integer amount);
}