package it.uniroma3.siw.model;

import java.awt.Image;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.Hibernate;

/*si rende persistente la classe Movie*/
@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)

	private Long id;
	@NotNull
	@NotBlank 
	@Size(max = 255)
	private String title;
	
	@Min(1900)
	@NotNull
	private Integer year;

	@Lob
	@Column(name = "image")
	private byte[] image;

	
	@ManyToOne
	private Artist artist_asdirector; // regista

	@OneToMany(mappedBy = "movie")
	private List<Review> movieReviews;

	/* associazione Molti a Molti tra Movie ed Artist con "actors" */

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Artist> actors; /* restituisce la lista degli attori nel film */

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<User> users; /* restituisce la lista degli utenti che si sono salvati questo film */

	private String plot;

	private Boolean isOnNetflix = false;

	private Boolean isOnPrimeVideo = false;

	private Boolean isOnDisneyPlus = false;

	private String genre;

	private float score;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public Artist getArtist_asdirector() {
		return artist_asdirector;
	}

	public void setArtist_asdirector(Artist artist_asdirector) {
		this.artist_asdirector = artist_asdirector;
	}

	public List<Review> getMovieReviews() {
		return this.movieReviews;
	}

	public void setMovieReviews(List<Review> movieReviews) {
		this.movieReviews = movieReviews;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(title, other.title) && year.equals(other.year);
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public Boolean getIsOnNetflix() {
		return isOnNetflix;
	}

	public void setIsOnNetflix(Boolean isOnNetflix) {
		this.isOnNetflix = isOnNetflix;
	}

	public Boolean getIsOnPrimeVideo() {
		return isOnPrimeVideo;
	}

	public void setIsOnPrimeVideo(Boolean isOnPrimeVideo) {
		this.isOnPrimeVideo = isOnPrimeVideo;
	}

	public Boolean getIsOnDisneyPlus() {
		return isOnDisneyPlus;
	}

	public void setIsOnDisneyPlus(Boolean isOnDisneyPlus) {
		this.isOnDisneyPlus = isOnDisneyPlus;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Set<Artist> getActors() {
		Hibernate.initialize(actors);
		return actors;
	}

	public void setActors(Set<Artist> actors) {
		this.actors = actors;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
