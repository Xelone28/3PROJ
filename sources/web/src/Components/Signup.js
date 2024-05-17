import React, { useState } from 'react';
import '../assets/css/Signup.css';
import { useNavigate } from 'react-router-dom';

const Signup = () => {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [image, setImage] = useState(null);  // New state variable for the image

  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();
    if (password !== confirmPassword ) {
      alert("Passwords don't match");
    } else if(!image){
      alert("upload a profile image");
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
      formData.append('Image', image);  // Add image


      // Send POST request
      fetch('http://localhost:5000/api/users', {
        method: 'POST',
        body: formData,
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
          console.log(data);
        }
      })
      .catch((error) => {
        console.error('Error:', error);
        alert('An error occurred while creating the user.');
      });
    }
  };

  const handleImageChange = (event) => {  // New function to handle image changes
    setImage(event.target.files[0]);
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