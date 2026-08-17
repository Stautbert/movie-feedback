package com.moviefeedback.feedbackservice.service;

import com.moviefeedback.feedbackservice.model.Feedback;
import com.moviefeedback.feedbackservice.repository.FeedbackRepository;
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
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        testFeedback = new Feedback();
        testFeedback.setId(1L);
        testFeedback.setMovieId(10L);
        testFeedback.setVisitorName("Test Visitor");
        testFeedback.setComment("Great movie!");
        testFeedback.setRating(5);
        testFeedback.setVisitorEmail("visitor@example.com");
    }

    @Test
    void getAllFeedback_ShouldReturnAllFeedback() {
        List<Feedback> expectedFeedback = Arrays.asList(testFeedback);
        when(feedbackRepository.findAll()).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = feedbackService.getAllFeedback();

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackRepository).findAll();
    }

    @Test
    void getFeedbackById_WhenFeedbackExists_ShouldReturnFeedback() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        Optional<Feedback> actualFeedback = feedbackService.getFeedbackById(1L);

        assertTrue(actualFeedback.isPresent());
        assertEquals(testFeedback, actualFeedback.get());
        verify(feedbackRepository).findById(1L);
    }

    @Test
    void getFeedbackById_WhenFeedbackDoesNotExist_ShouldReturnEmpty() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Feedback> actualFeedback = feedbackService.getFeedbackById(1L);

        assertFalse(actualFeedback.isPresent());
        verify(feedbackRepository).findById(1L);
    }

    @Test
    void createFeedback_WhenRatingValid_ShouldCreateFeedback() {
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);

        Feedback createdFeedback = feedbackService.createFeedback(testFeedback);

        assertEquals(testFeedback, createdFeedback);
        verify(feedbackRepository).save(testFeedback);
    }

    @Test
    void createFeedback_WhenRatingTooLow_ShouldThrowException() {
        testFeedback.setRating(0);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> feedbackService.createFeedback(testFeedback)
        );
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void createFeedback_WhenRatingTooHigh_ShouldThrowException() {
        testFeedback.setRating(6);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> feedbackService.createFeedback(testFeedback)
        );
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void updateFeedback_WhenFeedbackExists_ShouldUpdateFeedback() {
        Feedback updatedDetails = new Feedback();
        updatedDetails.setVisitorName("Updated Visitor");
        updatedDetails.setComment("Updated comment");
        updatedDetails.setRating(4);
        updatedDetails.setVisitorEmail("updated@example.com");

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);

        Feedback result = feedbackService.updateFeedback(1L, updatedDetails);

        assertEquals("Updated Visitor", result.getVisitorName());
        assertEquals("Updated comment", result.getComment());
        assertEquals(4, result.getRating());
        assertEquals("updated@example.com", result.getVisitorEmail());
        verify(feedbackRepository).findById(1L);
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void updateFeedback_WhenFeedbackDoesNotExist_ShouldThrowException() {
        when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> feedbackService.updateFeedback(1L, testFeedback)
        );
        assertEquals("Feedback not found with id: 1", exception.getMessage());
        verify(feedbackRepository).findById(1L);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void updateFeedback_WhenRatingInvalid_ShouldThrowException() {
        Feedback updatedDetails = new Feedback();
        updatedDetails.setVisitorName("Updated Visitor");
        updatedDetails.setComment("Updated comment");
        updatedDetails.setRating(7);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(testFeedback));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> feedbackService.updateFeedback(1L, updatedDetails)
        );
        assertEquals("Rating must be between 1 and 5", exception.getMessage());
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    @Test
    void deleteFeedback_WhenFeedbackExists_ShouldDeleteFeedback() {
        when(feedbackRepository.existsById(1L)).thenReturn(true);

        feedbackService.deleteFeedback(1L);

        verify(feedbackRepository).existsById(1L);
        verify(feedbackRepository).deleteById(1L);
    }

    @Test
    void deleteFeedback_WhenFeedbackDoesNotExist_ShouldThrowException() {
        when(feedbackRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> feedbackService.deleteFeedback(1L)
        );
        assertEquals("Feedback not found with id: 1", exception.getMessage());
        verify(feedbackRepository).existsById(1L);
        verify(feedbackRepository, never()).deleteById(any());
    }

    @Test
    void getFeedbackByMovieId_ShouldReturnMatchingFeedback() {
        List<Feedback> expectedFeedback = Arrays.asList(testFeedback);
        when(feedbackRepository.findByMovieId(10L)).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = feedbackService.getFeedbackByMovieId(10L);

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackRepository).findByMovieId(10L);
    }

    @Test
    void getFeedbackByVisitorName_ShouldReturnMatchingFeedback() {
        List<Feedback> expectedFeedback = Arrays.asList(testFeedback);
        when(feedbackRepository.findByVisitorNameIgnoreCaseContaining("Test")).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = feedbackService.getFeedbackByVisitorName("Test");

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackRepository).findByVisitorNameIgnoreCaseContaining("Test");
    }

    @Test
    void getFeedbackByRating_ShouldReturnMatchingFeedback() {
        List<Feedback> expectedFeedback = Arrays.asList(testFeedback);
        when(feedbackRepository.findByRating(5)).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = feedbackService.getFeedbackByRating(5);

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackRepository).findByRating(5);
    }

    @Test
    void getFeedbackByRatingGreaterThanEqual_ShouldReturnMatchingFeedback() {
        List<Feedback> expectedFeedback = Arrays.asList(testFeedback);
        when(feedbackRepository.findByRatingGreaterThanEqual(3)).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = feedbackService.getFeedbackByRatingGreaterThanEqual(3);

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackRepository).findByRatingGreaterThanEqual(3);
    }

    @Test
    void getAverageRatingByMovieId_WhenFeedbackExists_ShouldReturnAverage() {
        when(feedbackRepository.getAverageRatingByMovieId(10L)).thenReturn(4.5);

        Double average = feedbackService.getAverageRatingByMovieId(10L);

        assertEquals(4.5, average);
        verify(feedbackRepository).getAverageRatingByMovieId(10L);
    }

    @Test
    void getAverageRatingByMovieId_WhenNoFeedback_ShouldReturnNull() {
        when(feedbackRepository.getAverageRatingByMovieId(99L)).thenReturn(null);

        Double average = feedbackService.getAverageRatingByMovieId(99L);

        assertNull(average);
        verify(feedbackRepository).getAverageRatingByMovieId(99L);
    }

    @Test
    void getFeedbackCountByMovieId_ShouldReturnCount() {
        when(feedbackRepository.getFeedbackCountByMovieId(10L)).thenReturn(3L);

        Long count = feedbackService.getFeedbackCountByMovieId(10L);

        assertEquals(3L, count);
        verify(feedbackRepository).getFeedbackCountByMovieId(10L);
    }

    @Test
    void getRecentFeedbackByMovieId_ShouldReturnRecentFeedback() {
        List<Feedback> expectedFeedback = Arrays.asList(testFeedback);
        when(feedbackRepository.findRecentFeedbackByMovieId(10L)).thenReturn(expectedFeedback);

        List<Feedback> actualFeedback = feedbackService.getRecentFeedbackByMovieId(10L);

        assertEquals(expectedFeedback, actualFeedback);
        verify(feedbackRepository).findRecentFeedbackByMovieId(10L);
    }
}
