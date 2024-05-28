import React, { useState, useEffect } from 'react';
import io from 'socket.io-client';
import '../assets/css/App.css';

const socket = io('http://176.189.185.253:4000');

const PrivateMessageHandler = ({ sender, recipient }) => {
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  // const messageRef = useRef(null);

  const displayDateFormat = (date) => new Date(date).toLocaleString('en-GB', { hour12: false });

  useEffect(() => {
    socket.connect();

    const handlePrivateMessage = (data) => {
      const { sender, content, createdAt } = data;
      const formattedDate = displayDateFormat(createdAt);
      console.log(`Received private message from ${sender}: ${content} at ${formattedDate}`);
      setMessages((prevMessages) => [...prevMessages, `${sender} [${formattedDate}]: ${content}`]);
    };

    const handleOldPrivateMessages = (data) => {
      const oldMessages = data.map((msg) => {
        const { sender, content, createdAt } = msg;
        const formattedDate = displayDateFormat(createdAt);
        return `${sender} [${formattedDate}]: ${content}`;
      });
      setMessages((prevMessages) => [...oldMessages, ...prevMessages]);
    };

    socket.emit('fetchPrivateConversation', { sender, recipient });

    socket.on('privateMessage', handlePrivateMessage);
    socket.on('privateConversation', handleOldPrivateMessages);

    return () => {
      socket.off('privateMessage', handlePrivateMessage);
      socket.off('privateConversation', handleOldPrivateMessages);
      socket.disconnect();
    };
  }, [recipient,sender]);

  const sendPrivateMessage = () => {
    socket.emit('sendPrivateMessage', {
      content: message,
      recipient,
      sender,
    });
    console.log(`Send Private Message: ${message} to ${recipient}`);
    setMessage('');
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
      <button className="main-button" onClick={sendPrivateMessage} style={{ width: '100%', marginBottom: '16px' }}>
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

export default PrivateMessageHandler;
