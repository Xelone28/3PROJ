import React from 'react';
import { Link } from 'react-router-dom';

//  {user && `- ${user.username}`}  use to show username


function Header({ user }) {
  return (
    <header className="App-header">
      <h1>My App {user && `- ${user.username}`}</h1>
      <nav>
        <Link to="/">Home</Link><br></br>
        {user ? (
          <>
            <Link to="/profile">Profile</Link><br></br>
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