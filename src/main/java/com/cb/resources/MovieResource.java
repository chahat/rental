package com.cb.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cb.core.Movie;
import com.cb.core.User;
import com.cb.db.MovieDAO;
import com.cb.views.MovieView;
import com.cb.views.MoviesView;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;

@Path("/")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class MovieResource {

    private final MovieDAO movieDAO;

    public MovieResource(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    @GET
    @UnitOfWork
    public MoviesView listMovie() {
        return new MoviesView(movieDAO.findAll(), null);
    }
    
    @GET
    @Path("/movie")
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public MoviesView listMovie(@Auth User user) {
        return new MoviesView(movieDAO.findAll(), user);
    }
    
    @POST
    @Path("/movie")
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public Movie createMovie(Movie movie) {
        return movieDAO.create(movie);
    }

    @GET
    @Path("/movie/{movieId}")
    @UnitOfWork
    @RolesAllowed({"ADMIN", "GUEST"})
    public MovieView getMovie(@PathParam("movieId") IntParam movieId) {
    	return new MovieView(movieId.get().equals(0)?new Movie():movieDAO.findById(movieId.get()).orElseThrow(() -> new NotFoundException("No such movie.")));
    }
    
    @DELETE
    @Path("/movie/{movieId}")
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public void deleteMovie(@PathParam("movieId") IntParam movieId) {
    	movieDAO.delete(movieDAO.findById(movieId.get()).orElseThrow(() -> new NotFoundException("No such movie.")));
    }
}
