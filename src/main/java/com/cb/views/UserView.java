package com.cb.views;


import com.cb.core.User;

import io.dropwizard.views.View;

public class UserView extends View {
    private User user;

    public UserView() {
		super("");
	}

	public UserView(User user) {
        super("user.mustache");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
