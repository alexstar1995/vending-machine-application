package com.mvpfactory.vendingmachine.repository.mapper;

import com.mvpfactory.vendingmachine.model.User;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User map(UserEntity userEntity) {
       return User.builder()
               .username(userEntity.getUsername())
               .deposit(userEntity.getDeposit())
               .role(userEntity.getRole())
               .build();
    }

    public UserEntity mapForInsertion(User user, Timestamp updatedDate) {
        return getBaseBuilder(user, updatedDate)
                .insertedDate(updatedDate)
                .build();
    }

    public UserEntity mapForUpdate(User user, Timestamp updatedDate) {
        return getBaseBuilder(user, updatedDate)
                .build();
    }

    private UserEntity.UserEntityBuilder getBaseBuilder(User user, Timestamp updatedDate) {
        return UserEntity.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .deposit(user.getDeposit())
                .role(user.getRole())
                .updatedDate(updatedDate);
    }
}