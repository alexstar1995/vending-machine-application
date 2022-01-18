package com.mvpfactory.vendingmachine.service;

import com.mvpfactory.vendingmachine.error.model.*;
import com.mvpfactory.vendingmachine.model.BuyRequest;
import com.mvpfactory.vendingmachine.model.BuyResponse;
import com.mvpfactory.vendingmachine.model.Product;

import com.mvpfactory.vendingmachine.repository.ProductRepository;
import com.mvpfactory.vendingmachine.repository.UserRepository;
import com.mvpfactory.vendingmachine.repository.entity.ProductEntity;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import com.mvpfactory.vendingmachine.repository.mapper.ProductMapper;

import com.mvpfactory.vendingmachine.security.AuthUserService;
import com.mvpfactory.vendingmachine.security.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final AuthUserService authUserService;
    private final List<Integer> allowedCoins;

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository, AuthUserService authUserService,
                       ProductMapper productMapper, @Value("#{'${allowed_coins}'.split(',')}") List<Integer> allowedCoins) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.authUserService = authUserService;
        this.productMapper = productMapper;
        this.allowedCoins = allowedCoins;
    }

    public Product findProduct(String productName) {
        Optional<ProductEntity> productEntity = productRepository.findProductEntitiesByProductName(productName);
        if(productEntity.isEmpty()) {
            throw new ProductNotFoundException(String.format("Product name %s not found", productName));
        }
        return productMapper.map(productEntity.get());
    }

    public Product findProduct(UUID id) {
        Optional<ProductEntity> productEntity = productRepository.findById(id);
        if(productEntity.isEmpty()) {
            throw new ProductNotFoundException(String.format("Product id %s not found", id));
        }
        return productMapper.map(productEntity.get());
    }

    public List<Product> getAllProducts() {
        log.info("Getting all products");
        return productRepository.findAll()
                .stream()
                .map(productMapper::map)
                .collect(Collectors.toList());
    }

    public Product createProduct(Product product) {
        if(productRepository.findProductEntitiesByProductName(product.getProductName()).isPresent()) {
            throw new ProductAlreadyExistsException(String.format("Product %s already exists", product.getProductName()));
        }
        checkIfProductCostIsValid(product.getCost());

        UserEntity userEntity = getUserIfExists(product.getSellerId());

        ProductEntity insertedProduct = productRepository.save(productMapper.mapForInsertion(product, userEntity, Timestamp.from(Instant.now())));
        return productMapper.map(productRepository.getById(insertedProduct.getId()));
    }

    public Product updateProduct(Product product) {
        ProductEntity existingProductEntity = getProductIfExistsAndIfItBelongsToTheLoggedInUser(product.getId());
        checkIfProductCostIsValid(product.getCost());

        UserEntity userEntity = getUserIfExists(product.getSellerId());
        if(!userEntity.getId().equals(product.getSellerId())) {
            throw new OperationNotAllowedException(String.format("Cannot change the seller/owner of product id %s", product.getId()));
        }
        ProductEntity updatedProductEntity = productMapper.mapForUpdate(product, userEntity, Timestamp.from(Instant.now()));
        updatedProductEntity.setId(existingProductEntity.getId());
        updatedProductEntity.setInsertedDate(existingProductEntity.getInsertedDate());
        ProductEntity insertedProduct = productRepository.save(updatedProductEntity);
        return productMapper.map(productRepository.getById(insertedProduct.getId()));
    }

    public void deleteProduct(UUID id) {
        getProductIfExistsAndIfItBelongsToTheLoggedInUser(id);
        productRepository.deleteById(id);
    }

    public BuyResponse buy(BuyRequest buyRequest) {

        ProductEntity productEntity = checkIfProductExists(buyRequest.getProductId());

        AuthUserDetails loggedInUser = authUserService.getLoggedInUser();
        UserEntity userEntity = userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get();

        Integer amountToBuy = buyRequest.getAmount() > productEntity.getAmountAvailable() ? productEntity.getAmountAvailable() : buyRequest.getAmount();
        Integer orderCost = amountToBuy * productEntity.getCost();
        BuyResponse buyResponse = new BuyResponse();

        if(userEntity.getDeposit() < orderCost) {
            amountToBuy = userEntity.getDeposit() / productEntity.getCost();
            orderCost = amountToBuy * productEntity.getCost();

        }

        userRepository.decrementDepositBy(userEntity.getId(), orderCost);
        productRepository.decrementAmountBy(productEntity.getId(), amountToBuy);

        return BuyResponse.builder()
                .productId(buyRequest.getProductId())
                .totalSpent(orderCost)
                .numberOfProducts(amountToBuy)
                .change(calculateChange(userEntity.getDeposit() - orderCost))
                .build();
    }

    private List<Integer> calculateChange(Integer amount) {
        List<Integer> change = new ArrayList<>();
        allowedCoins.sort(Collections.reverseOrder());
        for (Integer coin : allowedCoins) {
            if (amount >= coin) {
                int numberOfCoins = amount / coin;
                for(int index = 0; index < numberOfCoins; index++) {
                    change.add(coin);
                }
                amount %= coin;
            }
        }
        return change;
    }

    private ProductEntity checkIfProductExists(UUID productId) {
        Optional<ProductEntity> productEntity = productRepository.findById(productId);
        if(productEntity.isEmpty()) {
            throw new ProductNotFoundException(String.format("Product id %s not found",productId));
        }
        return productEntity.get();
    }

    private ProductEntity getProductIfExistsAndIfItBelongsToTheLoggedInUser(UUID productId) {
        ProductEntity productEntity = checkIfProductExists(productId);
        AuthUserDetails loggedInUser = authUserService.getLoggedInUser();
        UserEntity userEntity = userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get();
        if(userEntity.getId() != productEntity.getSeller().getId()) {
            throw new OperationNotAllowedException("Cannot alter a product not created by you");
        }
        return productEntity;
    }

    private UserEntity getUserIfExists(UUID userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if(userEntity.isEmpty()) {
            throw new UserNotFoundException(String.format("User id %s not found", userId));
        }
        return userEntity.get();
    }

    private void checkIfProductCostIsValid(Integer cost) {
        if(cost % 5 != 0) {
            throw new InvalidProductCostException("Product cost should be a multiple of 5");
        }
    }
}