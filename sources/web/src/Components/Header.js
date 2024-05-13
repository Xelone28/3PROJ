import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

function Header({ user }) {
  const [username, setUsername] = useState('');

  useEffect(() => {
    const fetchUser = async () => {
      if (!user || !user.id) return;
      const response = await fetch(`http://localhost:5000/api/users/${user.id}`);
      const userData = await response.json();
      setUsername(userData.username);
    };

    // console.log('user prop:', user); // Check the value of the user prop
    fetchUser();
  }, [user]);

  // console.log('username:', username); // Check the value of the username

  return (
    <header className="App-header">
      <h1>My App {username && `- ${username}`}</h1>
      <nav>
        <Link to="/">Home</Link><br></br>
        {user ? (
          <>
            <Link to="/Groups">View Groups</Link><br></br>
            <Link to="/profile">Profile</Link><br></br>
            <Link to="/invitations">Invitations</Link><br></br>
            <Link to="/logout">Logout</Link><br></br>
          </>
        ) : (
          <>
            <Link to="/login">Log in</Link><br></br>
            <Link to="/signup">Sign up</Link><br></br>
          </>
        )}
      </nav>
    </header>
  );
}

export default Header;