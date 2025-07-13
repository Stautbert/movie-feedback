package com.moviefeedback.movieservice.service;

import com.moviefeedback.movieservice.model.Movie;
import com.moviefeedback.movieservice.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("A test movie description");
        testMovie.setGenre("Action");
        testMovie.setReleaseYear(2023);
        testMovie.setDirector("Test Director");
    }

    @Test
    void getAllMovies_ShouldReturnAllMovies() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.getAllMovies();

        // Assert
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository).findAll();
    }

    @Test
    void getMovieById_WhenMovieExists_ShouldReturnMovie() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        // Act
        Optional<Movie> actualMovie = movieService.getMovieById(1L);

        // Assert
        assertTrue(actualMovie.isPresent());
        assertEquals(testMovie, actualMovie.get());
        verify(movieRepository).findById(1L);
    }

    @Test
    void getMovieById_WhenMovieDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Movie> actualMovie = movieService.getMovieById(1L);

        // Assert
        assertFalse(actualMovie.isPresent());
        verify(movieRepository).findById(1L);
    }

    @Test
    void createMovie_WhenTitleDoesNotExist_ShouldCreateMovie() {
        // Arrange
        when(movieRepository.existsByTitleIgnoreCase("Test Movie")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        // Act
        Movie createdMovie = movieService.createMovie(testMovie);

        // Assert
        assertEquals(testMovie, createdMovie);
        verify(movieRepository).existsByTitleIgnoreCase("Test Movie");
        verify(movieRepository).save(testMovie);
    }

    @Test
    void createMovie_WhenTitleExists_ShouldThrowException() {
        // Arrange
        when(movieRepository.existsByTitleIgnoreCase("Test Movie")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> movieService.createMovie(testMovie)
        );
        assertEquals("Movie with title 'Test Movie' already exists", exception.getMessage());
        verify(movieRepository).existsByTitleIgnoreCase("Test Movie");
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieExists_ShouldUpdateMovie() {
        // Arrange
        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Updated Movie");
        updatedMovie.setDescription("Updated description");
        updatedMovie.setGenre("Drama");
        updatedMovie.setReleaseYear(2024);
        updatedMovie.setDirector("Updated Director");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieRepository.existsByTitleIgnoreCase("Updated Movie")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        // Act
        Movie result = movieService.updateMovie(1L, updatedMovie);

        // Assert
        assertEquals(updatedMovie, result);
        verify(movieRepository).findById(1L);
        verify(movieRepository).existsByTitleIgnoreCase("Updated Movie");
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieDoesNotExist_ShouldThrowException() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> movieService.updateMovie(1L, testMovie)
        );
        assertEquals("Movie not found with id: 1", exception.getMessage());
        verify(movieRepository).findById(1L);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovie_WhenMovieExists_ShouldDeleteMovie() {
        // Arrange
        when(movieRepository.existsById(1L)).thenReturn(true);

        // Act
        movieService.deleteMovie(1L);

        // Assert
        verify(movieRepository).existsById(1L);
        verify(movieRepository).deleteById(1L);
    }

    @Test
    void deleteMovie_WhenMovieDoesNotExist_ShouldThrowException() {
        // Arrange
        when(movieRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> movieService.deleteMovie(1L)
        );
        assertEquals("Movie not found with id: 1", exception.getMessage());
        verify(movieRepository).existsById(1L);
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void searchMovies_ShouldReturnMatchingMovies() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(testMovie);
        when(movieRepository.searchMovies("test")).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.searchMovies("test");

        // Assert
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository).searchMovies("test");
    }

    @Test
    void getMoviesByGenre_ShouldReturnMoviesByGenre() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(testMovie);
        when(movieRepository.findByGenreIgnoreCase("Action")).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.getMoviesByGenre("Action");

        // Assert
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository).findByGenreIgnoreCase("Action");
    }

    @Test
    void getMoviesByYear_ShouldReturnMoviesByYear() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(testMovie);
        when(movieRepository.findByReleaseYear(2023)).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.getMoviesByYear(2023);

        // Assert
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository).findByReleaseYear(2023);
    }

    @Test
    void getMoviesByDirector_ShouldReturnMoviesByDirector() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(testMovie);
        when(movieRepository.findByDirectorIgnoreCaseContaining("Test")).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.getMoviesByDirector("Test");

        // Assert
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository).findByDirectorIgnoreCaseContaining("Test");
    }
} 