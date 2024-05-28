import React, { useState, useEffect } from 'react';
import PrivateMessageHandler from './PrivateMessageHandler';
import '../assets/css/App.css';

const PrivateChat = () => {
  const [selectedUser, setSelectedUser] = useState(null);
  const [currentUsername, setCurrentUsername] = useState('');
  const [currentUserId, setCurrentUserId] = useState(null);
  const [users, setUsers] = useState([]);

  const user = JSON.parse(localStorage.getItem('user')) || {};
  const token = document.cookie.split('; ').find(row => row.startsWith('token='))?.split('=')[1] || null;

  useEffect(() => {
    const fetchCurrentUser = async () => {
      if (user.id && token) {
        try {
            const response = await fetch(`http://
          :5000/api/users/${user.id}`, {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`
            }
          });
          if (!response.ok) {
            throw new Error(`Network response was not ok: ${response.statusText}`);
          }
          const data = await response.json();
          setCurrentUsername(data.username);
          setCurrentUserId(user.id);
        } catch (error) {
          console.error('Error fetching the current user:', error);
        }
      }
    };

    const fetchGroupUsers = async () => {
      if (token) {
        try {
            const response = await fetch(`http://176.189.185.253:5000/useringroup/${user.id}/groupusers`, {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`
            }
          });
          if (!response.ok) {
            throw new Error(`Network response was not ok: ${response.statusText}`);
          }
          const data = await response.json();
          if (Array.isArray(data)) {
            setUsers(data);
          } else {
            console.error('Unexpected data format:', data);
          }
        } catch (error) {
          console.error('Error fetching group users:', error);
        }
      }
    };

    fetchCurrentUser();
    fetchGroupUsers();
  }, [user.id, token]);

  const generatePrivateChatId = (userId1, userId2) => {
    const sortedIds = [userId1, userId2].sort();
    return `PrivateChat${sortedIds[0]}${sortedIds[1]}`;
  };

  return (
    <div className="private-chat-container">
      {selectedUser ? (
        <PrivateMessageHandler 
          sender={currentUsername} 
          recipient={generatePrivateChatId(currentUserId, selectedUser.id)} 
        />
      ) : (
        <div>
          <h2>Select a user to chat with</h2>
          <ul className="user-list">
            {users.length > 0 ? (
              users.map((user) => (
                <li 
                  key={user.id} 
                  className={selectedUser && selectedUser.id === user.id ? 'selected' : ''}
                  onClick={() => setSelectedUser(user)}
                >
                  {user.username}
                </li>
              ))
            ) : (
              <li>No users available</li>
            )}
          </ul>
        </div>
      )}
    </div>
  );
};

export default PrivateChat;
