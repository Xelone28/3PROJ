import React, { useState, useEffect } from 'react';
import io from 'socket.io-client';
import '../assets/css/App.css';

const socket = io('http://176.189.185.253:4000');

function Message() {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [room, setRoom] = useState('');

  useEffect(() => {
    socket.on('message', (message) => {
      setMessages((messages) => [...messages, message]);
    });

    return () => {
      socket.off('message');
    };
  }, []);

  const joinRoom = () => {
    if (room) {
      socket.emit('joinRoom', { room });
    }
  };

  const sendMessage = () => {
    if (message && room) {
      socket.emit('sendMessage', { content: message, sender: 'User1', room });
      setMessage('');
    }
  };

  return (
    <div>
      <input 
        type="text" 
        value={room} 
        onChange={(e) => setRoom(e.target.value)} 
        placeholder="Room" 
      />
      <button className="main-button" onClick={joinRoom} disabled={!room}>Join Room</button>
      <input 
        type="text" 
        value={message} 
        onChange={(e) => setMessage(e.target.value)} 
        placeholder="Message" 
      />
      <button className="main-button" onClick={sendMessage} disabled={!message || !room}>Send</button>
      <ul>
        {messages.map((msg, index) => (
          <li key={index}>{msg.sender}: {msg.content}</li>
        ))}
      </ul>
    </div>
  );
}

export default Message;
