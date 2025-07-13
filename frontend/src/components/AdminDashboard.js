import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, Row, Col, Button, Table, Badge, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { FaPlus, FaEdit, FaTrash, FaStar } from 'react-icons/fa';
import { movieAPI, feedbackAPI } from '../services/api';

const AdminDashboard = () => {
  const [movies, setMovies] = useState([]);
  const [feedback, setFeedback] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalMovies: 0,
    totalFeedback: 0,
    averageRating: 0
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // Load movies and feedback in parallel
      const [moviesResponse, feedbackResponse] = await Promise.all([
        movieAPI.getAll(),
        feedbackAPI.getAll()
      ]);
      
      setMovies(moviesResponse.data);
      setFeedback(feedbackResponse.data);
      
      // Calculate stats
      const totalMovies = moviesResponse.data.length;
      const totalFeedback = feedbackResponse.data.length;
      const averageRating = totalFeedback > 0 
        ? feedbackResponse.data.reduce((sum, f) => sum + f.rating, 0) / totalFeedback 
        : 0;
      
      setStats({
        totalMovies,
        totalFeedback,
        averageRating: Math.round(averageRating * 10) / 10
      });
      
    } catch (error) {
      toast.error('Failed to load dashboard data');
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteMovie = async (movieId, movieTitle) => {
    if (window.confirm(`Are you sure you want to delete "${movieTitle}"? This will also delete all associated feedback.`)) {
      try {
        await movieAPI.delete(movieId);
        toast.success('Movie deleted successfully');
        loadDashboardData();
      } catch (error) {
        toast.error('Failed to delete movie');
        console.error('Error deleting movie:', error);
      }
    }
  };

  const handleDeleteFeedback = async (feedbackId) => {
    if (window.confirm('Are you sure you want to delete this feedback?')) {
      try {
        await feedbackAPI.delete(feedbackId);
        toast.success('Feedback deleted successfully');
        loadDashboardData();
      } catch (error) {
        toast.error('Failed to delete feedback');
        console.error('Error deleting feedback:', error);
      }
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
        <h2>🔧 Admin Dashboard</h2>
        <Link to="/admin/movies/new">
          <Button variant="primary">
            <FaPlus className="me-2" />
            Add New Movie
          </Button>
        </Link>
      </div>

      {/* Statistics Cards */}
      <Row className="mb-4">
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>Total Movies</Card.Title>
              <h2 className="text-primary">{stats.totalMovies}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>Total Feedback</Card.Title>
              <h2 className="text-success">{stats.totalFeedback}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title>Average Rating</Card.Title>
              <h2 className="text-warning">
                {renderStars(Math.round(stats.averageRating))}
                <span className="ms-2">({stats.averageRating}/5.0)</span>
              </h2>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Movies Table */}
      <Card className="mb-4">
        <Card.Header>
          <h5>📽️ Movies Management</h5>
        </Card.Header>
        <Card.Body>
          {movies.length > 0 ? (
            <Table responsive striped>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Director</th>
                  <th>Genre</th>
                  <th>Year</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {movies.map((movie) => (
                  <tr key={movie.id}>
                    <td>
                      <strong>{movie.title}</strong>
                      <br />
                      <small className="text-muted">{movie.description.substring(0, 50)}...</small>
                    </td>
                    <td>{movie.director}</td>
                    <td>
                      <Badge bg="secondary">{movie.genre}</Badge>
                    </td>
                    <td>{movie.releaseYear}</td>
                    <td>
                      <Link to={`/admin/movies/edit/${movie.id}`}>
                        <Button variant="outline-primary" size="sm" className="me-2">
                          <FaEdit />
                        </Button>
                      </Link>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        onClick={() => handleDeleteMovie(movie.id, movie.title)}
                      >
                        <FaTrash />
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <p className="text-center text-muted">No movies found. Add your first movie!</p>
          )}
        </Card.Body>
      </Card>

      {/* Feedback Table */}
      <Card>
        <Card.Header>
          <h5>💬 Recent Feedback</h5>
        </Card.Header>
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
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {feedback.slice(0, 10).map((item) => (
                  <tr key={item.id}>
                    <td>
                      <strong>{item.visitorName}</strong>
                      {item.visitorEmail && (
                        <br />
                        <small className="text-muted">{item.visitorEmail}</small>
                      )}
                    </td>
                    <td>
                      {movies.find(m => m.id === item.movieId)?.title || `Movie ID: ${item.movieId}`}
                    </td>
                    <td>
                      {renderStars(item.rating)}
                      <span className="ms-1">({item.rating}/5)</span>
                    </td>
                    <td>
                      <small>{item.comment.substring(0, 50)}...</small>
                    </td>
                    <td>
                      <small>{new Date(item.createdAt).toLocaleDateString()}</small>
                    </td>
                    <td>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        onClick={() => handleDeleteFeedback(item.id)}
                      >
                        <FaTrash />
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <p className="text-center text-muted">No feedback found yet.</p>
          )}
        </Card.Body>
      </Card>
    </div>
  );
};

export default AdminDashboard; 