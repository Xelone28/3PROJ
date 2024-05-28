import React, { useState, useEffect } from 'react';
import io from 'socket.io-client';
import '../assets/css/App.css';


const socket = io('http://176.189.185.253:4000'); 

const Chat = ({ username, roomName = null, privateRecipient = null }) => {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);

  const displayDateFormat = (date) => new Date(date).toLocaleString('en-GB', { hour12: false });

  useEffect(() => {
    socket.connect();

    const handleConnect = () => {
      console.log('Socket connected');
      if (roomName) {
        socket.emit('joinRoom', { room: roomName });
        console.log(`Joined Room: ${roomName}`);
      }
    };

    const handleConnectError = (error) => {
      console.error('Socket connection error:', error);
    };

    const handleGroupMessage = (data) => {
      const { sender, content, createdAt } = data;
      const formattedDate = displayDateFormat(createdAt);
      console.log(`Received message from ${sender}: ${content} at ${formattedDate}`);
      setMessages((prevMessages) => [...prevMessages, `${sender} [${formattedDate}]: ${content}`]);
    };

    const handleOldMessages = (data) => {
      const oldMessages = data.map((msg) => {
        const { sender, content, createdAt } = msg;
        const formattedDate = displayDateFormat(createdAt);
        return `${sender} [${formattedDate}]: ${content}`;
      });
      setMessages((prevMessages) => [...oldMessages, ...prevMessages]);
    };

    socket.on('connect', handleConnect);
    socket.on('connect_error', handleConnectError);
    socket.on('message', handleGroupMessage);
    socket.on('oldMessages', handleOldMessages);

    return () => {
      socket.off('connect', handleConnect);
      socket.off('connect_error', handleConnectError);
      socket.off('message', handleGroupMessage);
      socket.off('oldMessages', handleOldMessages);
      socket.disconnect();
    };
  }, [roomName]);

  const sendMessage = () => {
    if (roomName) {
      socket.emit('sendGroupMessage', {
        content: message,
        room: roomName,
        sender: username,
      });
      console.log(`Send Message: ${message} to Room: ${roomName}`);
      setMessage('');
    }
  };

  return (
    <div style={{ padding: '16px', width: '100%' }}>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="Message"
        style={{ width: '100%', marginBottom: '8px' }}
      />
      <button className="main-button" onClick={sendMessage} style={{ width: '100%', marginBottom: '16px' }}>
        Send
      </button>
      <div style={{ width: '100%', height: '60vh', overflowY: 'scroll' }}>
        {messages.map((msg, index) => (
          <div key={index}>{msg}</div>
        ))}
      </div>
    </div>
  );
};

export default Chat;
