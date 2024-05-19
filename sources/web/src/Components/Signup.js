import React, { useState, useEffect } from 'react';
import '../assets/css/Signup.css';
import { useNavigate } from 'react-router-dom';
import '../assets/css/App.css';

const Signup = () => {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [image, setImage] = useState(null);
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

  const handleSubmit = (event) => {
    event.preventDefault();
    if (password !== confirmPassword) {
      showMessage("Passwords don't match", 'error-banner');
    } else if (!image) {
      showMessage('Please upload a profile image', 'error-banner');
    } else {
      const formData = new FormData();
      formData.append('Username', event.target.elements.username.value);
      formData.append('Email', event.target.elements.email.value);
      formData.append('Password', password);
      formData.append('Rib', event.target.elements.rib.value);
      formData.append('PaypalUsername', event.target.elements.paypal.value);
      formData.append('Image', image);

      fetch('http://localhost:5000/api/users', {
        method: 'POST',
        body: formData,
      })
        .then((response) => response.json())
        .then((data) => {
          if (data.id) {
            showMessage('User created successfully!', 'success-banner');
            setTimeout(() => {
              navigate('/login');
            }, 2000);
          } else {
            console.error('Server response:', data);
            showMessage('An error occurred while creating the user.', 'error-banner');
          }
        })
        .catch((error) => {
          console.error('Error:', error);
          showMessage('An error occurred while creating the user.', 'error-banner');
        });
    }
  };

  const showMessage = (message, className) => {
    setMessage(message);
    setBannerClass(className);
    setShowBanner(true);
  };

  const handleImageChange = (event) => {
    setImage(event.target.files[0]);
  };

  return (
    <div className="form-container">
      <h2>Signup</h2>
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <form onSubmit={handleSubmit} className="signup-form">
        <div className="form-row grid-container">
          <label>
            Email:
            <input type="text" name="email" required />
          </label>
          <label>
            Username:
            <input type="text" name="username" required />
          </label>
          <label>
            RIB Number:
            <input type="text" name="rib" required />
          </label>
          <label>
            Paypal Username:
            <input type="text" name="paypal" required />
          </label>
          <label>
            Password:
            <input type="password" name="password" onChange={(e) => setPassword(e.target.value)} required />
          </label>
          <label>
            Confirm Password:
            <input type="password" name="confirmPassword" onChange={(e) => setConfirmPassword(e.target.value)} required />
          </label>
          <label>
            Image:
            <input type="file" name="image" onChange={handleImageChange} required />
          </label>
        </div>
        <div className="form-row">
          <input type="submit" className="main-button" value="Submit" />
        </div>
      </form>
    </div>
  );
};

export default Signup;
