package com.mvpfactory.vendingmachine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotBlank(message = "Mandatory username")
    private String username;

    @NotBlank(message = "Mandatory password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Integer deposit;

    private Role role;
}