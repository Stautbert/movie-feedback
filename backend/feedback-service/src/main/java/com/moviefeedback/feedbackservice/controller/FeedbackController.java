package com.moviefeedback.feedbackservice.controller;

import com.moviefeedback.feedbackservice.model.Feedback;
import com.moviefeedback.feedbackservice.service.FeedbackService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {
    
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    
    private final FeedbackService feedbackService;
    
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        logger.info("GET /api/feedback - Fetching all feedback");
        List<Feedback> feedback = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        logger.info("GET /api/feedback/{} - Fetching feedback by id", id);
        Optional<Feedback> feedback = feedbackService.getFeedbackById(id);
        return feedback.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@Valid @RequestBody Feedback feedback) {
        logger.info("POST /api/feedback - Creating new feedback for movie: {}", feedback.getMovieId());
        try {
            Feedback createdFeedback = feedbackService.createFeedback(feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedback);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @Valid @RequestBody Feedback feedbackDetails) {
        logger.info("PUT /api/feedback/{} - Updating feedback", id);
        try {
            Feedback updatedFeedback = feedbackService.updateFeedback(id, feedbackDetails);
            return ResponseEntity.ok(updatedFeedback);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating feedback: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        logger.info("DELETE /api/feedback/{} - Deleting feedback", id);
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting feedback: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Feedback>> getFeedbackByMovieId(@PathVariable Long movieId) {
        logger.info("GET /api/feedback/movie/{} - Fetching feedback by movie id", movieId);
        List<Feedback> feedback = feedbackService.getFeedbackByMovieId(movieId);
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/visitor/{visitorName}")
    public ResponseEntity<List<Feedback>> getFeedbackByVisitorName(@PathVariable String visitorName) {
        logger.info("GET /api/feedback/visitor/{} - Fetching feedback by visitor name", visitorName);
        List<Feedback> feedback = feedbackService.getFeedbackByVisitorName(visitorName);
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<Feedback>> getFeedbackByRating(@PathVariable Integer rating) {
        logger.info("GET /api/feedback/rating/{} - Fetching feedback by rating", rating);
        List<Feedback> feedback = feedbackService.getFeedbackByRating(rating);
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/rating/gte/{rating}")
    public ResponseEntity<List<Feedback>> getFeedbackByRatingGreaterThanEqual(@PathVariable Integer rating) {
        logger.info("GET /api/feedback/rating/gte/{} - Fetching feedback with rating >= {}", rating, rating);
        List<Feedback> feedback = feedbackService.getFeedbackByRatingGreaterThanEqual(rating);
        return ResponseEntity.ok(feedback);
    }
    
    @GetMapping("/movie/{movieId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByMovieId(@PathVariable Long movieId) {
        logger.info("GET /api/feedback/movie/{}/average-rating - Getting average rating", movieId);
        Double averageRating = feedbackService.getAverageRatingByMovieId(movieId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }
    
    @GetMapping("/movie/{movieId}/count")
    public ResponseEntity<Long> getFeedbackCountByMovieId(@PathVariable Long movieId) {
        logger.info("GET /api/feedback/movie/{}/count - Getting feedback count", movieId);
        Long count = feedbackService.getFeedbackCountByMovieId(movieId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/movie/{movieId}/recent")
    public ResponseEntity<List<Feedback>> getRecentFeedbackByMovieId(@PathVariable Long movieId) {
        logger.info("GET /api/feedback/movie/{}/recent - Fetching recent feedback", movieId);
        List<Feedback> feedback = feedbackService.getRecentFeedbackByMovieId(movieId);
        return ResponseEntity.ok(feedback);
    }
} 