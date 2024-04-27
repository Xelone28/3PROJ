import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const [user, setUser] = useState(null);
  const [username, setUsername] = useState('');
  const navigate = useNavigate();


  const deleteUser = async () => {
    const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('token='))
    .split('=')[1];
    const storedUser = JSON.parse(localStorage.getItem('user'));
    const response = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
    });
  
    if (!response.ok) {
      // Handle error
      console.error('Failed to delete user');
      return;
    }
  
    // If the delete was successful, remove the user and token from localStorage
    localStorage.removeItem('user');
    document.cookie = "token=; Max-Age=0";
        navigate('/');
    window.location.reload();

  };

  useEffect(() => {
    const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('token='))
    .split('=')[1];
    const fetchUser = async () => {
      const storedUser = JSON.parse(localStorage.getItem('user'));
      const response = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });

      const data = await response.json();
      setUser(data);
    };

    fetchUser();
  }, []);

  const handleUpdate = async (event) => {
    const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('token='))
    .split('=')[1];
    event.preventDefault();

    const storedUser = JSON.parse(localStorage.getItem('user'));
    const response = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ Username: username }),
    });

    if (!response.ok) {
      // Handle error
      console.error('Failed to update username');
      return;
      
    }
    
    else {
      localStorage.removeItem('user');
      document.cookie = "token=; Max-Age=0";
      navigate('/login');
      window.location.reload();
    }

    // If the server returns an empty response, don't try to parse it as JSON
    // Instead, you can fetch the updated user data
    const updatedResponse = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
    });

    const updatedUser = await updatedResponse.json();
    setUser(updatedUser);
    
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>Profile</h1>
      <p>Username: {user.username}</p>
      <p>Email: {user.email}</p>
      <p>RIB: {user.rib}</p>
      <p>PayPal Username: {user.paypalUsername}</p>
      <form onSubmit={handleUpdate}>
        <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="New username" required />
        <button type="submit">Update username</button>
      </form>
      <button onClick={deleteUser}>Delete Account</button>

    </div>
  );
}

export default Profile;