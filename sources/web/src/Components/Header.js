import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../assets/css/Header.css';
import '../assets/css/App.css';

function Header({ user }) {
  const [username, setUsername] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const tokenCookie = document.cookie.split('; ').find(row => row.startsWith('token='));
  const token = tokenCookie ? tokenCookie.split('=')[1] : null;

  useEffect(() => {
    const fetchUser = async () => {
      if (!user || !user.id) return;
        const response = await fetch(`http://176.189.185.253:5000/api/users/${user.id}`, {
        headers: { 'Authorization': `Bearer ${token}` },
      });
      if (response.ok) {
        const userData = await response.json();
        setUsername(userData.username);
      }
    };
    fetchUser();
  }, [user, token]);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <header className="App-header">
      <h1>RATCORD</h1>
      {token && (
        <div className={`header-nav ${menuOpen ? 'show' : ''}`}>
          {token && (
            <div className="menu-button" onClick={toggleMenu}>
              <div></div>
              <div></div>
              <div></div>
            </div>
          )}
          <div className="nav-links">

            {user ? (
              <>
                <Link to="/profile">{username && `${username}`}</Link><br />
                <Link to="/Groups">View Groups</Link><br />
                <Link to="/Privatemessaging">Messages</Link><br />
                <Link to="/invitations">Invitations</Link><br />
                <Link to="/logout">Logout</Link><br />
              </>
            ) : (
              <>
                <Link to="/login">Log in</Link><br />
                <Link to="/signup">Sign up</Link><br />
              </>
            )}
          </div>
        </div>
      )}
    </header>
  );
}

export default Header;
