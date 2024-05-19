// src/Components/Footer.js

import React from 'react';
import '../assets/css/Footer.css';


const Footer = () => {
  return (
    <footer className="footer">
      <p>&copy; {new Date().getFullYear()} Your Company. All rights reserved.</p>
    </footer>
  );
};

export default Footer;
