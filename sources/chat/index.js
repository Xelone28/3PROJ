const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();
app.use(cors({ origin: 'http://localhost:3000' }));
const server = http.createServer(app);
const io = socketIo(server, {
  cors: {
    origin: "http://localhost:3000",
    methods: ["GET", "POST"]
  }
});

// Connect to MongoDB
mongoose.connect('mongodb://mongo:27017/chat', { useNewUrlParser: true, useUnifiedTopology: true });

const messageSchema = new mongoose.Schema({
  content: String,
  sender: String,
  recipient: String, // Add recipient for private messages
  room: String,
  createdAt: { type: Date, default: Date.now }
});

const Message = mongoose.model('Message', messageSchema);

app.use(express.static('public'));

io.on('connection', (socket) => {
  console.log('New client connected');

  socket.on('joinRoom', async (data) => {
    console.log('joinRoom event received with data:', data);
    const room = data.room;
    if (room) {
      socket.join(room);
      console.log(`User joined room: ${room}`);

      // Fetch old messages from the database
      const oldMessages = await Message.find({ room }).sort({ createdAt: 1 }).exec();

      // Send old messages to the user
      socket.emit('oldMessages', oldMessages);

    } else {
      console.log('Room name is empty, cannot join room');
    }
  });

  socket.on('sendGroupMessage', async (data) => {
    console.log('sendGroupMessage event received with data:', data);
    const { content, room, sender } = data;
    if (room) {
      const msg = new Message({ content, sender, room });
      await msg.save();
      io.to(room).emit('message', { sender, content, room, createdAt: msg.createdAt.toISOString() });
    } else {
      console.log('Room is empty, cannot send group message');
    }
  });

  socket.on('sendPrivateMessage', async (data) => {
    console.log('sendPrivateMessage event received with data:', data);
    const { content, sender, recipient } = data;
    if (recipient) {
      const msg = new Message({ content, sender, recipient });
      await msg.save();
      // Emit the message to the sender
      socket.emit('privateMessage', { sender, content, recipient, createdAt: msg.createdAt.toISOString() });

      // Emit the message to the recipient
      io.to(recipient).emit('privateMessage', { sender, content, recipient, createdAt: msg.createdAt.toISOString() });
    } else {
      console.log('Recipient is empty, cannot send private message');
    }
  });

  socket.on('fetchPrivateConversation', async (data) => {
    console.log('fetchPrivateConversation event received with data:', data);
    const { sender, recipient } = data;
    const oldMessages = await Message.find({
      $or: [
        { recipient, recipient },
        { sender: recipient, recipient: recipient }
      ]
    }).sort({ createdAt: 1 }).exec();

    // Send old messages back to the requesting client
    socket.emit('privateConversation', oldMessages);
  });

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

const PORT = process.env.PORT || 4000;
server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
