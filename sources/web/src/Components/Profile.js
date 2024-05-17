import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const [user, setUser] = useState(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rib, setRib] = useState('');
  const [paypalUsername, setPaypalUsername] = useState('');
  const [image, setImage] = useState(null);

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
    event.preventDefault();
  
    const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('token='))
    .split('=')[1];
  
    const storedUser = JSON.parse(localStorage.getItem('user'));
  
    // Create a new FormData instance
    const formData = new FormData();
  
    // Append the fields to the form data
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
      body: formData, // Send the form data
    });
  
    if (!response.ok) {
      // Handle error
      console.error('Failed to update username');
      return;
    }
  
    // Fetch the updated user data
    const updatedResponse = await fetch(`http://localhost:5000/api/users/${storedUser.id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
    });
  
    const updatedUser = await updatedResponse.json();
    setUser(updatedUser);
    localStorage.setItem('user', JSON.stringify(updatedUser)); // Update the user in localStorage
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      {user.image && <img src={user.image} style={{width: "300px"}} alt="User" />}
      <h1>Profile</h1>
      <p>Username: {user.username}</p>
      <p>Email: {user.email}</p>
      <p>RIB: {user.rib}</p>
      <p>PayPal Username: {user.paypalUsername}</p>

      <form onSubmit={handleUpdate}>
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
  <button type="submit">Update</button>
</form>
      <button onClick={deleteUser}>Delete Account</button>
    </div>
  );
}

export default Profile;