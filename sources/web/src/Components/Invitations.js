import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';

function Creategroup() {
  const user = JSON.parse(localStorage.getItem('user'));
//   const [members, setMembers] = useState(['']);
//   const [groupName, setGroupName] = useState('');
//   const [groupDesc, setGroupDesc] = useState('');
//   const navigate = useNavigate();
  const [invitations, setInvitations] = useState([]);
  const userId = user.id;
  let token;
  const tokenRow = document.cookie.split('; ').find(row => row.startsWith('token='));
  if (tokenRow) {
    token = tokenRow.split('=')[1];
  }
  // console.log(token);
  
  useEffect(() => {
    const fetchInvitations = async () => {
      const response = await fetch(`http://localhost:5000/useringroup/invitation/${userId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        // console.log('Received invitations:', data);

        const invitationsWithGroupInfo = await Promise.all(data.map(async (invitation) => {
          const groupResponse = await fetch(`http://localhost:5000/group/${invitation.group.id}`, {
            headers: {
              'Authorization': `Bearer ${token}`,
            },
          });

          const groupData = await groupResponse.json();

          if (groupResponse.ok) {
            return { ...invitation, groupName: groupData.groupName, groupDescription: groupData.groupDesc };
          } else {
            console.error('Failed to fetch group info:', groupData);
            return invitation;
          }
        }));

        setInvitations(invitationsWithGroupInfo);
      } else {
        console.error('Failed to fetch invitations:', await response.json());
      }
    };

    fetchInvitations();
  }, [userId, token]);

  const acceptInvitation = async (groupId) => {
    console.log(userId, groupId); // Log userId and groupId

    const response = await fetch(`http://localhost:5000/useringroup/${userId}/${groupId}`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ IsActive: true }),
    });

    if (response.ok) {
      // Refresh the invitations after accepting an invitation
      window.location.reload();
    } else {
      console.error('Failed to accept invitation');
    }
  };

  const refuseInvitation = async (groupId) => {
    const response = await fetch(`http://localhost:5000/useringroup/${userId}/${groupId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.ok) {
      // Refresh the invitations after refusing an invitation
      window.location.reload();
    } else {
      console.error('Failed to refuse invitation');
    }
  };

  return (
    <div>
      <h2>Invitations</h2>
      {invitations.map((invitation, index) => {
        // Log the structure of the invitation object
        console.log('Invitation:', invitation);

        // Ensure invitation.group exists before rendering
        if (!invitation.group || !invitation.group.id) {
          console.error('Invalid invitation structure:', invitation);
          return null;
        }

        return (
          <div key={index} style={{ border: '1px solid black', margin: '10px', padding: '10px' }}>
            <p>Group: {invitation.groupName}</p>
            <p>Description: {invitation.groupDescription}</p>
            <p>Admin: {invitation.isGroupAdmin ? 'Yes' : 'No'}</p>
            <button onClick={() => acceptInvitation(invitation.group.id)}>Accept</button>
            <button onClick={() => refuseInvitation(invitation.group.id)}>Refuse</button>
          </div>
        );
      })}
    </div>
  );
}

export default Creategroup;
