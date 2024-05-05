
import React, { useState } from 'react';
import '../assets/css/Signup.css';
import { useNavigate } from 'react-router-dom';
const Signup = () => {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const navigate = useNavigate();
  const handleSubmit = (event) => {
    event.preventDefault();
    if (password !== confirmPassword) {
      alert("Passwords don't match");
    } else {
      // Get form data
      const email = event.target.elements.email.value;
      const username = event.target.elements.username.value;
      const rib = event.target.elements.rib.value;
      const paypalUsername = event.target.elements.paypal.value;
  
      // Create user object
      const user = {
        Username: username,
        Email: email,
        Password: password,
        Rib: rib,
        PaypalUsername: paypalUsername
      };
  
      const userJson = JSON.stringify(user);
      console.log(userJson); // Log JSON string
  
      // Send POST request
      fetch('http://localhost:5000/api/users', {
        method: 'POST',
        
        headers: {
          'Content-Type': 'application/json',
        },
        body: userJson,
      })
      .then(response => response.json())
      .then(data => {
        // Handle response data
        if (data.id) {
          alert('User created successfully!');
          navigate('/login');
        } else {
          console.error('Server response:', data);
          alert('An error occurred while creating the user.');
        }
      })
      .catch((error) => {
        console.error('Error:', error);
        alert('An error occurred while creating the user.');
      });
    }
  };

  return (
    <div>
      <h2>Signup</h2>
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
            <input type="password" name="password" onChange={e => setPassword(e.target.value)} />
          </label>
          <label>
            Confirm Password:
            <input type="password" name="confirmPassword" onChange={e => setConfirmPassword(e.target.value)} />
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