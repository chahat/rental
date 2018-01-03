package com.cb.core;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "movie")
public class Movie {
	
	public static enum Type {
		OLD, NEW, REG;
	}
	
	public static final Double premium = 40.0;
	public static final Double basic = 30.0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "url")
    private String url;
    
    @Column(name = "year", nullable = false)
    private Integer year;
    
    @Transient
    private Double price;
    
    @Column(name = "type", nullable = false)
    private String type = Type.NEW.name();

    public Movie() {
    }

    public Movie(String fullName) {
        this.name = fullName;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Double getPrice() {
		return type.equals(Type.NEW)?premium:basic;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonIgnore
	public boolean isNew()
	{
		return type.equals(Type.NEW.name());
	}
	
	@JsonIgnore
	public boolean isReg()
	{
		return type.equals(Type.REG.name());
	}
	
	@JsonIgnore
	public boolean isOld()
	{
		return type.equals(Type.OLD.name());
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Movie)) {
            return false;
        }

        final Movie that = (Movie) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name) && 
                Objects.equals(this.year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, year);
    }
}
