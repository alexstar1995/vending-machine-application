package com.mvpfactory.vendingmachine.error.model;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String message) { super(message); }
}
