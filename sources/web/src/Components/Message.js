import React, { useState, useEffect } from 'react';
import io from 'socket.io-client';

const socket = io('http://localhost:4000');

function Message() {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [room, setRoom] = useState('');

  useEffect(() => {
    socket.on('message', (message) => {
      setMessages((messages) => [...messages, message]);
    });
  }, []);

  const joinRoom = () => {
    socket.emit('joinRoom', { room });
  };

  const sendMessage = () => {
    socket.emit('sendMessage', { content: message, sender: 'User1', room });
    setMessage('');
  };

  return (
    <div>
      <input type="text" value={room} onChange={(e) => setRoom(e.target.value)} placeholder="Room" />
      <button onClick={joinRoom}>Join Room</button>
      <input type="text" value={message} onChange={(e) => setMessage(e.target.value)} placeholder="Message" />
      <button onClick={sendMessage}>Send</button>
      <ul>
        {messages.map((msg, index) => (
          <li key={index}>{msg.sender}: {msg.content}</li>
        ))}
      </ul>
    </div>
  );
}

export default Message;