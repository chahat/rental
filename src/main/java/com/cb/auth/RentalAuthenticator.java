package com.cb.auth;

import java.util.Optional;
import java.util.Set;

import com.cb.core.User;
import com.google.common.collect.ImmutableSet;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class RentalAuthenticator implements Authenticator<BasicCredentials, User> {

	private static final Set<String> VALID_USERS = ImmutableSet.of("guest","admin");
	
	@Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
		if (VALID_USERS.contains(credentials.getUsername()) && credentials.getUsername().equals(credentials.getPassword())) {
			User user = new User(credentials.getUsername());
			user.setRole(credentials.getUsername().equalsIgnoreCase(User.Role.ADMIN.name())?User.Role.ADMIN.name():User.Role.GUEST.name());
            return Optional.of(user);
        }
		return Optional.empty();
    }
}
