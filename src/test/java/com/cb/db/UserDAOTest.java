package com.cb.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.cb.core.Movie;
import com.cb.core.User;
import com.cb.core.UserMovie;

import io.dropwizard.testing.junit.DAOTestRule;

public class UserDAOTest {

    @Rule
    public DAOTestRule daoTestRule = DAOTestRule.newBuilder()
        .addEntityClass(User.class)
        .addEntityClass(UserMovie.class)
        .addEntityClass(Movie.class)
        .build();

    private UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        userDAO = new UserDAO(daoTestRule.getSessionFactory());
    }

    @Test
    public void createPerson() {
        final User jeff = daoTestRule.inTransaction(() -> userDAO.create(new User("Jeff")));
        assertThat(jeff.getId()).isGreaterThan(0);
        assertThat(jeff.getFullName()).isEqualTo("Jeff");
        assertThat(jeff.getRole()).isEqualTo(User.Role.GUEST.name());
        assertThat(jeff.getPoint()).isEqualTo(0);
        assertThat(userDAO.findById(jeff.getId(), false)).isEqualTo(Optional.of(jeff));
    }
    
    @Test
    public void findAll() {
        daoTestRule.inTransaction(() -> {
            userDAO.create(new User("Jeff"));
            userDAO.create(new User("Jim"));
            userDAO.create(new User("Randy"));
        });

        final List<User> persons = userDAO.findAll();
        assertThat(persons).extracting("fullName").containsOnly("Jeff", "Jim", "Randy");
    }

    @Test(expected = ConstraintViolationException.class)
    public void handlesNullFullName() {
        daoTestRule.inTransaction(() -> userDAO.create(new User(null)));
    }
}
