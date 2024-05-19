import React, { useState } from 'react';
import Chat from './Chat';
import PrivateMessageHandler from './PrivateMessageHandler';
import { useParams } from 'react-router-dom';


const InterfaceChat = () => {
  const [selectedUser, setSelectedUser] = useState(null);
  const username = 'User1'; // Replace with the logged-in user's username
  const users = ['User2', 'User3', 'User4']; // remplacer la liste, et la remplacer par user que tu connais --> http://localhost:5000/useringroup/$userId/groupusers
  const { groupId } = useParams();
  const storedUser = "TO_CHANGE_FOR_REAL_USERNAME" // HELP ME

  return (
    <div>
      {selectedUser ? (
        //<PrivateMessageHandler sender={username} recipient={selectedUser} />

        <PrivateMessageHandler sender={username} recipient={"PrivateChat13"} />
      ) : (
        <div>
          <h2>Select a user to chat with</h2>
          <ul>
            {users.map((user) => (
              <li key={user} onClick={() => 
                {
                  //récupère l'Id du user selectionner
                  //il faut que tu formes la chaine PrivateChat12
                  //Pour créer ca, il faut que tu append au string PrivateChat les deux id des users en ascendant
                  // une fois que ta fini ca, tu le paser a la place de user juste en dessous
                  setSelectedUser(user)
                }
                }> //
                {user}
              </li>
            ))}
          </ul>
          <Chat username={storedUser} roomName={"GroupMesages"+groupId} />
        </div>
      )}
    </div>
  );
};

export default InterfaceChat;
