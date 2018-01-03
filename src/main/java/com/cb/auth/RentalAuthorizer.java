package com.cb.auth;

import com.cb.core.User;

import io.dropwizard.auth.Authorizer;

public class RentalAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user.getRole().equals(role);
    }
}
