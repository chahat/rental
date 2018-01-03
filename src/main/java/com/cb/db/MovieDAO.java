package com.cb.db;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import com.cb.core.Movie;

import io.dropwizard.hibernate.AbstractDAO;

public class MovieDAO extends AbstractDAO<Movie> {
    public MovieDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Movie> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }

    public Movie create(Movie user) {
        return persist(user);
    }
    
    public void delete(Movie movie) {
        currentSession().delete(movie);
    }

    public List<Movie> findAll() {
        return criteria().list();
    }
}
