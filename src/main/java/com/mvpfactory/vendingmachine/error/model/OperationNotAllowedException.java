package com.mvpfactory.vendingmachine.error.model;

public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String message) { super(message); }
}
