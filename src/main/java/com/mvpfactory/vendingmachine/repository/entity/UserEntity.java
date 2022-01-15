package com.mvpfactory.vendingmachine.repository.entity;

import com.mvpfactory.vendingmachine.model.Role;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    @ColumnDefault("0")
    private Integer deposit;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "inserted_date")
    private Timestamp insertedDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;
}