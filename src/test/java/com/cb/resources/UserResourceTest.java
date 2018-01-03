package com.cb.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.cb.core.User;
import com.cb.db.UserDAO;
import com.cb.views.UserView;

import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * Unit tests for {@link PersonResource}.
 */
public class UserResourceTest {
    private static final UserDAO DAO = mock(UserDAO.class);
    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .addResource(new UserResource(DAO))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();
    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setId(1);
    }

    @After
    public void tearDown() {
        reset(DAO);
    }

    @Test
    public void getPersonSuccess() {
        when(DAO.findById(1,true)).thenReturn(Optional.of(user));
        UserView found = RULE.target("/user/1").request().get(UserView.class);
        assertThat(found.getUser().getId()).isEqualTo(user.getId());
        verify(DAO).findById(1, true);
    }

    @Test
    public void getPersonNotFound() {
        when(DAO.findById(2, true)).thenReturn(Optional.empty());
        final Response response = RULE.target("/user/2").request().get();

        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(DAO).findById(2, true);
    }
}
