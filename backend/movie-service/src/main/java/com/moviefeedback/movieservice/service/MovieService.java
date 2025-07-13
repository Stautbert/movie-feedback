package com.moviefeedback.movieservice.service;

import com.moviefeedback.movieservice.model.Movie;
import com.moviefeedback.movieservice.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieService {
    
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    
    private final MovieRepository movieRepository;
    
    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    
    public List<Movie> getAllMovies() {
        logger.info("Fetching all movies");
        return movieRepository.findAll();
    }
    
    public Optional<Movie> getMovieById(Long id) {
        logger.info("Fetching movie with id: {}", id);
        return movieRepository.findById(id);
    }
    
    public Movie createMovie(Movie movie) {
        logger.info("Creating new movie: {}", movie.getTitle());
        
        if (movieRepository.existsByTitleIgnoreCase(movie.getTitle())) {
            throw new IllegalArgumentException("Movie with title '" + movie.getTitle() + "' already exists");
        }
        
        Movie savedMovie = movieRepository.save(movie);
        logger.info("Movie created successfully with id: {}", savedMovie.getId());
        return savedMovie;
    }
    
    public Movie updateMovie(Long id, Movie movieDetails) {
        logger.info("Updating movie with id: {}", id);
        
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id));
        
        // Check if the new title conflicts with existing movies (excluding current movie)
        if (!movie.getTitle().equalsIgnoreCase(movieDetails.getTitle()) &&
            movieRepository.existsByTitleIgnoreCase(movieDetails.getTitle())) {
            throw new IllegalArgumentException("Movie with title '" + movieDetails.getTitle() + "' already exists");
        }
        
        movie.setTitle(movieDetails.getTitle());
        movie.setDescription(movieDetails.getDescription());
        movie.setGenre(movieDetails.getGenre());
        movie.setReleaseYear(movieDetails.getReleaseYear());
        movie.setDirector(movieDetails.getDirector());
        
        Movie updatedMovie = movieRepository.save(movie);
        logger.info("Movie updated successfully with id: {}", updatedMovie.getId());
        return updatedMovie;
    }
    
    public void deleteMovie(Long id) {
        logger.info("Deleting movie with id: {}", id);
        
        if (!movieRepository.existsById(id)) {
            throw new IllegalArgumentException("Movie not found with id: " + id);
        }
        
        movieRepository.deleteById(id);
        logger.info("Movie deleted successfully with id: {}", id);
    }
    
    public List<Movie> searchMovies(String keyword) {
        logger.info("Searching movies with keyword: {}", keyword);
        return movieRepository.searchMovies(keyword);
    }
    
    public List<Movie> getMoviesByGenre(String genre) {
        logger.info("Fetching movies by genre: {}", genre);
        return movieRepository.findByGenreIgnoreCase(genre);
    }
    
    public List<Movie> getMoviesByYear(Integer year) {
        logger.info("Fetching movies by year: {}", year);
        return movieRepository.findByReleaseYear(year);
    }
    
    public List<Movie> getMoviesByDirector(String director) {
        logger.info("Fetching movies by director: {}", director);
        return movieRepository.findByDirectorIgnoreCaseContaining(director);
    }
} 