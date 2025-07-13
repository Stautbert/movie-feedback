import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Form, Button, Badge, Spinner, Table } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { FaStar, FaSearch, FaFilter } from 'react-icons/fa';
import { feedbackAPI, movieAPI } from '../services/api';

const FeedbackList = () => {
  const [feedback, setFeedback] = useState([]);
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRating, setFilterRating] = useState('');
  const [filterMovie, setFilterMovie] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [feedbackResponse, moviesResponse] = await Promise.all([
        feedbackAPI.getAll(),
        movieAPI.getAll()
      ]);
      setFeedback(feedbackResponse.data);
      setMovies(moviesResponse.data);
    } catch (error) {
      toast.error('Failed to load feedback data');
      console.error('Error loading feedback:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    // Client-side search implementation
    const filtered = feedback.filter(item => {
      const matchesSearch = !searchTerm || 
        item.visitorName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.comment.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesRating = !filterRating || item.rating === parseInt(filterRating);
      
      const matchesMovie = !filterMovie || item.movieId === parseInt(filterMovie);
      
      return matchesSearch && matchesRating && matchesMovie;
    });
    
    setFeedback(filtered);
  };

  const clearFilters = () => {
    setSearchTerm('');
    setFilterRating('');
    setFilterMovie('');
    loadData();
  };

  const renderStars = (rating) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <FaStar
          key={i}
          className={i <= rating ? 'text-warning' : 'text-muted'}
        />
      );
    }
    return stars;
  };

  const getMovieTitle = (movieId) => {
    const movie = movies.find(m => m.id === movieId);
    return movie ? movie.title : `Movie ID: ${movieId}`;
  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  return (
    <div>
      <h2 className="mb-4">💬 Movie Feedback</h2>

      {/* Search and Filters */}
      <Card className="mb-4">
        <Card.Body>
          <Row>
            <Col md={4}>
              <Form.Group className="mb-3">
                <Form.Label>Search Feedback</Form.Label>
                <div className="d-flex">
                  <Form.Control
                    type="text"
                    placeholder="Search by visitor name or comment..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                  />
                  <Button variant="outline-secondary" onClick={handleSearch}>
                    <FaSearch />
                  </Button>
                </div>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group className="mb-3">
                <Form.Label>Filter by Rating</Form.Label>
                <Form.Select
                  value={filterRating}
                  onChange={(e) => setFilterRating(e.target.value)}
                >
                  <option value="">All Ratings</option>
                  <option value="5">5 Stars</option>
                  <option value="4">4 Stars</option>
                  <option value="3">3 Stars</option>
                  <option value="2">2 Stars</option>
                  <option value="1">1 Star</option>
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group className="mb-3">
                <Form.Label>Filter by Movie</Form.Label>
                <Form.Select
                  value={filterMovie}
                  onChange={(e) => setFilterMovie(e.target.value)}
                >
                  <option value="">All Movies</option>
                  {movies.map(movie => (
                    <option key={movie.id} value={movie.id}>
                      {movie.title}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group className="mb-3">
                <Form.Label>&nbsp;</Form.Label>
                <div>
                  <Button variant="outline-primary" onClick={handleSearch} className="me-2">
                    <FaFilter />
                  </Button>
                  <Button variant="outline-secondary" onClick={clearFilters}>
                    Clear
                  </Button>
                </div>
              </Form.Group>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Feedback Table */}
      <Card>
        <Card.Body>
          {feedback.length > 0 ? (
            <Table responsive striped>
              <thead>
                <tr>
                  <th>Visitor</th>
                  <th>Movie</th>
                  <th>Rating</th>
                  <th>Comment</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {feedback.map((item) => (
                  <tr key={item.id}>
                    <td>
                      <strong>{item.visitorName}</strong>
                      {item.visitorEmail && (
                        <div>
                          <small className="text-muted">{item.visitorEmail}</small>
                        </div>
                      )}
                    </td>
                    <td>
                      <Badge bg="info">{getMovieTitle(item.movieId)}</Badge>
                    </td>
                    <td>
                      <div>
                        {renderStars(item.rating)}
                        <span className="ms-2">({item.rating}/5)</span>
                      </div>
                    </td>
                    <td>
                      <div style={{ maxWidth: '300px' }}>
                        {item.comment}
                      </div>
                    </td>
                    <td>
                      <small>{new Date(item.createdAt).toLocaleDateString()}</small>
                      <br />
                      <small className="text-muted">
                        {new Date(item.createdAt).toLocaleTimeString()}
                      </small>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div className="text-center py-5">
              <h4>No feedback found</h4>
              <p className="text-muted">Try adjusting your search or filters</p>
            </div>
          )}
        </Card.Body>
      </Card>
    </div>
  );
};

export default FeedbackList; 