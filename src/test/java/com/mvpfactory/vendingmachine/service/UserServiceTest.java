package com.mvpfactory.vendingmachine.service;

import com.mvpfactory.vendingmachine.error.model.DepositException;
import com.mvpfactory.vendingmachine.error.model.UserAlreadyExistsException;
import com.mvpfactory.vendingmachine.error.model.UserDetailsException;
import com.mvpfactory.vendingmachine.error.model.UserNotFoundException;
import com.mvpfactory.vendingmachine.model.DepositRequest;
import com.mvpfactory.vendingmachine.model.Role;
import com.mvpfactory.vendingmachine.model.User;
import com.mvpfactory.vendingmachine.repository.UserRepository;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import com.mvpfactory.vendingmachine.repository.mapper.UserMapper;
import com.mvpfactory.vendingmachine.security.AuthUserService;
import com.mvpfactory.vendingmachine.security.model.AuthUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthUserService authUserService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<Integer> allowedCoins;

    @Mock
    private AuthUserDetails loggedInUser;

    @Mock
    private UserEntity userEntity;

    @InjectMocks
    private UserService userService;

    private static final UserEntity EXISTING_USER_ENTITY = new UserEntity();
    private static final User EXISTING_USER = new User();
    private static final String EXISTING_USERNAME = "user1";
    private static final String PASSWORD = "pas123";
    private static final Integer DEPOSIT = 5;
    private static final Integer INVALID_COIN = 12;
    private static final Integer VALID_COIN = 25;
    private static final Role USER_ROLE = Role.BUYER;
    private static final String NON_EXISTING_USERNAME = "user2";
    private static final UUID USER_ID = UUID.randomUUID();

    private User buildUser(String username, String password, Integer deposit, Role role) {
        return User.builder()
                .username(username)
                .password(password)
                .deposit(deposit)
                .role(role)
                .build();
    }

    @Test
    public void givenExistingUser_thenReturnUser() {
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(EXISTING_USER_ENTITY));
        when(userMapper.map(EXISTING_USER_ENTITY)).thenReturn(EXISTING_USER);

        User result = userService.findUser(EXISTING_USERNAME);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(EXISTING_USER);

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper).map(EXISTING_USER_ENTITY);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    public void givenNonExistingUser_thenFindUserThrowsUserNotFoundException() {

        when(userRepository.findUserEntityByUsername(NON_EXISTING_USERNAME)).thenReturn(Optional.empty());

        UserNotFoundException result = assertThrows(UserNotFoundException.class, () -> userService.findUser(NON_EXISTING_USERNAME));

        assertThat(result).isNotNull();
        assertThat(result).hasMessage(String.format("Username %s not found", NON_EXISTING_USERNAME));

        verify(userRepository).findUserEntityByUsername(NON_EXISTING_USERNAME);
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userMapper);
    }

    @Test
    public void givenExistingUser_updateUserWithNullDeposit_thenThrowUserDetailsException() {

        User user = buildUser(EXISTING_USERNAME, PASSWORD, null, USER_ROLE);
        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(EXISTING_USER_ENTITY));

        UserDetailsException result = assertThrows(UserDetailsException.class, () -> userService.updateUser(user));

        assertThat(result).isNotNull();
        assertThat(result).hasMessage(String.format("Cannot update deposit for user %s. Please use endpoint /deposit", user.getUsername()));

        verify(authUserService).getLoggedInUser();
        verifyNoMoreInteractions(authUserService);

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userMapper);
    }

    @Test
    public void givenExistingUser_whenTryingToChangeDepositValue_thenThrowUserDetailsException() {

        User user = buildUser(EXISTING_USERNAME, PASSWORD, DEPOSIT, USER_ROLE);
        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(EXISTING_USER_ENTITY));

        UserDetailsException result = assertThrows(UserDetailsException.class, () -> userService.updateUser(user));

        assertThat(result).isNotNull();
        assertThat(result).hasMessage(String.format("Cannot update deposit for user %s. Please use endpoint /deposit", user.getUsername()));

        verify(authUserService).getLoggedInUser();
        verifyNoMoreInteractions(authUserService);

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userMapper);
    }

    @Test
    public void givenExistingUser_whenTryingToUpdateItCorrectly_thenUpdateUser() {

        User user = buildUser(EXISTING_USERNAME, PASSWORD, DEPOSIT, USER_ROLE);
        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(userEntity));
        when(userEntity.getDeposit()).thenReturn(DEPOSIT);
        when(userEntity.getId()).thenReturn(USER_ID);
        when(userMapper.mapForUpdate(any(), any())).thenReturn(userEntity);
        doNothing().when(userEntity).setInsertedDate(any());
        doNothing().when(userEntity).setId(any());
        when(loggedInUser.getAuthorities()).thenReturn(new ArrayList<>());
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        doNothing().when(authUserService).logOutUser();
        when(userMapper.map(userEntity)).thenReturn(user);

        User result = userService.updateUser(user);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(user);

        verify(authUserService).getLoggedInUser();
        verify(authUserService).logOutUser();
        verifyNoMoreInteractions(authUserService);

        verify(userRepository, times(2)).findUserEntityByUsername(EXISTING_USERNAME);
        verify(userRepository).save(userEntity);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper).mapForUpdate(any(), any());
        verify(userMapper).map(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    public void registerUserThatAlreadyExists_thenThrowUserAlreadyExistsException() {

        User user = buildUser(EXISTING_USERNAME, PASSWORD, DEPOSIT, USER_ROLE);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(EXISTING_USER_ENTITY));

        UserAlreadyExistsException result = assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));

        assertThat(result).isNotNull();
        assertThat(result).hasMessage(String.format("Username %s already exists", EXISTING_USERNAME));

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(userMapper);
    }

    @Test
    public void registerNewUser() {

        User user = buildUser(EXISTING_USERNAME, PASSWORD, DEPOSIT, USER_ROLE);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.empty());
        when(userMapper.mapForInsertion(any(), any())).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userEntity.getId()).thenReturn(USER_ID);
        when(userRepository.getById(USER_ID)).thenReturn(userEntity);
        when(userMapper.map(userEntity)).thenReturn(user);

        User result = userService.registerUser(user);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(user);

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verify(userRepository).save(userEntity);
        verify(userRepository).getById(USER_ID);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper).mapForInsertion(any(), any());
        verify(userMapper).map(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    public void deleteUser() {

        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(userEntity));
        doNothing().when(userRepository).delete(userEntity);
        doNothing().when(authUserService).logOutUser();

        userService.deleteUser();

        verify(authUserService).getLoggedInUser();
        verify(authUserService).logOutUser();
        verifyNoMoreInteractions(authUserService);

        verify(loggedInUser, times(2)).getUsername();
        verifyNoMoreInteractions(loggedInUser);

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verify(userRepository).delete(userEntity);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getAllUsers() {

        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.map(userEntity)).thenReturn(EXISTING_USER);

        List<User> result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(EXISTING_USER);

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);

        verify(userMapper).map(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    public void depositInvalidCoin_thenThrowDepositException() {

        DepositRequest request = new DepositRequest(INVALID_COIN);
        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(userEntity));
        when(allowedCoins.contains(INVALID_COIN)).thenReturn(false);

        DepositException result = assertThrows(DepositException.class, () -> userService.deposit(request));

        assertThat(result).isNotNull();
        assertThat(result).hasMessage(String.format("Coin %s is not in the allowed list of %s", INVALID_COIN, allowedCoins));

        verify(authUserService).getLoggedInUser();
        verifyNoMoreInteractions(authUserService);

        verify(loggedInUser, times(2)).getUsername();
        verifyNoMoreInteractions(loggedInUser);

        verify(userRepository).findUserEntityByUsername(EXISTING_USERNAME);
        verifyNoMoreInteractions(userRepository);

        verify(allowedCoins).contains(INVALID_COIN);
        verifyNoMoreInteractions(allowedCoins);
    }

    @Test
    public void depositValidCoin() {

        DepositRequest request = new DepositRequest(VALID_COIN);
        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(userEntity));
        when(allowedCoins.contains(VALID_COIN)).thenReturn(true);
        when(userEntity.getId()).thenReturn(USER_ID);
        doNothing().when(userRepository).deposit(USER_ID, VALID_COIN);
        when(userMapper.map(userEntity)).thenReturn(EXISTING_USER);

        User result = userService.deposit(request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(EXISTING_USER);

        verify(authUserService).getLoggedInUser();
        verifyNoMoreInteractions(authUserService);

        verify(loggedInUser, times(3)).getUsername();
        verifyNoMoreInteractions(loggedInUser);

        verify(userRepository, times(2)).findUserEntityByUsername(EXISTING_USERNAME);
        verify(userRepository).deposit(USER_ID, VALID_COIN);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper).map(userEntity);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    public void resetDeposit() {

        when(authUserService.getLoggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUsername()).thenReturn(EXISTING_USERNAME);
        when(userRepository.findUserEntityByUsername(EXISTING_USERNAME)).thenReturn(Optional.of(userEntity));
        when(userEntity.getId()).thenReturn(USER_ID);
        doNothing().when(userRepository).resetDeposit(USER_ID);
        when(userMapper.map(userEntity)).thenReturn(EXISTING_USER);

        User result = userService.resetDeposit();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(EXISTING_USER);

        verify(authUserService).getLoggedInUser();
        verifyNoMoreInteractions(authUserService);

        verify(loggedInUser, times(3)).getUsername();
        verifyNoMoreInteractions(loggedInUser);

        verify(userRepository, times(2)).findUserEntityByUsername(EXISTING_USERNAME);
        verify(userRepository).resetDeposit(USER_ID);
        verifyNoMoreInteractions(userRepository);
    }
}