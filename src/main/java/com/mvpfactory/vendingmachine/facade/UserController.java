package com.mvpfactory.vendingmachine.facade;

import com.mvpfactory.vendingmachine.model.DepositRequest;
import com.mvpfactory.vendingmachine.model.User;
import com.mvpfactory.vendingmachine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable String username) {
        return userService.findUser(username);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public User registerUser(@RequestBody @Valid User user) {
        return userService.registerUser(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }

    @PutMapping("/{username}/deposit/reset")
    @ResponseStatus(HttpStatus.OK)
    public void resetUserDeposit(@PathVariable String username) {
        userService.resetDeposit(username);
    }

    @PutMapping("/{username}/deposit")
    @ResponseStatus(HttpStatus.OK)
    public User deposit(@PathVariable String username,
                        @RequestBody @Valid DepositRequest depositRequest) {
        return userService.deposit(username, depositRequest);
    }
}