package com.mvpfactory.vendingmachine.service;

import com.mvpfactory.vendingmachine.error.model.DepositException;
import com.mvpfactory.vendingmachine.error.model.OperationNotAllowedException;
import com.mvpfactory.vendingmachine.error.model.UserDetailsException;
import com.mvpfactory.vendingmachine.error.model.UserNotFoundException;
import com.mvpfactory.vendingmachine.model.DepositRequest;
import com.mvpfactory.vendingmachine.model.Role;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import com.mvpfactory.vendingmachine.repository.mapper.UserMapper;
import com.mvpfactory.vendingmachine.model.User;
import com.mvpfactory.vendingmachine.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private List<Integer> allowedCoins;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       @Value("#{'${allowed_coins}'.split(',')}") List<Integer> allowedCoins) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.allowedCoins = allowedCoins;
    }

    public User findUser(String username) {
        log.info("Trying to find user {}", username);
        UserEntity userEntity = getUserIfExists(username);
        return userMapper.map(userEntity);
    }

    public User updateUser(User user) {
        log.info("Trying to update user {}", user.getUsername());
        getUserIfExists(user.getUsername());
        if(user.getDeposit() < 0) {
            throw new UserDetailsException(String.format("Username %s cannot have negative deposit", user.getUsername()));
        }
        return userMapper.map(
                userRepository.save(
                        userMapper.mapForUpdate(user, Timestamp.from(Instant.now()))
                )
        );
    }

    public User registerUser(User user) {
        UserEntity insertedUser = userRepository.save(userMapper.mapForInsertion(user, Timestamp.from(Instant.now())));
        userRepository.resetDeposit(insertedUser.getId());
        return userMapper.map(userRepository.getById(insertedUser.getId()));
    }
    public void deleteUser(String username) {
        log.info("Trying to delete user {}", username);
        UserEntity userEntity = getUserIfExists(username);
        userRepository.delete(userEntity);
    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream().map(userMapper::map).collect(Collectors.toList());
    }

    public User deposit(String username, DepositRequest depositRequest) {
        log.info("Trying to deposit amount {} for user {}", depositRequest.getCoin(), username);
        UserEntity userEntity = getUserIfExists(username);
        checkIfUserHasTheCorrectRole(userEntity);

        if(!allowedCoins.contains(depositRequest.getCoin())) {
            throw new DepositException(String.format("Coin %s is not in the allowed list for username %s", depositRequest.getCoin(), username));
        }
        userRepository.deposit(userEntity.getId(), depositRequest.getCoin());
        return userMapper.map(userRepository.getById(userEntity.getId()));
    }

    public void resetDeposit(String username) {
        log.info("Trying to reset deposit for user {}", username);
        UserEntity userEntity = getUserIfExists(username);
        checkIfUserHasTheCorrectRole(userEntity);
        userRepository.resetDeposit(userEntity.getId());
    }

    private UserEntity getUserIfExists(String username) {
        log.info("Checking if username exists {}", username);
        Optional<UserEntity> userEntity = userRepository.findUserEntityByUsername(username);
        if(userEntity.isEmpty()) {
            throw new UserNotFoundException(String.format("Username %s not found", username));
        }
        return userEntity.get();
    }

    private void checkIfUserHasTheCorrectRole(UserEntity userEntity) {
        log.info("Checking if username {} has the correct role", userEntity.getUsername());
        if(userEntity.getRole() != Role.BUYER) {
            throw new OperationNotAllowedException(String.format("Username %s does not have the appropriate role", userEntity.getUsername()));
        }
    }
}