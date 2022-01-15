package com.mvpfactory.vendingmachine.error.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ApiError {

    private final UUID id;
    private final String message;
}
