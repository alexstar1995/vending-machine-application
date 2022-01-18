package com.mvpfactory.vendingmachine.repository.entity;

import lombok.*;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "products")
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name="product_name")
    private String productName;

    @Column(name="amount_available")
    private Integer amountAvailable;

    @Column(name="cost")
    private Integer cost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private UserEntity seller;

    @Column(name = "inserted_date")
    private Timestamp insertedDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;
}
