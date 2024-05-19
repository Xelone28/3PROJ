import React, { useState, useEffect } from 'react';
import PrivateMessageHandler from './PrivateMessageHandler';

const PrivateChat = () => {
  const [selectedUser, setSelectedUser] = useState(null);
  const [currentUsername, setCurrentUsername] = useState('');
  const [currentUserId, setCurrentUserId] = useState(null);
  const [users, setUsers] = useState([]);

  // Get the user and token from localStorage and cookies
  const user = JSON.parse(localStorage.getItem('user'));
  const tokenRow = document.cookie.split('; ').find(row => row.startsWith('token='));
  const token = tokenRow ? tokenRow.split('=')[1] : null;

  useEffect(() => {
    const fetchCurrentUser = async () => {
      if (user && token) {
        try {
          const response = await fetch(`http://localhost:5000/api/users/${user.id}`, {
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
          const response = await fetch(`http://localhost:5000/useringroup/${user.id}/groupusers`, {
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
  }, [user, token]);

  const generatePrivateChatId = (userId1, userId2) => {
    const sortedIds = [userId1, userId2].sort();
    return `PrivateChat${sortedIds[0]}${sortedIds[1]}`;
  };

  return (
    <div>
      {selectedUser ? (
        <PrivateMessageHandler 
          sender={currentUsername} 
          recipient={generatePrivateChatId(currentUserId, selectedUser.id)} 
        />
      ) : (
        <div>
          <h2>Select a user to chat with</h2>
          <ul>
            {users.length > 0 ? (
              users.map((user) => (
                <li key={user.id} onClick={() => setSelectedUser(user)}>
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
