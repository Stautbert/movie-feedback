import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Form, Button, Card, Row, Col, Alert, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { FaStar } from 'react-icons/fa';
import { movieAPI, feedbackAPI } from '../services/api';

const FeedbackForm = () => {
  const { movieId } = useParams();
  const navigate = useNavigate();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    visitorName: '',
    comment: '',
    rating: 5,
    visitorEmail: ''
  });

  useEffect(() => {
    loadMovie();
  }, [movieId]);

  const loadMovie = async () => {
    try {
      const response = await movieAPI.getById(movieId);
      setMovie(response.data);
    } catch (error) {
      toast.error('Failed to load movie details');
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleRatingChange = (rating) => {
    setFormData(prev => ({
      ...prev,
      rating: rating
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.visitorName.trim() || !formData.comment.trim()) {
      toast.error('Please fill in all required fields');
      return;
    }

    try {
      setSubmitting(true);
      const feedbackData = {
        ...formData,
        movieId: parseInt(movieId)
      };
      
      await feedbackAPI.create(feedbackData);
      toast.success('Feedback submitted successfully!');
      navigate('/');
    } catch (error) {
      toast.error('Failed to submit feedback');
      console.error('Error submitting feedback:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const renderStars = (rating) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <FaStar
          key={i}
          className={`star ${i <= rating ? 'text-warning' : 'text-muted'}`}
          style={{ cursor: 'pointer', fontSize: '1.5rem' }}
          onClick={() => handleRatingChange(i)}
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

  if (!movie) {
    return (
      <Alert variant="danger">
        Movie not found. <Button variant="link" onClick={() => navigate('/')}>Go back</Button>
      </Alert>
    );
  }

  return (
    <div>
      <h2 className="mb-4">📝 Add Feedback</h2>
      
      <Card className="mb-4">
        <Card.Body>
          <Card.Title>{movie.title}</Card.Title>
          <Card.Subtitle className="mb-2 text-muted">
            {movie.director} • {movie.releaseYear}
          </Card.Subtitle>
          <Card.Text>{movie.description}</Card.Text>
        </Card.Body>
      </Card>

      <Card>
        <Card.Body>
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Your Name *</Form.Label>
                  <Form.Control
                    type="text"
                    name="visitorName"
                    value={formData.visitorName}
                    onChange={handleInputChange}
                    placeholder="Enter your name"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Email (Optional)</Form.Label>
                  <Form.Control
                    type="email"
                    name="visitorEmail"
                    value={formData.visitorEmail}
                    onChange={handleInputChange}
                    placeholder="Enter your email"
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Rating *</Form.Label>
              <div className="d-flex align-items-center">
                {renderStars(formData.rating)}
                <span className="ms-3">
                  {formData.rating} out of 5 stars
                </span>
              </div>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Your Review *</Form.Label>
              <Form.Control
                as="textarea"
                rows={4}
                name="comment"
                value={formData.comment}
                onChange={handleInputChange}
                placeholder="Share your thoughts about this movie..."
                required
              />
            </Form.Group>

            <div className="d-flex justify-content-between">
              <Button variant="outline-secondary" onClick={() => navigate('/')}>
                Cancel
              </Button>
              <Button 
                variant="primary" 
                type="submit" 
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <Spinner animation="border" size="sm" className="me-2" />
                    Submitting...
                  </>
                ) : (
                  'Submit Feedback'
                )}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

export default FeedbackForm; 