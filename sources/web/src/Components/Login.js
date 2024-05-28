import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../assets/css/Signup.css';
import '../assets/css/App.css';

function Login({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState(null);
  const [bannerClass, setBannerClass] = useState('');
  const [showBanner, setShowBanner] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (showBanner) {
      const timer = setTimeout(() => {
        setShowBanner(false);
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [showBanner]);

  const showErrorBanner = (message) => {
    setMessage(message);
    setBannerClass('error-banner');
    setShowBanner(true);
  };

  const showSuccessBanner = (message) => {
    setMessage(message);
    setBannerClass('success-banner');
    setShowBanner(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

      const response = await fetch('http://176.189.185.253:5000/api/users/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ Email: email, Password: password }),
    });

    const data = await response.json();
    if (response.ok) {
      showSuccessBanner('Logged in successfully!');
      document.cookie = `token=${data.token}; path=/`;
      const newUser = { email: data.email, id: data.id };
      onLogin(newUser);
      setTimeout(() => {
        navigate('/groups');
      }, 2000);
    } else {
      showErrorBanner('Invalid username or password');
    }
  };

  return (
    <div>
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <input type="text" style={{ maxWidth: '50vh' }} value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
        <input type="password" style={{ maxWidth: '50vh' }} value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
        <button className="main-button " type="submit">Log in</button>
      </form>
    </div>
  );
}

export default Login;
