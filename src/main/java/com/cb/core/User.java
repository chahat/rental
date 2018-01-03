package com.cb.core;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User implements Principal{
	
	public static enum Role {
		GUEST, ADMIN;
	}
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "role", nullable = false)
    private String role = Role.GUEST.name();
    
    @Column(name = "point", nullable = false)
    private int point;
    
    @OneToMany(mappedBy = "id.user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMovie> userMovies = new ArrayList<>();

    public User() {
    }

    public User(String fullName) {
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String jobTitle) {
        this.role = jobTitle;
    }

    public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public List<UserMovie> getUserMovies() {
		return userMovies;
	}

	public void setUserMovies(List<UserMovie> userMovies) {
		this.userMovies = userMovies;
	}

	@JsonIgnore
	public boolean isGuest()
	{
		return role.equals(Role.GUEST.name());
	}
	
	@JsonIgnore
	public boolean isAdmin()
	{
		return role.equals(Role.ADMIN.name());
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        final User that = (User) o;

        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, role);
    }

	@Override
	@JsonIgnore
	public String getName() {
		return fullName;
	}
}
