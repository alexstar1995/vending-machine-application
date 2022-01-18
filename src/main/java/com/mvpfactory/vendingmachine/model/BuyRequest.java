package com.mvpfactory.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Integer amount;
}
