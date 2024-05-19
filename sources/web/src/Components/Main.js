import React from 'react';
import { Link } from 'react-router-dom';
import '../assets/css/Mainpage.css';
import '../assets/css/App.css';

function Main() {
  return (
    <div className="main-page">
      <h1>Welcome to Ratcord</h1>
      <div className="button-container">
        <Link to="/login">
          <button className="main-button">Sign In</button>
        </Link>
        <Link to="/signup">
          <button className="main-button">Register</button>
        </Link>
      </div>
    </div>
  );
}

export default Main;
