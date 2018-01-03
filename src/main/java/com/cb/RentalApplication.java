package com.cb;

import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.cb.auth.RentalAuthenticator;
import com.cb.auth.RentalAuthorizer;
import com.cb.core.Movie;
import com.cb.core.Template;
import com.cb.core.User;
import com.cb.core.UserMovie;
import com.cb.db.MovieDAO;
import com.cb.db.UserDAO;
import com.cb.health.TemplateHealthCheck;
import com.cb.resources.MovieResource;
import com.cb.resources.UserResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class RentalApplication extends Application<RentalConfiguration> {

	private final HibernateBundle<RentalConfiguration> hibernateBundle =
            new HibernateBundle<RentalConfiguration>(User.class, Movie.class, UserMovie.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(RentalConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };
            
    public static void main(final String[] args) throws Exception {
        new RentalApplication().run(args);
    }
    
    @Override
    public String getName() {
        return "Rental";
    }

    @Override
    public void initialize(final Bootstrap<RentalConfiguration> bootstrap) {
    	bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<RentalConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(RentalConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<RentalConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(RentalConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(final RentalConfiguration configuration,
                    final Environment environment) {
    	
        final Template template = configuration.buildTemplate();
        final UserDAO userDao = new UserDAO(hibernateBundle.getSessionFactory());
        final MovieDAO movieDao = new MovieDAO(hibernateBundle.getSessionFactory());
        
        environment.healthChecks().register("template", new TemplateHealthCheck(template));
//        environment.jersey().register(DateRequiredFeature.class);
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new RentalAuthenticator())
                .setAuthorizer(new RentalAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        
        environment.jersey().register(new UserResource(userDao));
        environment.jersey().register(new MovieResource(movieDao));
        
        environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
			
			@Override
			public void serverStarted(Server arg0) {
				
//				User guest = new User("guest");
//				Movie movie = new Movie();
//				movie.setId(1);
//				guest.getUserMovies().add(new UserMovie(new UserMoviePK(guest, movie), new Date(), 2));
//				UserDAO userDaoN = new UserDAO(hibernateBundle.getSessionFactory());
//				UserResource ur = new UnitOfWorkAwareProxyFactory(hibernateBundle)
//			               .create(UserResource.class, UserDAO.class, userDaoN);
//				ur.createUser(guest);
			}
		});
    }

}
