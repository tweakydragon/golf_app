import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import './Home.css'; // Import the CSS file

const Home = () => {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

  const fetchSessions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8080/api/sessions');
      setSessions(response.data);
    } catch (err) {
      console.error('Error fetching sessions:', err);
      setError('Failed to load sessions. Please try again later.');
    } finally {
      setLoading(false);
    }
  }, []);

  const searchSessions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let endpoint = 'http://localhost:8080/api/sessions';
      if (searchQuery.trim()) {
        endpoint = `http://localhost:8080/api/sessions/search?title=${encodeURIComponent(searchQuery.trim())}`;
      }
      const response = await axios.get(endpoint);
      setSessions(response.data);
    } catch (err) {
      console.error('Error searching sessions:', err);
      setError('Failed to search sessions. Please try again later.');
    } finally {
      setLoading(false);
    }
  }, [searchQuery]);

  useEffect(() => {
    const debounceTimeout = setTimeout(() => {
      if (searchQuery) {
        searchSessions();
      } else {
        fetchSessions();
      }
    }, 300); // 300ms debounce

    return () => clearTimeout(debounceTimeout);
  }, [searchQuery, fetchSessions, searchSessions]);

  const formatDate = (dateString) => {
    if (!dateString) return 'Unknown date';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  };

  return (
    <div className="home-container">
      <div className="header mb-4">
        <h1 className="display-5 text-center">Golf Shot Analysis</h1>
        <p className="text-muted text-center">View your golf shot data or upload new sessions</p>
      </div>

      <div className="card mb-4">
        <div className="card-header d-flex justify-content-between align-items-center">
          <h2 className="h5 mb-0">Recent Sessions</h2>
          <Link to="/upload" className="btn btn-primary btn-sm">Upload New</Link>
        </div>
        
        <div className="card-body">
          {loading && (
            <div className="text-center p-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
              <p className="mt-3">Loading sessions...</p>
            </div>
          )}
          {error && <div className="alert alert-danger">{error}</div>}
          {!loading && !error && sessions.length === 0 && (
            <div className="text-center p-5">
              <div className="mb-4">
                <i className="bi bi-file-earmark-excel" style={{ fontSize: '3rem' }}></i>
              </div>
              <h3>No Sessions Found</h3>
              <p>Upload your first Garmin R10 CSV file to get started.</p>
              <Link to="/upload" className="btn btn-primary">Upload CSV</Link>
            </div>
          )}
          {!loading && !error && sessions.length > 0 && (
            <div>
              <div className="mb-4">
                <div className="input-group">
                  <span className="input-group-text"><i className="bi bi-search"></i></span>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Search sessions by title..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>
              </div>
              
              <div className="list-group">
                {sessions.map(session => (
                  <Link
                    key={session.id}
                    to={`/sessions/${session.id}`}
                    className="list-group-item list-group-item-action"
                  >
                    <div className="d-flex w-100 justify-content-between">
                      <h5 className="mb-1">{session.title}</h5>
                      <small>{formatDate(session.uploadDate)}</small>
                    </div>
                    <p className="mb-1 text-muted">{session.location || 'No location specified'}</p>
                    <small>{session.shots?.length || 0} shots</small>
                  </Link>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;
