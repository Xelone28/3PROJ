import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './assets/css/App.css';

import Main from './Components/Main';
import Login from './Components/Login';
import Signup from './Components/Signup';
import Notfound from './Components/Notfound';
import Profile from './Components/Profile';
import Creategroup from './Components/Creategroup';
import Header from './Components/Header';
import Logout from './Components/Logout';

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem('user'));
    if (storedUser) {
      setUser(storedUser);
    }
  }, []);

  const handleLogin = (newUser) => {
    localStorage.setItem('user', JSON.stringify(newUser));
    setUser(newUser);
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    setUser(null);
  };

  return (
    <Router>
      <div className="App">
        <Header user={user} />
        <Routes>
          <Route path="/" element={<Main />} />
          <Route path="/login" element={<Login onLogin={handleLogin} />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/logout" element={<Logout onLogout={handleLogout} />} />
          <Route path="/*" element={<Notfound/>} />
          <Route path="/profile" element={<Profile/>} />
          <Route path="/Creategroup" element={<Creategroup/>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;