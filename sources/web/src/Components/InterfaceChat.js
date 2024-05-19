import React, { useState, useEffect } from 'react';
import Chat from './Chat';
import { useParams } from 'react-router-dom';

const InterfaceChat = () => {
  const [currentUsername, setCurrentUsername] = useState('');
  const [currentUserId, setCurrentUserId] = useState(null);
  const [users, setUsers] = useState([]);
  const { groupId } = useParams();

  // Get the user and token from localStorage and cookies
  const user = JSON.parse(localStorage.getItem('user'));
  const tokenRow = document.cookie.split('; ').find(row => row.startsWith('token='));
  const token = tokenRow ? tokenRow.split('=')[1] : null;

  useEffect(() => {
    // Fetch the current user's username using the userId and token
    if (user && token) {
      fetch(`http://localhost:5000/api/users/${user.id}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(data => {
          setCurrentUsername(data.username);
          setCurrentUserId(user.id);
        })
        .catch(error => {
          console.error('Error fetching the current user:', error);
        });
    }

    // Fetch the list of users in the group
    if (groupId && token) {
      fetch(`http://localhost:5000/useringroup/${user.id}/groupusers`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(data => {
          // console.log('Group users data:', data); // Log the returned data here
          if (Array.isArray(data)) {
            setUsers(data);
          } else {
            console.error('Unexpected data format:', data);
          }
        })
        .catch(error => {
          console.error('Error fetching group users:', error);
        });
    }
  }, [user, token, groupId]);

  return (
    <div>
      <h2>Group Chat</h2>
      <ul>
        {users.map((user) => (
          <li key={user.id}>{user.username}</li>
        ))}
      </ul>
      <Chat username={currentUsername} roomName={`GroupMessages${groupId}`} />
    </div>
  );
};

export default InterfaceChat;
