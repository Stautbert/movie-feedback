package com.moviefeedback.movieservice.repository;

import com.moviefeedback.movieservice.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    Optional<Movie> findByTitleIgnoreCase(String title);
    
    List<Movie> findByGenreIgnoreCase(String genre);
    
    List<Movie> findByReleaseYear(Integer releaseYear);
    
    List<Movie> findByDirectorIgnoreCaseContaining(String director);
    
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(m.director) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> searchMovies(@Param("keyword") String keyword);
    
    boolean existsByTitleIgnoreCase(String title);
} 