package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {

	
	public List<Movie> findByYearOrderByTitleAsc(Integer year);
	public List<Movie> findByGenreOrderByTitleAsc(String genre);
	public List<Movie> findByYearAndGenreOrderByTitleAsc(Integer year,String genre);
	
	
	public List<Movie> findAllByOrderByTitle();
	public List<Movie> findAllByOrderByTitleAsc();
	public List<Movie> findAllByOrderByTitleDesc();
	public List<Movie> findAllByOrderByScoreDesc();
	public boolean existsByTitleAndYear(String title, Integer year);
	
	
	
}