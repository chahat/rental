package com.cb.views;


import java.util.List;

import com.cb.core.Movie;
import com.cb.core.User;
import com.cb.core.User.Role;

import io.dropwizard.views.View;

public class MoviesView extends View {
    private List<Movie> movies;

    public MoviesView() {
		super("");
	}

	public MoviesView(List<Movie> movies, User user) {
        super(user != null && user.getRole().equals(Role.ADMIN.name())?"moviesadmin.mustache":"movies.mustache");
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
