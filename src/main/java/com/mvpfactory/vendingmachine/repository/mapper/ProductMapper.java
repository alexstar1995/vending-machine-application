package com.mvpfactory.vendingmachine.repository.mapper;

import com.mvpfactory.vendingmachine.model.Product;
import com.mvpfactory.vendingmachine.repository.entity.ProductEntity;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ProductMapper {

    public Product map(ProductEntity productEntity) {
        return Product.builder()
                .id(productEntity.getId())
                .productName(productEntity.getProductName())
                .amountAvailable(productEntity.getAmountAvailable())
                .cost(productEntity.getCost())
                .sellerId(productEntity.getSeller().getId())
                .build();
    }

    public ProductEntity mapForInsertion(Product product, UserEntity userEntity, Timestamp timestamp) {
        return getBaseBuilder(product, userEntity, timestamp)
                .insertedDate(timestamp)
                .build();
    }

    public ProductEntity mapForUpdate(Product product, UserEntity userEntity, Timestamp timestamp) {
        return getBaseBuilder(product, userEntity, timestamp)
                .build();
    }

    private ProductEntity.ProductEntityBuilder getBaseBuilder(Product product, UserEntity userEntity, Timestamp timestamp) {
        return ProductEntity.builder()
                .productName(product.getProductName())
                .amountAvailable(product.getAmountAvailable())
                .cost(product.getCost())
                .seller(userEntity)
                .updatedDate(timestamp);
    }
}