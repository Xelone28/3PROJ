import React, { useState, useEffect } from 'react';
import '../assets/css/Signup.css';
import { useNavigate } from 'react-router-dom';

const Signup = () => {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [image, setImage] = useState(null); // New state variable for the image
  const [message, setMessage] = useState(null); // State variable for banner message
  const [bannerClass, setBannerClass] = useState(''); // State variable for banner class
  const [showBanner, setShowBanner] = useState(false); // State variable for showing banner

  const navigate = useNavigate();

  useEffect(() => {
    if (showBanner) {
      const timer = setTimeout(() => {
        setShowBanner(false);
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [showBanner]);

  const handleSubmit = (event) => {
    event.preventDefault();
    if (password !== confirmPassword) {
      setMessage("Passwords don't match");
      setBannerClass('error-banner');
      setShowBanner(true);
    } else if (!image) {
      setMessage('Please upload a profile image');
      setBannerClass('error-banner');
      setShowBanner(true);
    } else {
      // Get form data
      const email = event.target.elements.email.value;
      const username = event.target.elements.username.value;
      const rib = event.target.elements.rib.value;
      const paypalUsername = event.target.elements.paypal.value;

      // Create FormData object
      const formData = new FormData();
      formData.append('Username', username);
      formData.append('Email', email);
      formData.append('Password', password);
      formData.append('Rib', rib);
      formData.append('PaypalUsername', paypalUsername);
      formData.append('Image', image); // Add image

      // Send POST request
      fetch('http://localhost:5000/api/users', {
        method: 'POST',
        body: formData,
      })
        .then((response) => response.json())
        .then((data) => {
          // Handle response data
          if (data.id) {
            setMessage('User created successfully!');
            setBannerClass('success-banner');
            setShowBanner(true);
            setTimeout(() => {
              navigate('/login');
            }, 5000);
          } else {
            console.error('Server response:', data);
            setMessage('An error occurred while creating the user.');
            setBannerClass('error-banner');
            setShowBanner(true);
          }
        })
        .catch((error) => {
          console.error('Error:', error);
          setMessage('An error occurred while creating the user.');
          setBannerClass('error-banner');
          setShowBanner(true);
        });
    }
  };

  const handleImageChange = (event) => {
    setImage(event.target.files[0]);
  };

  return (
    <div>
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
            <input type="email" name="email" />
          </label>
          <label>
            Username:
            <input type="text" name="username" />
          </label>
          <label>
            RIB:
            <input type="text" name="rib" />
          </label>
          <label>
            Paypal Username:
            <input type="text" name="paypal" />
          </label>
          <label>
            Password:
            <input type="password" name="password" onChange={(e) => setPassword(e.target.value)} />
          </label>
          <label>
            Confirm Password:
            <input type="password" name="confirmPassword" onChange={(e) => setConfirmPassword(e.target.value)} />
          </label>
          <label>
            Image:
            <input type="file" name="image" onChange={handleImageChange} />
          </label>
        </div>
        <div className="form-row">
          <input type="submit" value="Submit" />
        </div>
      </form>
    </div>
  );
};

export default Signup;
