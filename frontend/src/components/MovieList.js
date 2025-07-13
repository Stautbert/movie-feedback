import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Form, Button, Badge, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { FaStar, FaSearch, FaPlus } from 'react-icons/fa';
import { movieAPI, feedbackAPI } from '../services/api';

const MovieList = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterGenre, setFilterGenre] = useState('');
  const [filterYear, setFilterYear] = useState('');
  const [averageRatings, setAverageRatings] = useState({});

  useEffect(() => {
    loadMovies();
  }, []);

  const loadMovies = async () => {
    try {
      setLoading(true);
      const response = await movieAPI.getAll();
      setMovies(response.data);
      
      // Load average ratings for each movie
      const ratings = {};
      for (const movie of response.data) {
        try {
          const ratingResponse = await feedbackAPI.getAverageRatingByMovieId(movie.id);
          ratings[movie.id] = ratingResponse.data;
        } catch (error) {
          ratings[movie.id] = 0;
        }
      }
      setAverageRatings(ratings);
    } catch (error) {
      toast.error('Failed to load movies');
      console.error('Error loading movies:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      loadMovies();
      return;
    }

    try {
      setLoading(true);
      const response = await movieAPI.search(searchTerm);
      setMovies(response.data);
    } catch (error) {
      toast.error('Search failed');
      console.error('Error searching movies:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterByGenre = async () => {
    if (!filterGenre.trim()) {
      loadMovies();
      return;
    }

    try {
      setLoading(true);
      const response = await movieAPI.getByGenre(filterGenre);
      setMovies(response.data);
    } catch (error) {
      toast.error('Filter failed');
      console.error('Error filtering movies:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterByYear = async () => {
    if (!filterYear.trim()) {
      loadMovies();
      return;
    }

    try {
      setLoading(true);
      const response = await movieAPI.getByYear(parseInt(filterYear));
      setMovies(response.data);
    } catch (error) {
      toast.error('Filter failed');
      console.error('Error filtering movies:', error);
    } finally {
      setLoading(false);
    }
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
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>🎬 Movie Collection</h2>
        <Link to="/admin/movies/new">
          <Button variant="primary">
            <FaPlus className="me-2" />
            Add Movie
          </Button>
        </Link>
      </div>

      {/* Search and Filters */}
      <Card className="mb-4">
        <Card.Body>
          <Row>
            <Col md={4}>
              <Form.Group className="mb-3">
                <Form.Label>Search Movies</Form.Label>
                <div className="d-flex">
                  <Form.Control
                    type="text"
                    placeholder="Search by title, description, or director..."
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
            <Col md={4}>
              <Form.Group className="mb-3">
                <Form.Label>Filter by Genre</Form.Label>
                <div className="d-flex">
                  <Form.Control
                    type="text"
                    placeholder="Enter genre..."
                    value={filterGenre}
                    onChange={(e) => setFilterGenre(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleFilterByGenre()}
                  />
                  <Button variant="outline-secondary" onClick={handleFilterByGenre}>
                    Filter
                  </Button>
                </div>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group className="mb-3">
                <Form.Label>Filter by Year</Form.Label>
                <div className="d-flex">
                  <Form.Control
                    type="number"
                    placeholder="Enter year..."
                    value={filterYear}
                    onChange={(e) => setFilterYear(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleFilterByYear()}
                  />
                  <Button variant="outline-secondary" onClick={handleFilterByYear}>
                    Filter
                  </Button>
                </div>
              </Form.Group>
            </Col>
          </Row>
          <Button variant="outline-primary" onClick={loadMovies}>
            Clear Filters
          </Button>
        </Card.Body>
      </Card>

      {/* Movies Grid */}
      <Row>
        {movies.map((movie) => (
          <Col key={movie.id} lg={4} md={6} className="mb-4">
            <Card className="h-100">
              <Card.Body>
                <Card.Title>{movie.title}</Card.Title>
                <Card.Subtitle className="mb-2 text-muted">
                  {movie.director} • {movie.releaseYear}
                </Card.Subtitle>
                <Badge bg="secondary" className="mb-2">
                  {movie.genre}
                </Badge>
                <Card.Text>{movie.description}</Card.Text>
                
                <div className="mb-3">
                  <strong>Average Rating:</strong>
                  <div className="mt-1">
                    {renderStars(Math.round(averageRatings[movie.id] || 0))}
                    <span className="ms-2">
                      ({averageRatings[movie.id]?.toFixed(1) || '0.0'}/5.0)
                    </span>
                  </div>
                </div>

                <div className="d-flex justify-content-between">
                  <Link to={`/feedback/new/${movie.id}`}>
                    <Button variant="success" size="sm">
                      Add Feedback
                    </Button>
                  </Link>
                  <Link to={`/admin/movies/edit/${movie.id}`}>
                    <Button variant="outline-primary" size="sm">
                      Edit
                    </Button>
                  </Link>
                </div>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>

      {movies.length === 0 && (
        <div className="text-center mt-5">
          <h4>No movies found</h4>
          <p>Try adjusting your search or filters</p>
        </div>
      )}
    </div>
  );
};

export default MovieList; 