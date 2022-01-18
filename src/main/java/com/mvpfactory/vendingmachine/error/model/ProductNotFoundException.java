package com.mvpfactory.vendingmachine.error.model;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) { super(message); }
}
