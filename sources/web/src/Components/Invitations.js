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
  const token = document.cookie
  .split('; ')
  .find(row => row.startsWith('token='))
  .split('=')[1];


  useEffect(() => {
    const fetchInvitations = async () => {
      const response = await fetch(`http://localhost:5000/useringroup/invitation/${userId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
  
      if (response.ok) {
        const data = await response.json();
  
        const invitationsWithGroupInfo = await Promise.all(data.map(async (invitation) => {
            const groupResponse = await fetch(`http://localhost:5000/group/${invitation.groupId}`, {
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

  const acceptInvitation = async (userId, groupId) => {
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
  
  const refuseInvitation = async (userId, groupId) => {
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
      {invitations.map((invitation, index) => (
        <div key={index} style={{ border: '1px solid black', margin: '10px', padding: '10px' }}>
          <p>Group: {invitation.groupName}</p>
          <p>Description: {invitation.groupDescription}</p>
          <p>Admin: {invitation.isGroupAdmin ? 'Yes' : 'No'}</p>
          <button onClick={() => acceptInvitation(invitation.userId, invitation.groupId)}>Accept</button>
          <button onClick={() => refuseInvitation(invitation.userId, invitation.groupId)}>Refuse</button>
        </div>
      ))}
    </div>
  );
}

export default Creategroup;