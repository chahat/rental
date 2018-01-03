package com.cb.resources;

import java.util.Iterator;
import java.util.Optional;

import javax.annotation.security.DenyAll;
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
import com.cb.core.UserMovie;
import com.cb.core.UserMoviePK;
import com.cb.db.UserDAO;
import com.cb.dto.UserMovieDTO;
import com.cb.views.MovieView;
import com.cb.views.UserView;
import com.cb.views.UsersView;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.IntParam;

@Path("/user")
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
public class UserResource {

    private UserDAO userDAO;

    public UserResource()
    {
    	
    }
    
    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public UsersView listUser() {
        return new UsersView(userDAO.findAll());
    }
    
    @POST
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public User createUser(User user) {
        return userDAO.create(user);
    }
    
    @GET
    @UnitOfWork
    @RolesAllowed({"GUEST", "ADMIN"})
    @Path("/{userId}")
    public UserView getPerson(@PathParam("userId") IntParam userId) {
    	return new UserView(userId.get().equals(0)?new User():userDAO.findById(userId.get(), true).orElseThrow(() -> new NotFoundException("No such user.")));
    }
    
    @DELETE
    @Path("/{userId}")
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public void deleteUser(@PathParam("userId") IntParam userId) {
    	userDAO.delete(userDAO.findById(userId.get(), false).orElseThrow(() -> new NotFoundException("No such user.")));
    }
    
    @POST
    @UnitOfWork
    @RolesAllowed("ADMIN")
    @Path("/{userId}")
    public User postUserMovie(@PathParam("userId") IntParam userId, UserMovieDTO userMovieDTO) {
    	User user = userDAO.findById(userId.get(), false).orElseThrow(() -> new NotFoundException("No such user."));
    	MovieView movieView = userDAO.findMovieById(userMovieDTO.getId());
    	Movie movie = movieView.getMovie();
    	User userClone = new User();
    	userClone.setId(user.getId());
    	Movie movieClone = new Movie();
    	movieClone.setId(userMovieDTO.getId());
    	UserMovie userMovie = new UserMovie();
    	userMovie.setId(new UserMoviePK(userClone, movieClone));
    	userMovie.setDateString(userMovieDTO.getDateString());
    	userMovie.setDays(userMovieDTO.getDays());
    	user.getUserMovies().add(userMovie);
    	user.setPoint(user.getPoint()+(movie.isNew()?2:1));
    	return user;
    }
    
    @DELETE
    @Path("/{userId}/{movieId}")
    @UnitOfWork
    @RolesAllowed("ADMIN")
    public void deleteUserMovie(@PathParam("userId") IntParam userId, @PathParam("movieId") IntParam movieId) {
    	User user = userDAO.findById(userId.get(), false).orElseThrow(() -> new NotFoundException("No such user."));
    	Iterator<UserMovie> userMovieIter = user.getUserMovies().iterator();
    	while(userMovieIter.hasNext())
    	{
    		UserMovie userMovie = userMovieIter.next();
    		if(userMovie.getMovie().getId().equals(movieId.get()))
    		{
    			userMovieIter.remove();
    			break;
    		}
    	}
    }

    // function used for authentication
    @DenyAll
    @UnitOfWork
	public Optional<User> findByFullName(String userName) {
		return userDAO.findByFullName(userName);
	}
}
