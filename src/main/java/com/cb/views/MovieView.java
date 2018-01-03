package com.cb.views;


import com.cb.core.Movie;

import io.dropwizard.views.View;

public class MovieView extends View {
    private final Movie movie;

    public MovieView(Movie movie) {
        super("movie.mustache");
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }
}
