package com.mvpfactory.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotBlank(message = "Mandatory username")
    private String username;

    @NotBlank(message = "Mandatory password")
    private String password;

    private Integer deposit;

    @NotBlank(message = "Mandatory role")
    private Role role;
}