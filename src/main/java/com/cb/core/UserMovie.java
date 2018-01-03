package com.cb.core;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.cb.core.Movie.Type;

@Entity
@Table(name="usermovie")
@AssociationOverrides({
    @AssociationOverride(name = "id.user", 
        joinColumns = @JoinColumn(name = "iduser")),
    @AssociationOverride(name = "id.movie", 
        joinColumns = @JoinColumn(name = "idmovie")) })
public class UserMovie {

	@EmbeddedId
	private UserMoviePK id;
	
	@Column(name = "date", nullable = false)
    private Date date = Calendar.getInstance().getTime();
	
	@Column(name = "days", nullable = false)
    private Integer days;
	
	public UserMovie()
	{
		
	}
	
	public UserMovie(UserMoviePK id, Date date, Integer days)
	{
		this.id = id;
		this.date = date;
		this.days = days;
	}

	public UserMoviePK getId() {
		return id;
	}

	public void setId(UserMoviePK id) {
		this.id = id;
	}
	
	public Movie getMovie() {
		return id.movie;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDateString(String date)
	{
		try {
			this.date = new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String getDateString()
	{
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}
	
	private Double pricePvt(long days)
	{
		double price = Movie.basic; 
		switch(Type.valueOf(getMovie().getType()))
		{
			case NEW:
				price = Movie.premium * days;
				break;
			case REG:
				price = days > 3?price + ((days-3)*price):price;
				break;
			case OLD:
				price = days > 5?price + ((days-5)*price):price;
				break;
		}
		return price;
	}
	public String getPrice()
	{
		return String.valueOf(pricePvt(days));
	}
	
	public long getDaysDiff()
	{
		long daysDiff = TimeUnit.DAYS.convert(Calendar.getInstance().getTime().getTime() - date.getTime(), TimeUnit.MILLISECONDS);
		daysDiff -= days;
		return daysDiff>0?daysDiff:0;
	}
	
	public String getDelayPrice()
	{
		long daysDiff = getDaysDiff();
		return daysDiff>0?String.valueOf((pricePvt(daysDiff+days) - pricePvt(days))): null;
	}

	public void setDate(Date year) {
		this.date = year;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserMovie other = (UserMovie) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
