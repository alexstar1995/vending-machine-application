package com.mvpfactory.vendingmachine.error.model;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) { super(message); }
}