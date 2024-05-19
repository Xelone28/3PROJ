import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../assets/css/App.css';
import '../assets/css/Profile.css';

function Profile() {
  const [user, setUser] = useState(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rib, setRib] = useState('');
  const [paypalUsername, setPaypalUsername] = useState('');
  const [image, setImage] = useState(null);

  const navigate = useNavigate();

  const getToken = () => {
    const tokenRow = document.cookie.split('; ').find(row => row.startsWith('token='));
    return tokenRow ? tokenRow.split('=')[1] : null;
  };

  const deleteUser = async () => {
    const token = getToken();
    const storedUser = JSON.parse(localStorage.getItem('user'));

    const response = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
    });

    if (!response.ok) {
      console.error('Failed to delete user');
      return;
    }

    localStorage.removeItem('user');
    document.cookie = "token=; Max-Age=0";
    navigate('/');
    window.location.reload();
  };

  useEffect(() => {
    const fetchUser = async () => {
      const token = getToken();
      const storedUser = JSON.parse(localStorage.getItem('user'));

      const response = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
      });

      if (!response.ok) {
        console.error('Failed to fetch user data');
        return;
      }

      const data = await response.json();
      setUser(data);
    };

    fetchUser();
  }, []);

  const handleUpdate = async (event) => {
    event.preventDefault();
    const token = getToken();
    const storedUser = JSON.parse(localStorage.getItem('user'));
    const formData = new FormData();

    formData.append('Username', username || user.username);
    formData.append('Password', password || user.password);
    formData.append('RIB', rib || user.rib);
    formData.append('PaypalUsername', paypalUsername || user.paypalUsername);
    if (image) {
      formData.append('Image', image, image.name);
    }

    const response = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData,
    });

    if (!response.ok) {
      console.error('Failed to update user');
      return;
    }

    const updatedResponse = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
    });

    if (!updatedResponse.ok) {
      console.error('Failed to fetch updated user data');
      return;
    }

    const updatedUser = await updatedResponse.json();
    setUser(updatedUser);
    localStorage.setItem('user', JSON.stringify(updatedUser));
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="profile-container">
      {user.image && <img src={user.image} className="profile-image" alt="User" />}
      <h1>Profile</h1>
      <div className="profile-info">
        <p>Username: {user.username}</p>
        <p>Email: {user.email}</p>
        <p>RIB: {user.rib}</p>
        <p>PayPal Username: {user.paypalUsername}</p>
        <p>Ratcord ID : {user.id}</p>
      </div>

      <form className="profile-form" onSubmit={handleUpdate}>
        <input
          type="text"
          name="username"
          defaultValue={user.username}
          onChange={e => setUsername(e.target.value)}
        />
        <input
          type="password"
          name="password"
          defaultValue={user.password}
          onChange={e => setPassword(e.target.value)}
        />
        <input
          type="text"
          name="rib"
          defaultValue={user.rib}
          onChange={e => setRib(e.target.value)}
        />
        <input
          type="text"
          name="paypalUsername"
          defaultValue={user.paypalUsername}
          onChange={e => setPaypalUsername(e.target.value)}
        />
        <input
          type="file"
          name="image"
          onChange={e => setImage(e.target.files[0])}
        />
        <button className="main-button" type="submit">Update</button>
      </form>
      <button className="delete-button" onClick={deleteUser}>Delete Account</button>
    </div>
  );
}

export default Profile;