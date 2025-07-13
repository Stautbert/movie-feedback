package com.moviefeedback.feedbackservice.service;

import com.moviefeedback.feedbackservice.model.Feedback;
import com.moviefeedback.feedbackservice.repository.FeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {
    
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    
    private final FeedbackRepository feedbackRepository;
    
    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }
    
    public List<Feedback> getAllFeedback() {
        logger.info("Fetching all feedback");
        return feedbackRepository.findAll();
    }
    
    public Optional<Feedback> getFeedbackById(Long id) {
        logger.info("Fetching feedback with id: {}", id);
        return feedbackRepository.findById(id);
    }
    
    public Feedback createFeedback(Feedback feedback) {
        logger.info("Creating new feedback for movie: {}", feedback.getMovieId());
        
        // Validate rating
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        logger.info("Feedback created successfully with id: {}", savedFeedback.getId());
        return savedFeedback;
    }
    
    public Feedback updateFeedback(Long id, Feedback feedbackDetails) {
        logger.info("Updating feedback with id: {}", id);
        
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with id: " + id));
        
        // Validate rating
        if (feedbackDetails.getRating() < 1 || feedbackDetails.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        feedback.setVisitorName(feedbackDetails.getVisitorName());
        feedback.setComment(feedbackDetails.getComment());
        feedback.setRating(feedbackDetails.getRating());
        feedback.setVisitorEmail(feedbackDetails.getVisitorEmail());
        
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        logger.info("Feedback updated successfully with id: {}", updatedFeedback.getId());
        return updatedFeedback;
    }
    
    public void deleteFeedback(Long id) {
        logger.info("Deleting feedback with id: {}", id);
        
        if (!feedbackRepository.existsById(id)) {
            throw new IllegalArgumentException("Feedback not found with id: " + id);
        }
        
        feedbackRepository.deleteById(id);
        logger.info("Feedback deleted successfully with id: {}", id);
    }
    
    public List<Feedback> getFeedbackByMovieId(Long movieId) {
        logger.info("Fetching feedback for movie: {}", movieId);
        return feedbackRepository.findByMovieId(movieId);
    }
    
    public List<Feedback> getFeedbackByVisitorName(String visitorName) {
        logger.info("Fetching feedback by visitor name: {}", visitorName);
        return feedbackRepository.findByVisitorNameIgnoreCaseContaining(visitorName);
    }
    
    public List<Feedback> getFeedbackByRating(Integer rating) {
        logger.info("Fetching feedback with rating: {}", rating);
        return feedbackRepository.findByRating(rating);
    }
    
    public List<Feedback> getFeedbackByRatingGreaterThanEqual(Integer rating) {
        logger.info("Fetching feedback with rating >= {}", rating);
        return feedbackRepository.findByRatingGreaterThanEqual(rating);
    }
    
    public Double getAverageRatingByMovieId(Long movieId) {
        logger.info("Calculating average rating for movie: {}", movieId);
        return feedbackRepository.getAverageRatingByMovieId(movieId);
    }
    
    public Long getFeedbackCountByMovieId(Long movieId) {
        logger.info("Getting feedback count for movie: {}", movieId);
        return feedbackRepository.getFeedbackCountByMovieId(movieId);
    }
    
    public List<Feedback> getRecentFeedbackByMovieId(Long movieId) {
        logger.info("Fetching recent feedback for movie: {}", movieId);
        return feedbackRepository.findRecentFeedbackByMovieId(movieId);
    }
} 