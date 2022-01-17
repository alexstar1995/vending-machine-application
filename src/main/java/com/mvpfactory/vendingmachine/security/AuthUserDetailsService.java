package com.mvpfactory.vendingmachine.security;

import com.mvpfactory.vendingmachine.repository.UserRepository;
import com.mvpfactory.vendingmachine.repository.entity.UserEntity;
import com.mvpfactory.vendingmachine.security.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> entity = userRepository.findUserEntityByUsername(username);
        entity.orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
        return new AuthUserDetails(entity.get());
    }
}
