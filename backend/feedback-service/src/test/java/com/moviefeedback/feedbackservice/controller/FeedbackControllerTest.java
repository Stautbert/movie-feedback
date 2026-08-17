package com.moviefeedback.feedbackservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviefeedback.feedbackservice.model.Feedback;
import com.moviefeedback.feedbackservice.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        testFeedback = new Feedback(10L, "Test Visitor", "Great movie!", 5, "visitor@example.com");
        testFeedback.setId(1L);
    }

    @Test
    void getAllFeedback_ShouldReturnFeedbackList() throws Exception {
        when(feedbackService.getAllFeedback()).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/api/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].visitorName").value("Test Visitor"));
    }

    @Test
    void getFeedbackById_WhenFound_ShouldReturnFeedback() throws Exception {
        when(feedbackService.getFeedbackById(1L)).thenReturn(Optional.of(testFeedback));

        mockMvc.perform(get("/api/feedback/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getFeedbackById_WhenNotFound_ShouldReturn404() throws Exception {
        when(feedbackService.getFeedbackById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/feedback/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFeedback_WhenValid_ShouldReturn201() throws Exception {
        when(feedbackService.createFeedback(any(Feedback.class))).thenReturn(testFeedback);

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFeedback)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visitorName").value("Test Visitor"));
    }

    @Test
    void createFeedback_WhenRatingMissing_ShouldReturn400() throws Exception {
        Feedback invalidFeedback = new Feedback(10L, "Test Visitor", "Great movie!", null, null);

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFeedback)))
                .andExpect(status().isBadRequest());

        verify(feedbackService, never()).createFeedback(any(Feedback.class));
    }

    @Test
    void createFeedback_WhenRatingOutOfRange_ShouldReturn400() throws Exception {
        Feedback invalidFeedback = new Feedback(10L, "Test Visitor", "Great movie!", 6, null);

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFeedback)))
                .andExpect(status().isBadRequest());

        verify(feedbackService, never()).createFeedback(any(Feedback.class));
    }

    @Test
    void updateFeedback_WhenValid_ShouldReturn200() throws Exception {
        when(feedbackService.updateFeedback(eq(1L), any(Feedback.class))).thenReturn(testFeedback);

        mockMvc.perform(put("/api/feedback/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFeedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitorName").value("Test Visitor"));
    }

    @Test
    void updateFeedback_WhenNotFound_ShouldReturn400() throws Exception {
        when(feedbackService.updateFeedback(eq(99L), any(Feedback.class)))
                .thenThrow(new IllegalArgumentException("Feedback not found with id: 99"));

        mockMvc.perform(put("/api/feedback/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFeedback)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFeedback_WhenFound_ShouldReturn204() throws Exception {
        doNothing().when(feedbackService).deleteFeedback(1L);

        mockMvc.perform(delete("/api/feedback/1"))
                .andExpect(status().isNoContent());

        verify(feedbackService).deleteFeedback(1L);
    }

    @Test
    void deleteFeedback_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new IllegalArgumentException("Feedback not found with id: 99"))
                .when(feedbackService).deleteFeedback(99L);

        mockMvc.perform(delete("/api/feedback/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFeedbackByMovieId_ShouldReturnMatchingFeedback() throws Exception {
        when(feedbackService.getFeedbackByMovieId(10L)).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/api/feedback/movie/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieId").value(10));
    }

    @Test
    void getFeedbackByVisitorName_ShouldReturnMatchingFeedback() throws Exception {
        when(feedbackService.getFeedbackByVisitorName("Test")).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/api/feedback/visitor/Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].visitorName").value("Test Visitor"));
    }

    @Test
    void getFeedbackByRating_ShouldReturnMatchingFeedback() throws Exception {
        when(feedbackService.getFeedbackByRating(5)).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/api/feedback/rating/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void getFeedbackByRatingGreaterThanEqual_ShouldReturnMatchingFeedback() throws Exception {
        when(feedbackService.getFeedbackByRatingGreaterThanEqual(3)).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/api/feedback/rating/gte/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void getAverageRatingByMovieId_WhenFeedbackExists_ShouldReturnAverage() throws Exception {
        when(feedbackService.getAverageRatingByMovieId(10L)).thenReturn(4.5);

        mockMvc.perform(get("/api/feedback/movie/10/average-rating"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void getAverageRatingByMovieId_WhenNoFeedback_ShouldReturnZero() throws Exception {
        when(feedbackService.getAverageRatingByMovieId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/feedback/movie/99/average-rating"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }

    @Test
    void getFeedbackCountByMovieId_ShouldReturnCount() throws Exception {
        when(feedbackService.getFeedbackCountByMovieId(10L)).thenReturn(3L);

        mockMvc.perform(get("/api/feedback/movie/10/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void getRecentFeedbackByMovieId_ShouldReturnRecentFeedback() throws Exception {
        when(feedbackService.getRecentFeedbackByMovieId(10L)).thenReturn(Arrays.asList(testFeedback));

        mockMvc.perform(get("/api/feedback/movie/10/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieId").value(10));
    }
}
