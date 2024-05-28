import React, { useState, useEffect } from 'react';
import Chat from './Chat';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import '../assets/css/App.css';

const InterfaceChat = () => {
  const navigate = useNavigate();
  const [currentUsername, setCurrentUsername] = useState('');
  const [currentUserId, setCurrentUserId] = useState(null);
  const [users, setUsers] = useState([]);
  const { groupId } = useParams();

  const user = JSON.parse(localStorage.getItem('user'));
  const tokenRow = document.cookie.split('; ').find(row => row.startsWith('token='));
  const token = tokenRow ? tokenRow.split('=')[1] : null;

  useEffect(() => {
    if (user && token) {
        fetch(`http://176.189.185.253:5000/api/users/${currentUserId}`, {
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

    if (groupId && token) {
        fetch(`http://176.189.185.253:5000/useringroup/${user.id}/groupusers`, {
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
  }, [user, token, groupId, currentUserId]);

  return (
    <div>
            <button className="main-button" onClick={() => navigate(`/group/${groupId}`)}>Back</button>
      <h2>Group Chat</h2>
      <ul>
        {users.map(user => (
          <li key={user.id}>{user.username}</li>
        ))}
      </ul>
      <Chat username={currentUsername} roomName={`GroupMessages${groupId}`} />
    </div>
  );
};

export default InterfaceChat;
