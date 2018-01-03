package com.cb.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cb.core.Movie;
import com.cb.core.User;
import com.cb.core.UserMovie;
import com.cb.core.UserMoviePK;

import io.dropwizard.testing.junit.DAOTestRule;

public class DAOTest {

    @Rule
    public DAOTestRule daoTestRule = DAOTestRule.newBuilder()
        .addEntityClass(User.class)
        .addEntityClass(UserMovie.class)
        .addEntityClass(Movie.class)
        .build();

    private UserDAO userDAO;
    private MovieDAO movieDAO;
    private User jeff;
    private Movie newMovie, oldMovie, regMovie;

    @Before
    public void setUp() throws Exception {
        userDAO = new UserDAO(daoTestRule.getSessionFactory());
        movieDAO = new MovieDAO(daoTestRule.getSessionFactory());
        jeff = daoTestRule.inTransaction(() -> userDAO.create(new User("Jeff")));
        newMovie = new Movie("Matrix 2");
    	newMovie.setType(Movie.Type.NEW.name());
    	newMovie.setYear(1999);
    	regMovie = new Movie("Spider Man 2");
    	regMovie.setType(Movie.Type.REG.name());
    	regMovie.setYear(1985);
    	oldMovie = new Movie("Out of Africa");
    	oldMovie.setYear(1970);
    	oldMovie.setType(Movie.Type.OLD.name());
        newMovie = daoTestRule.inTransaction(() -> movieDAO.create(newMovie));
        regMovie = daoTestRule.inTransaction(() -> movieDAO.create(regMovie));
        oldMovie = daoTestRule.inTransaction(() -> movieDAO.create(oldMovie));
    }

    @Test
    public void createMovie() {
        assertThat(oldMovie.getId()).isGreaterThan(0);
        assertThat(regMovie.getId()).isGreaterThan(0);
        assertThat(newMovie.getId()).isGreaterThan(0);
        assertThat(oldMovie.getType()).isEqualTo(Movie.Type.OLD.name());
        assertThat(regMovie.getType()).isEqualTo(Movie.Type.REG.name());
        assertThat(newMovie.getType()).isEqualTo(Movie.Type.NEW.name());
        assertThat(movieDAO.findById(oldMovie.getId())).isEqualTo(Optional.of(oldMovie));
        assertThat(movieDAO.findById(regMovie.getId())).isEqualTo(Optional.of(regMovie));
        assertThat(movieDAO.findById(newMovie.getId())).isEqualTo(Optional.of(newMovie));
    }

    @Test
    public void testPriceNew() {
    	int days = 1;
    	UserMovie jeffNew = new UserMovie(new UserMoviePK(jeff, newMovie), Calendar.getInstance().getTime(), days);
    	jeff.getUserMovies().add(jeffNew);
    	final User jeffNewMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffNewMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getPrice()).isEqualTo(String.valueOf(new Double(40)));
    }
    
    @Test
    public void testPriceReg() {
    	int days = 2;
    	UserMovie regNew = new UserMovie(new UserMoviePK(jeff, regMovie), Calendar.getInstance().getTime(), days);
    	jeff.getUserMovies().add(regNew);
    	final User jeffRegMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffRegMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getPrice()).isEqualTo(String.valueOf(new Double(30)));
    }
    
    @Test
    public void testPriceReg2() {
    	int days = 5;
    	UserMovie jeffReg = new UserMovie(new UserMoviePK(jeff, regMovie), Calendar.getInstance().getTime(), days);
    	jeff.getUserMovies().add(jeffReg);
    	final User jeffRegMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffRegMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getPrice()).isEqualTo(String.valueOf(new Double(90)));
    }
    
    @Test
    public void testPriceOld() {
    	int days = 7;
    	UserMovie jeffOld = new UserMovie(new UserMoviePK(jeff, oldMovie), Calendar.getInstance().getTime(), days);
    	jeff.getUserMovies().add(jeffOld);
    	final User jeffOldMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffOldMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getPrice()).isEqualTo(String.valueOf(new Double(90)));
    }
    
    @Test
    public void testPriceOld2() {
    	int days = 7;
    	UserMovie jeffOld = new UserMovie(new UserMoviePK(jeff, oldMovie), Calendar.getInstance().getTime(), days);
    	jeff.getUserMovies().add(jeffOld);
    	final User jeffOldMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffOldMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getPrice()).isEqualTo(String.valueOf(Movie.basic*3));
    }
    
    @Test
    public void testPriceNewLate() {
    	int days = 1;
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, -3);
    	UserMovie jeffNew = new UserMovie(new UserMoviePK(jeff, newMovie),cal.getTime() , days);
    	jeff.getUserMovies().add(jeffNew);
    	final User jeffNewMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffNewMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getDelayPrice()).isEqualTo(String.valueOf(new Double(80)));
    }
    
    @Test
    public void testPriceRegLate() {
    	int days = 3;
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, -4);
    	UserMovie jeffNew = new UserMovie(new UserMoviePK(jeff, regMovie),cal.getTime() , days);
    	jeff.getUserMovies().add(jeffNew);
    	final User jeffNewMovie = daoTestRule.inTransaction(() -> userDAO.create(jeff));
    	assertThat(jeffNewMovie.getUserMovies().size()).isEqualTo(1);
    	assertThat(jeff.getUserMovies().get(0).getDelayPrice()).isEqualTo(String.valueOf(new Double(30)));
    }
}
