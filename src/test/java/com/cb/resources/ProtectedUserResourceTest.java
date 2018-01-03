package com.cb.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.cb.auth.RentalAuthenticator;
import com.cb.auth.RentalAuthorizer;
import com.cb.core.User;
import com.cb.db.UserDAO;
import com.cb.views.UserView;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;

public final class ProtectedUserResourceTest {

	private static final UserDAO DAO = mock(UserDAO.class);
    private static final BasicCredentialAuthFilter<User> BASIC_AUTH_HANDLER =
        new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(new RentalAuthenticator())
            .setAuthorizer(new RentalAuthorizer())
            .setPrefix("Basic")
            .setRealm("SUPER SECRET STUFF")
            .buildAuthFilter();

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
        .addProvider(RolesAllowedDynamicFeature.class)
        .addProvider(new AuthDynamicFeature(BASIC_AUTH_HANDLER))
        .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
        .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
        .addResource(new UserResource(DAO))
        .build();

    private User user = null;

    @Before
    public void setUp() {
    	user = new User();
    	user.setId(1);
    	user.setFullName("admin");
    	user.setRole(User.Role.ADMIN.name());
    }

    @After
    public void tearDown() {
        reset(DAO);
    }
    
    @Test
    public void testProtectedAdminEndpoint() {
    	when(DAO.create(user)).thenReturn(user);
    	User userN = RULE.target("/user").request(MediaType.APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE))
            .readEntity(User.class);
        assertThat(userN.getId()).isEqualTo(user.getId());
    }

    @Test
    public void testProtectedBasicUserEndpoint() {
    	when(DAO.findById(1, true)).thenReturn(Optional.of(user));
        UserView userView = RULE.target("/user/1").request(MediaType.APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, "Basic Z3Vlc3Q6Z3Vlc3Q=")
            .get(UserView.class);
        assertThat(userView.getUser().getId()).isEqualTo(1);
    }

    @Test
    public void testProtectedBasicUserEndpointPrincipalIsNotAuthorized401() {
        try {
            RULE.target("/user").request()
            .get(String.class);
            failBecauseExceptionWasNotThrown(NotAuthorizedException.class);
        } catch (NotAuthorizedException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(401);
        }
    }
    
    @Test
    public void testProtectedBasicUserEndpointPrincipalIsNotAuthorized403() {
        try {
            RULE.target("/user").request(MediaType.APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, "Basic Z3Vlc3Q6Z3Vlc3Q=")
            .get(String.class);
            failBecauseExceptionWasNotThrown(ForbiddenException.class);
        } catch (ForbiddenException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(403);
        }
    }

}
