package com.mvpfactory.vendingmachine.facade;

import com.mvpfactory.vendingmachine.model.BuyRequest;
import com.mvpfactory.vendingmachine.model.BuyResponse;
import com.mvpfactory.vendingmachine.model.Product;
import com.mvpfactory.vendingmachine.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public Product getProductByUsername(@PathVariable String productName) {
        return productService.findProduct(productName);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product getProductById(@PathVariable UUID id) {
        return productService.findProduct(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Product createProduct(@RequestBody @Valid Product product) {
        return productService.createProduct(product);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Product updateProduct(@RequestBody @Valid Product product) {
        return productService.updateProduct(product);
    }

    @PutMapping("/buy")
    @ResponseStatus(HttpStatus.OK)
    public BuyResponse buyProduct(@RequestBody @Valid BuyRequest buyRequest) {
        return productService.buy(buyRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }
}