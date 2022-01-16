package com.mvpfactory.vendingmachine.security;

import com.mvpfactory.vendingmachine.security.model.AuthUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthUserService {

    public void logOutUser() {
        Authentication authSession = SecurityContextHolder.getContext().getAuthentication();
        if(authSession != null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if(requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
                new SecurityContextLogoutHandler().logout(request, null, authSession);
            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public AuthUserDetails getLoggedInUser() {
       return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
