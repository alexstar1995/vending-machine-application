package com.mvpfactory.vendingmachine.error.model;

public class InvalidProductCostException extends RuntimeException {
    public InvalidProductCostException(String message) { super(message); }
}
