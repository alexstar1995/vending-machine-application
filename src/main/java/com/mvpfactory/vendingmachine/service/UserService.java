package com.mvpfactory.vendingmachine.service;

import com.mvpfactory.vendingmachine.error.model.*;
import com.mvpfactory.vendingmachine.model.DepositRequest;

import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import com.mvpfactory.vendingmachine.repository.mapper.UserMapper;
import com.mvpfactory.vendingmachine.model.User;
import com.mvpfactory.vendingmachine.repository.UserRepository;
import com.mvpfactory.vendingmachine.security.AuthUserService;
import com.mvpfactory.vendingmachine.security.model.AuthUserDetails;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final AuthUserService authUserService;
    private final UserMapper userMapper;
    private final List<Integer> allowedCoins;

    @Autowired
    public UserService(UserRepository userRepository, AuthUserService authUserService, UserMapper userMapper,
                       @Value("#{'${allowed_coins}'.split(',')}") List<Integer> allowedCoins) {
        this.userRepository = userRepository;
        this.authUserService = authUserService;
        this.userMapper = userMapper;
        this.allowedCoins = allowedCoins;
    }

    public User findUser(String username) {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByUsername(username);
        if(userEntity.isEmpty()) {
            throw new UserNotFoundException(String.format("Username %s not found", username));
        }
        return userMapper.map(userEntity.get());
    }

    public User updateUser(User user) {

        AuthUserDetails loggedInUser = authUserService.getLoggedInUser();
        log.info("Trying to update user {}", loggedInUser.getUsername());

        UserEntity existingUser = userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get();
        if(user.getDeposit() == null || !user.getDeposit().equals(existingUser.getDeposit())) {
            throw new UserDetailsException(String.format("Cannot update deposit for user %s. Please use endpoint /deposit", loggedInUser.getUsername()));
        }

        UserEntity updatedUserEntity = userMapper.mapForUpdate(user, Timestamp.from(Instant.now()));
        updatedUserEntity.setInsertedDate(existingUser.getInsertedDate());
        updatedUserEntity.setId(userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get().getId());
        if(!loggedInUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + user.getRole()))) {
            updatedUserEntity.setDeposit(0);
        }

        UserEntity savedEntity = userRepository.save(updatedUserEntity);
        authUserService.logOutUser();
        return userMapper.map(savedEntity);
    }

    public User registerUser(User user) {
        if(userRepository.findUserEntityByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(String.format("Username %s already exists", user.getUsername()));
        }
        user.setDeposit(0);
        UserEntity insertedUser = userRepository.save(userMapper.mapForInsertion(user, Timestamp.from(Instant.now())));
        return userMapper.map(userRepository.getById(insertedUser.getId()));
    }

    public void deleteUser() {
        AuthUserDetails loggedInUser = authUserService.getLoggedInUser();
        log.info("Trying to delete user {}", loggedInUser.getUsername());
        userRepository.delete(userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get());
        authUserService.logOutUser();
    }

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .collect(Collectors.toList());
    }

    public User deposit(DepositRequest depositRequest) {
        AuthUserDetails loggedInUser = authUserService.getLoggedInUser();
        log.info("Trying to deposit amount {} for user {}", depositRequest.getCoin(), loggedInUser.getUsername());
        UserEntity userEntity = userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get();

        if(!allowedCoins.contains(depositRequest.getCoin())) {
            throw new DepositException(String.format("Coin %s is not in the allowed list of %s", depositRequest.getCoin(), allowedCoins));
        }
        userRepository.deposit(userEntity.getId(), depositRequest.getCoin());
        return userMapper.map(userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get());
    }

    public User resetDeposit() {
        AuthUserDetails loggedInUser = authUserService.getLoggedInUser();
        log.info("Trying to reset deposit for user {}", loggedInUser.getUsername());
        UserEntity userEntity = userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get();
        userRepository.resetDeposit(userEntity.getId());
        return userMapper.map(userRepository.findUserEntityByUsername(loggedInUser.getUsername()).get());
    }
}