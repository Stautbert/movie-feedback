package com.moviefeedback.movieservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviefeedback.movieservice.model.Movie;
import com.moviefeedback.movieservice.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie("Test Movie", "A test description", "Action", 2023, "Test Director");
        testMovie.setId(1L);
    }

    @Test
    void getAllMovies_ShouldReturnMovieList() throws Exception {
        when(movieService.getAllMovies()).thenReturn(Arrays.asList(testMovie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Movie"));
    }

    @Test
    void getMovieById_WhenFound_ShouldReturnMovie() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(testMovie));

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void getMovieById_WhenNotFound_ShouldReturn404() throws Exception {
        when(movieService.getMovieById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMovie_WhenValid_ShouldReturn201() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenReturn(testMovie);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void createMovie_WhenTitleMissing_ShouldReturn400() throws Exception {
        Movie invalidMovie = new Movie("", "A test description", "Action", 2023, "Test Director");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMovie)))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).createMovie(any(Movie.class));
    }

    @Test
    void createMovie_WhenDuplicateTitle_ShouldReturn400() throws Exception {
        when(movieService.createMovie(any(Movie.class)))
                .thenThrow(new IllegalArgumentException("Movie with title 'Test Movie' already exists"));

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovie_WhenValid_ShouldReturn200() throws Exception {
        when(movieService.updateMovie(eq(1L), any(Movie.class))).thenReturn(testMovie);

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void updateMovie_WhenNotFound_ShouldReturn400() throws Exception {
        when(movieService.updateMovie(eq(99L), any(Movie.class)))
                .thenThrow(new IllegalArgumentException("Movie not found with id: 99"));

        mockMvc.perform(put("/api/movies/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMovie_WhenFound_ShouldReturn204() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1L);
    }

    @Test
    void deleteMovie_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new IllegalArgumentException("Movie not found with id: 99"))
                .when(movieService).deleteMovie(99L);

        mockMvc.perform(delete("/api/movies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchMovies_ShouldReturnMatchingMovies() throws Exception {
        when(movieService.searchMovies("test")).thenReturn(Arrays.asList(testMovie));

        mockMvc.perform(get("/api/movies/search").param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Movie"));
    }

    @Test
    void getMoviesByGenre_ShouldReturnMatchingMovies() throws Exception {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieService.getMoviesByGenre("Action")).thenReturn(movies);

        mockMvc.perform(get("/api/movies/genre/Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genre").value("Action"));
    }

    @Test
    void getMoviesByYear_ShouldReturnMatchingMovies() throws Exception {
        when(movieService.getMoviesByYear(2023)).thenReturn(Arrays.asList(testMovie));

        mockMvc.perform(get("/api/movies/year/2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].releaseYear").value(2023));
    }

    @Test
    void getMoviesByDirector_ShouldReturnMatchingMovies() throws Exception {
        when(movieService.getMoviesByDirector("Test")).thenReturn(Arrays.asList(testMovie));

        mockMvc.perform(get("/api/movies/director/Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].director").value("Test Director"));
    }
}
