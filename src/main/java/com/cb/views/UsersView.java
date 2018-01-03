package com.cb.views;


import java.util.List;

import com.cb.core.User;

import io.dropwizard.views.View;

public class UsersView extends View {
    private List<User> users;

    public UsersView() {
        super("");
    }
    
    public UsersView(List<User> users) {
        super("users.mustache");
        this.users = users;
    }

	public List<User> getUsers() {
		return users;
	}

}
