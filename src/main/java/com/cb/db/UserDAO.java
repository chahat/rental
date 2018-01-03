package com.cb.db;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.cb.core.User;
import com.cb.resources.MovieResource;
import com.cb.views.MovieView;

import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.jersey.params.IntParam;

public class UserDAO extends AbstractDAO<User> {
    
	public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> findById(Integer id, boolean init) {
    	User user = get(id);
    	if(user != null && init)
    	{
    		user.getUserMovies().size();
    	}
        return Optional.ofNullable(user);
    }
    
    public Optional<User> findByFullName(String name) {
        return Optional.ofNullable(uniqueResult(criteria().add(Restrictions.eq("fullName", name))));
    }

    public User create(User user) {
        return persist(user);
    }
    
    public void delete(User user) {
        currentSession().delete(user);
    }

    public List<User> findAll() {
        return criteria().list();
    }
    
    public MovieView findMovieById(Integer id)
    {
    	MovieDAO dao = new MovieDAO(currentSession().getSessionFactory());
		MovieResource ur = new UnitOfWorkAwareProxyFactory("hibernate", currentSession().getSessionFactory())
	               .create(MovieResource.class, MovieDAO.class, dao);
		return ur.getMovie(new IntParam(id.toString()));
    }
}
