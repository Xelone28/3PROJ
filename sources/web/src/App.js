import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
// import logo from '..assets/img/logo.svg';
import './assets/css/App.css';

// Import your Main, Login and Signup components
import Main from './Components/Main';
import Login from './Components/Login';
import Signup from './Components/Signup';
import Notfound from './Components/Notfound';
import Profile from './Components/Profile';
import Creategroup from './Components/Creategroup';

function App() {
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <nav>
            <Link to="/">Home</Link><br></br>
            <Link to="/login">Log in</Link><br></br>
            <Link to="/signup">Sign up</Link>
          </nav>
        </header>
        <Routes>
          <Route path="/" element={<Main />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/*" element={<Notfound/>} />
          <Route path="/profile" element={<Profile/>} />
          <Route path="/Creategroup" element={<Creategroup/>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;