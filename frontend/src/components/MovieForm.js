import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Form, Button, Card, Row, Col, Alert, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { movieAPI } from '../services/api';

const MovieForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  const [loading, setLoading] = useState(isEditing);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    genre: '',
    releaseYear: new Date().getFullYear(),
    director: ''
  });

  useEffect(() => {
    if (isEditing) {
      loadMovie();
    }
  }, [id]);

  const loadMovie = async () => {
    try {
      const response = await movieAPI.getById(id);
      const movie = response.data;
      setFormData({
        title: movie.title,
        description: movie.description,
        genre: movie.genre,
        releaseYear: movie.releaseYear,
        director: movie.director
      });
    } catch (error) {
      toast.error('Failed to load movie details');
      navigate('/admin');
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title.trim() || !formData.description.trim() || 
        !formData.genre.trim() || !formData.director.trim()) {
      toast.error('Please fill in all required fields');
      return;
    }

    if (formData.releaseYear < 1888 || formData.releaseYear > new Date().getFullYear() + 1) {
      toast.error('Please enter a valid release year');
      return;
    }

    try {
      setSubmitting(true);
      
      if (isEditing) {
        await movieAPI.update(id, formData);
        toast.success('Movie updated successfully!');
      } else {
        await movieAPI.create(formData);
        toast.success('Movie created successfully!');
      }
      
      navigate('/admin');
    } catch (error) {
      toast.error(isEditing ? 'Failed to update movie' : 'Failed to create movie');
      console.error('Error saving movie:', error);
    } finally {
      setSubmitting(false);
    }
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
      <h2 className="mb-4">
        {isEditing ? '✏️ Edit Movie' : '➕ Add New Movie'}
      </h2>
      
      <Card>
        <Card.Body>
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={8}>
                <Form.Group className="mb-3">
                  <Form.Label>Movie Title *</Form.Label>
                  <Form.Control
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    placeholder="Enter movie title"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Release Year *</Form.Label>
                  <Form.Control
                    type="number"
                    name="releaseYear"
                    value={formData.releaseYear}
                    onChange={handleInputChange}
                    min="1888"
                    max={new Date().getFullYear() + 1}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Genre *</Form.Label>
                  <Form.Control
                    type="text"
                    name="genre"
                    value={formData.genre}
                    onChange={handleInputChange}
                    placeholder="e.g., Action, Drama, Comedy"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Director *</Form.Label>
                  <Form.Control
                    type="text"
                    name="director"
                    value={formData.director}
                    onChange={handleInputChange}
                    placeholder="Enter director name"
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Description *</Form.Label>
              <Form.Control
                as="textarea"
                rows={4}
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Enter movie description..."
                required
              />
            </Form.Group>

            <div className="d-flex justify-content-between">
              <Button variant="outline-secondary" onClick={() => navigate('/admin')}>
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
                    {isEditing ? 'Updating...' : 'Creating...'}
                  </>
                ) : (
                  isEditing ? 'Update Movie' : 'Create Movie'
                )}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

export default MovieForm; 