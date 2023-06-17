package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.hibernate.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import it.uniroma3.siw.controller.validator.MovieUpdateValidator;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class MovieController 
{
	@Autowired MovieRepository movieRepository;
	@Autowired ArtistRepository artistRepository;
	@Autowired MovieValidator movieValidator;
	@Autowired MovieUpdateValidator movieUpdateValidator;
	@Autowired ReviewRepository reviewRepository;
	@Autowired UserRepository userRepository;
	@Autowired CredentialsRepository credentialsRepository;
	@Autowired
	private CredentialsService credentialsService;
	
	
	
	
	
	
	 
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}
	
	
	
	
	//RICHIESTE HTTP PER MOVIE
	
	@GetMapping("/admin/formNewMovie")
	public String formNewMovie(Model model) 
	{
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie";
	}
	
	@GetMapping("/admin/manageMovies")
	public String manageMovies(Model model) 
	{
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/manageMovies";
		
	}
	
	
	@PostMapping("/admin/movie/edit/{id}")
	public String editMovie(@Valid @ModelAttribute("movie") Movie movie,BindingResult bindingResult,@PathVariable("id") Long id,Model model,
			@RequestParam("image") MultipartFile imageProfileFile,@RequestParam("plot")String plot,@RequestParam("score")Float score,
			@RequestParam("genre")String genre, @RequestParam(name = "isOnNetflix", required = false) Boolean isOnNetflix,
			@RequestParam(name = "isOnPrimeVideo", required = false)Boolean isOnPrimeVideo, @RequestParam(name = "isOnDisneyPlus", required = false)Boolean isOnDisneyPlus) throws IOException
	{
		
		
		
		  
		  this.movieUpdateValidator.validate(movie, bindingResult);

		  if (!bindingResult.hasErrors()) 
		  {
			  movie.setScore(score);
			 
    		 if (plot != null && !plot.isEmpty()) 
    		 {
    			 movie.setPlot(plot);
    		 }
		    
    		 if (genre != null && !genre.isEmpty()) 
    		 {
    			 movie.setGenre(genre);
    		 }
		 
    		 if (isOnNetflix != null && isOnNetflix == true ) 
    		 {
    			 movie.setIsOnNetflix(true);
    		 }
    		 else
    		 {
    			 movie.setIsOnNetflix(false);
    		 }

    		 if (isOnPrimeVideo != null && isOnPrimeVideo == true ) 
    		 {
    			 movie.setIsOnPrimeVideo(true);
    		 }
    		 else
    		 {
		    	movie.setIsOnPrimeVideo(false);
    		 }

    		 if (isOnDisneyPlus != null && isOnDisneyPlus == true ) 
    		 {
    			 movie.setIsOnDisneyPlus(true);
    		 }
    		 else
    		 {
    			 movie.setIsOnDisneyPlus(false);
    		 }
    		 if (imageProfileFile != null) 
    		 {
    			 byte[] imageBytes = imageProfileFile.getBytes();
    			 // Imposta l'array di byte dell'immagine nell'oggetto Movie
				
    			 movie.setImage(imageBytes);
    		 }
		 
	
		 this.movieRepository.save(movie);
		 model.addAttribute("movie",movie);
		 return "public/movie";
    	}
    	 
    	else 
    	{
    		return "admin/formUpdateMovie";
    	}
	}
	
	@GetMapping("/admin/deleteMovie/{id}")
	public String deleteMovie(@PathVariable("id") Long id, Model model)
	{
		
		Movie movie = this.movieRepository.findById(id).get();
		
		Set<User> users = this.movieRepository.findById(id).get().getUsers();
		
		List<Review> reviews= this.reviewRepository.findByMovie(movie);
        
        
		/*Cancello prima le recensioni associate al film..*/
        for(Review review: reviews) 
        	this.reviewRepository.delete(review);
        
        /*Cancello prima le associazioni del film dei vari utenti associati al film..*/
        for(User user: users) 
        {
        	user.getMyMovies().remove(movie);
        	this.userRepository.save(user);
        }
        
        this.movieRepository.delete(movie);
		model.addAttribute("movies", this.movieRepository.findAll());
		
		return "admin/manageMovies";
	}
	

	
	
	@GetMapping("/admin/addDirectorToMovie/{id}")
	public String addDirectorToMovie(@PathVariable("id") Long id, Model model) { //lista degli attori nel film
		
		Artist regista= this.movieRepository.findById(id).get().getArtist_asdirector();
		
		
		List<Artist> artisti= (List<Artist>) this.artistRepository.findAll();
		
		artisti.remove(regista);
		
		
		model.addAttribute("movie",this.movieRepository.findById(id).get());
		model.addAttribute("artists",artisti);
		
		return "admin/directorsToAdd";
	}
	
	@GetMapping("/admin/setDirectorToMovie/{artistid}/{movieid}")
	public String setDirectorToMovie(@PathVariable("artistid") Long artistid,@PathVariable("movieid") Long movieid, Model model) { 
		
		Artist regista= this.artistRepository.findById(artistid).get();
		Movie movie = this.movieRepository.findById(movieid).get();
		movie.setArtist_asdirector(regista);
		this.movieRepository.save(movie);
		
		regista.getMovieasdirector().add(movie);
		this.artistRepository.save(regista);
		
		List<Artist> artisti= (List<Artist>) this.artistRepository.findAll();
		
		artisti.remove(regista);
		
		
		model.addAttribute("movie",this.movieRepository.findById(movieid).get());
		model.addAttribute("artists",artisti);
		
		return "admin/directorsToAdd";
	}
	
	@GetMapping("/admin/updateActorsOfMovie/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model)
	{
		
		/*a questa pagina devo restituire gli attori che posso aggiungere
		 * e quelli già presenti perchè devo poterli cancellare dalla lista dello
		 * specifico movie!*/
		
		Movie movie= this.movieRepository.findById(id).get();
		
		model.addAttribute("actorsmovie",movie.getActors());
		
		model.addAttribute("movie",movie);
		
		model.addAttribute("artists",this.artistRepository.getArtistByActorsOfmovieNotContains(movie)); 
		/*restituisco tutti gli artisti che non sono attori di questo film*/
		
		
		
		return "admin/actorsToAdd";
	}
	

	
	@GetMapping("/admin/addActorToMovie/{artistid}/{movieid}")
	public String addActorToMovie(@PathVariable("artistid") Long artistid,@PathVariable("movieid")Long movieid, Model model) 
	{
		
	        Movie movie = this.movieRepository.findById(movieid).get();
	        Artist artist = this.artistRepository.findById(artistid).get();
	        
	        Set<Artist> movieActors = movie.getActors();
	        movieActors.add(artist);
	        
	        movie.setActors(movieActors);
	        this.movieRepository.save(movie);
	        
	        
	        artist.getActorsOfmovie().add(movie);
	        this.artistRepository.save(artist);

	        
	        model.addAttribute("movie",movie);
	        model.addAttribute("actorsmovie",this.movieRepository.findById(movieid).get().getActors());
	        
	        model.addAttribute("artists",this.artistRepository.getArtistByActorsOfmovieNotContains(movie));
	        
	        return "admin/actorsToAdd";
	    }
	
	
	

	
	@GetMapping("/admin/removeActorFromMovie/{artistid}/{movieid}")
	public String removeActorFromMovie(@PathVariable("artistid") Long artistid,@PathVariable("movieid")Long movieid, Model model) {
		
	        Movie movie = this.movieRepository.findById(movieid).get();
	        Artist artist = this.artistRepository.findById(artistid).get();
	        
	        Set<Artist> movieActors = movie.getActors();
	        
	        
	        movieActors.remove(artist);
	        movie.setActors(movieActors);
	        this.movieRepository.save(movie);
	        
	        artist.getActorsOfmovie().remove(movie);
	        this.artistRepository.save(artist);

	        
	        model.addAttribute("movie",movie);
	        model.addAttribute("actorsmovie",this.movieRepository.findById(movieid).get().getActors());
	        
	        model.addAttribute("artists",this.artistRepository.getArtistByActorsOfmovieNotContains(movie));
	        
	        return "admin/actorsToAdd";
	    }
	

	
	
	

	@PostMapping("/admin/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie,
	                       BindingResult bindingResult,
	                       Model model,
	                       @RequestParam("image") MultipartFile imageFile)throws IOException{

	    this.movieValidator.validate(movie, bindingResult);

	    	if (!bindingResult.hasErrors()) 
	    	{
	    		
	    		// Ottieni l'array di byte dell'immagine dal MultipartFile
	    		byte[] imageBytes = imageFile.getBytes();
	        
	    		// Imposta l'array di byte dell'immagine nell'oggetto Movie
	    		movie.setImage(imageBytes);
	        
	    		// Salva il nuovo oggetto Movie nel database
	    		this.movieRepository.save(movie);
	    		model.addAttribute("movie", movie);
	    		return "/public/movie";
	    	}
	    	else 
	    	{
	    		return "admin/formNewMovie";
	    	}
	        
	    }
	
	@GetMapping("/image/{id}")
	@ResponseBody
	public byte[] getImage(@PathVariable("id") Long id) {
	    // Ottiene il film dal database
	    Movie movie = movieRepository.findById(id).orElse(null);
	    if (movie != null && movie.getImage() != null) {
	        // Restituisce l'immagine come array di byte
	        return movie.getImage();
	    }
	    return new byte[0];
	}
	
	
	
	@GetMapping("/admin/formUpdateMovie/{id}")
	public String getUpdateMovie(@PathVariable("id") Long id, Model model) 
	{
		
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		
		Movie movie=this.movieRepository.findById(id).get();
		Artist artist= this.movieRepository.findById(id).get().getArtist_asdirector();
		
		model.addAttribute("movie",movie);
		model.addAttribute("artist_asdirector",artist); 
        model.addAttribute("artists", movie.getArtist_asdirector());
        
		return "admin/formUpdateMovie";
	}
	
	
	
	@GetMapping("/public/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model, Authentication authentication) 
	{
		
		Movie movie= this.movieRepository.findById(id).get();
		
		
		
		List<Review>reviewAllMovie= this.reviewRepository.findByMovie(movie); /*contiene tutte le recensioni di tale film*/
		Boolean movieIsAdded = false;
		
	    if (authentication != null && authentication.isAuthenticated()) {
	    	
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
	        
	        //verifico se il film movie è nella lista dell'utente 
	        for(Movie m : credentials.getUser().getMyMovies())
	        {
	        	if( (movie.getTitle().equals(m.getTitle()) && (movie.getYear().equals(m.getYear()))))
	        	{
	        		movieIsAdded = true;
	        	}
	        }
	        
	        
	        for(Review review: reviewAllMovie)
	        {
	        	if( review.getWriter().getId().equals(credentials.getUser().getId())) 
	        	{
	        		model.addAttribute("review",review); /*recensione di tale utente su questo film*/
	        		model.addAttribute("reviews",reviewAllMovie); /*tutte le recensioni di tale film*/
	        		model.addAttribute("director",movie.getArtist_asdirector()); //gli passiamo il regista
	        		model.addAttribute("actors",movie.getActors()); //gli passiamo gli attori
	        		model.addAttribute("movieIsAdded",movieIsAdded);
	        		model.addAttribute("movie", movie);
	        		return "public/movie";
	        	}
	        }
	    }
	    
	    model.addAttribute("movieIsAdded",movieIsAdded);
		model.addAttribute("reviews",reviewAllMovie); /*tutte le recensioni di tale film*/
		model.addAttribute("director",movie.getArtist_asdirector()); //gli passiamo il regista
		model.addAttribute("actors",movie.getActors()); //gli passiamo gli attori
		model.addAttribute("numAttori",movie.getActors().size());
	    model.addAttribute("review",null);
	    model.addAttribute("movie",movie);
	    return "public/movie";
	}


	@GetMapping("/public/movies")
	public String showMovies(Model model) 
	{
		model.addAttribute("movies", this.movieRepository.findAllByOrderByTitle());
		return "public/movies";
	}
	
	@GetMapping("/public/formSearchMovies")
	public String formSearchMovies() 
	{
		return "public/formSearchMovies";
	}

	@PostMapping("/public/searchMovies")
	public String searchMovies(Model model,@RequestParam String genre, @RequestParam Integer year)
	{
		if(genre == "" && year == null)
		{
			model.addAttribute("movies", null);
		}
		
		if(genre == "" && year != null)
		{
			model.addAttribute("movies", this.movieRepository.findByYearOrderByTitleAsc(year));
		}
		
		if(year == null && genre != "")
		{
			model.addAttribute("movies", this.movieRepository.findByGenreOrderByTitleAsc(genre));
		}
		
		if(genre != "" && year != null)
		{
			model.addAttribute("movies", this.movieRepository.findByYearAndGenreOrderByTitleAsc(year,genre));
		}
		
		return "public/foundMovies";
	}
	
	// RICHIESTE HTTP PER MY_MOVIES 
	@GetMapping("/default/myMovies")
	public String myMovies(Model model)
	{
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
	    	
		model.addAttribute("movies", credentials.getUser().getMyMovies());
			
		return "default/myMovies";
	}
	
	
	
	
	
	
	@GetMapping("/default/AddAsMyMovie/{id}")
	public String AddAsMyMovie(@PathVariable("id") Long id, Model model, Authentication authentication)
	{
		
		//Prendo l'utente
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User user = credentials.getUser();
		//Prendo l'enità film
		Movie movie= this.movieRepository.findById(id).get();
		List<Review>reviewAllMovie= this.reviewRepository.findByMovie(movie); /*contiene tutte le recensioni di tale film*/
		//Setto a true la variabile movieIsAdded
		Boolean movieIsAdded = true;
		
		 Set<User> users = movie.getUsers();
		 users.add(user);
	        
	     movie.setUsers(users);
	     this.movieRepository.save(movie);
	        
	        
	     user.getMyMovies().add(movie);
	     this.userRepository.save(user);
	        
	        for(Review review: reviewAllMovie)
	        {
	        	if( review.getWriter().getId().equals(credentials.getUser().getId())) 
	        	{
	        		model.addAttribute("review",review); /*recensione di tale utente su questo film*/
	        		model.addAttribute("reviews",reviewAllMovie); /*tutte le recensioni di tale film*/
	        		model.addAttribute("director",movie.getArtist_asdirector()); //gli passiamo il regista
	        		model.addAttribute("actors",movie.getActors()); //gli passiamo gli attori
	        		model.addAttribute("movieIsAdded",movieIsAdded);
	        		model.addAttribute("movie", movie);
	        		return "public/movie";
	        	}
	        }
	        model.addAttribute("review",null); /*recensione di tale utente su questo film*/
    		model.addAttribute("reviews",reviewAllMovie); /*tutte le recensioni di tale film*/
    		model.addAttribute("director",movie.getArtist_asdirector()); //gli passiamo il regista
    		model.addAttribute("actors",movie.getActors()); //gli passiamo gli attori
    		model.addAttribute("movieIsAdded",movieIsAdded);
    		model.addAttribute("movie", movie);
	    return "public/movie";
	}
	
	@Transactional
	@GetMapping("/default/deleteAsMyMovie/{id}")
	public String deleteAsMyMovie(@PathVariable("id") Long id, Model model, Authentication authentication)
	{
		//Prendo l'utente
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User user = credentials.getUser();
		//Prendo l'enità film
		Movie movie= this.movieRepository.findById(id).get();
		List<Review>reviewAllMovie= this.reviewRepository.findByMovie(movie); /*contiene tutte le recensioni di tale film*/
		//Setto a true la variabile movieIsAdded
		Boolean movieIsAdded = false;
		
		
		
		 movie.getUsers().remove(user);
	     
	     this.movieRepository.save(movie);
	        
	       
	     user.getMyMovies().remove(movie);
	     
	     this.userRepository.save(user);
	        
	        for(Review review: reviewAllMovie)
	        {
	        	if( review.getWriter().getId().equals(credentials.getUser().getId())) 
	        	{
	        		model.addAttribute("review",review); /*recensione di tale utente su questo film*/
	        		model.addAttribute("reviews",reviewAllMovie); /*tutte le recensioni di tale film*/
	        		model.addAttribute("director",movie.getArtist_asdirector()); //gli passiamo il regista
	        		model.addAttribute("actors",movie.getActors()); //gli passiamo gli attori
	        		model.addAttribute("movieIsAdded",movieIsAdded);
	        		model.addAttribute("movie", movie);
	        		return "public/movie";
	        	}
	        }
	        model.addAttribute("review",null); /*recensione di tale utente su questo film*/
    		model.addAttribute("reviews",reviewAllMovie); /*tutte le recensioni di tale film*/
    		model.addAttribute("director",movie.getArtist_asdirector()); //gli passiamo il regista
    		model.addAttribute("actors",movie.getActors()); //gli passiamo gli attori
    		model.addAttribute("movieIsAdded",movieIsAdded);
    		model.addAttribute("movie", movie);
	    return "public/movie";
	}
}