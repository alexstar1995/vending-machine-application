package com.mvpfactory.vendingmachine.repository.mapper;

import com.mvpfactory.vendingmachine.model.User;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserMapper {

    public User map(UserEntity userEntity) {
       return User.builder()
               .username(userEntity.getUsername())
               .password(userEntity.getPassword())
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
                .password(user.getPassword())
                .deposit(user.getDeposit())
                .role(user.getRole())
                .updatedDate(updatedDate);
    }
}