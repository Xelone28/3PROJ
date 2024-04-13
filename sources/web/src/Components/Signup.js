import React, { useState } from 'react';
import '../assets/css/Signup.css';

function Signup() {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    if (password !== confirmPassword) {
      alert("Passwords don't match");
    } else {
      // Submit form
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
}

export default Signup;