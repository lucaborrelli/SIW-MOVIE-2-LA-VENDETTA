package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;


@Component
public class ArtistValidator implements Validator
{

	@Autowired
	private ArtistRepository artistRepository;
	
	
	@Override
	public void validate(Object o, Errors errors) 
	{
		
		LocalDate today= LocalDate.now(); 
		Artist artista= (Artist)o; 
		
		if(this.artistRepository.existsByNameAndSurname(artista.getName(), artista.getSurname()))
			errors.reject("artist.duplicate");
		else 
		{
			if(artista.getDateOfBirth() == null)
				errors.rejectValue("dateOfBirth", "typenull");
			else
			{
				
				
				if(!artista.getDateOfBirth().isBefore(today))
					errors.rejectValue("dateOfBirth","typeincorr");
			}
				
			if(artista.getName().isEmpty())
			{
				errors.rejectValue("name","typenull");
			}
			else
				if(!(artista.getName().matches("[a-zA-Z ]+")))
					errors.rejectValue("name","typeincorr");
			
			if(artista.getSurname().isEmpty()) 
			{
				errors.rejectValue("surname","typenull");
			}
			
			else
				if(!(artista.getSurname().matches("[a-zA-Z ]+")))
					errors.rejectValue("surname","typeincorr");
			
			if(artista.getNationality().isEmpty()) 
			{
				errors.rejectValue("nationality","typenull");
			}
			else
				if(!(artista.getNationality().matches("[a-zA-Z ]+"))) 
					errors.rejectValue("nationality","typeincorr");
			
			if(artista.getDateOfDeath() != null)
			{
				if(!artista.getDateOfDeath().isBefore(today)) 
				{
					errors.rejectValue("dateOfDeath", "typeincorr");
				}
			
				if(!artista.getDateOfBirth().isBefore(artista.getDateOfDeath()))
					errors.rejectValue("dateOfDeath", "typeincorr");
			}
		}		
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Artist.class.equals(aClass);
	}
}
