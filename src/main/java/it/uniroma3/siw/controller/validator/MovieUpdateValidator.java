package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;

@Component
public class MovieUpdateValidator implements Validator 
{
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		
		Movie movie= (Movie)o;
			if(movie.getPlot().length() > 254 ) 
			{
				errors.rejectValue("plot", "typeincorr");
			}
			
			if(movie.getGenre().equals(""))
			{
				errors.rejectValue("genre", "typeincorr");
			}
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Movie.class.equals(aClass);
	}
}
