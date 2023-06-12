package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
	
	public List<Review> findAllByOrderByDateDesc(); //mi restituisce le recensioni dal piu recente al meno.
	
	public List<Review> findByMovie(Movie movie);
	
	public List<Review> findByWriter(User writer);

}
