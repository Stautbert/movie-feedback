import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Movie API
export const movieAPI = {
  getAll: () => api.get('/api/movies'),
  getById: (id) => api.get(`/api/movies/${id}`),
  create: (movie) => api.post('/api/movies', movie),
  update: (id, movie) => api.put(`/api/movies/${id}`, movie),
  delete: (id) => api.delete(`/api/movies/${id}`),
  search: (keyword) => api.get(`/api/movies/search?keyword=${keyword}`),
  getByGenre: (genre) => api.get(`/api/movies/genre/${genre}`),
  getByYear: (year) => api.get(`/api/movies/year/${year}`),
  getByDirector: (director) => api.get(`/api/movies/director/${director}`),
};

// Feedback API
export const feedbackAPI = {
  getAll: () => api.get('/api/feedback'),
  getById: (id) => api.get(`/api/feedback/${id}`),
  create: (feedback) => api.post('/api/feedback', feedback),
  update: (id, feedback) => api.put(`/api/feedback/${id}`, feedback),
  delete: (id) => api.delete(`/api/feedback/${id}`),
  getByMovieId: (movieId) => api.get(`/api/feedback/movie/${movieId}`),
  getByVisitorName: (visitorName) => api.get(`/api/feedback/visitor/${visitorName}`),
  getByRating: (rating) => api.get(`/api/feedback/rating/${rating}`),
  getByRatingGreaterThanEqual: (rating) => api.get(`/api/feedback/rating/gte/${rating}`),
  getAverageRatingByMovieId: (movieId) => api.get(`/api/feedback/movie/${movieId}/average-rating`),
  getFeedbackCountByMovieId: (movieId) => api.get(`/api/feedback/movie/${movieId}/count`),
  getRecentFeedbackByMovieId: (movieId) => api.get(`/api/feedback/movie/${movieId}/recent`),
};

export default api; 