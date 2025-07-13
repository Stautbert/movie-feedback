package com.moviefeedback.feedbackservice.repository;

import com.moviefeedback.feedbackservice.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByMovieId(Long movieId);
    
    List<Feedback> findByVisitorNameIgnoreCaseContaining(String visitorName);
    
    List<Feedback> findByRating(Integer rating);
    
    List<Feedback> findByRatingGreaterThanEqual(Integer rating);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.movieId = :movieId")
    Double getAverageRatingByMovieId(@Param("movieId") Long movieId);
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.movieId = :movieId")
    Long getFeedbackCountByMovieId(@Param("movieId") Long movieId);
    
    @Query("SELECT f FROM Feedback f WHERE f.movieId = :movieId ORDER BY f.createdAt DESC")
    List<Feedback> findRecentFeedbackByMovieId(@Param("movieId") Long movieId);
} 