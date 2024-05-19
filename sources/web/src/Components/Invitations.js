import React, { useState, useEffect } from 'react';
import '../assets/css/Groupepage.css';
import '../assets/css/App.css';

function Creategroup() {
  const user = JSON.parse(localStorage.getItem('user'));
  const [invitations, setInvitations] = useState([]);
  const [message, setMessage] = useState(null);
  const [bannerClass, setBannerClass] = useState('');
  const [showBanner, setShowBanner] = useState(false);
  const userId = user.id;
  const tokenRow = document.cookie.split('; ').find(row => row.startsWith('token='));
  const token = tokenRow ? tokenRow.split('=')[1] : null;

  useEffect(() => {
    if (showBanner) {
      const timer = setTimeout(() => {
        setShowBanner(false);
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [showBanner]);

  const showErrorBanner = (message) => {
    setMessage(message);
    setBannerClass('error-banner');
    setShowBanner(true);
  };

  const showSuccessBanner = (message) => {
    setMessage(message);
    setBannerClass('success-banner');
    setShowBanner(true);
  };

  useEffect(() => {
    const fetchInvitations = async () => {
      const response = await fetch(`http://localhost:5000/useringroup/invitation/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        const invitationsWithGroupInfo = await Promise.all(
          data.map(async (invitation) => {
            const groupResponse = await fetch(`http://localhost:5000/group/${invitation.group.id}`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });

            if (groupResponse.ok) {
              const groupData = await groupResponse.json();
              return { ...invitation, groupName: groupData.groupName, groupDescription: groupData.groupDesc };
            } else {
              console.error('Failed to fetch group info');
              return invitation;
            }
          })
        );

        setInvitations(invitationsWithGroupInfo);
      } else {
        showErrorBanner('Failed to fetch invitations or no invitations found');
      }
    };

    fetchInvitations();
  }, [userId, token]);

  const acceptInvitation = async (groupId) => {
    const response = await fetch(`http://localhost:5000/useringroup/${userId}/${groupId}`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ IsActive: true }),
    });

    if (response.ok) {
      showSuccessBanner('Invitation accepted');
      setTimeout(() => window.location.reload(), 2000);
    } else {
      showErrorBanner('Failed to accept invitation');
    }
  };

  const refuseInvitation = async (groupId) => {
    const response = await fetch(`http://localhost:5000/useringroup/${userId}/${groupId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.ok) {
      showSuccessBanner('Invitation refused');
      setTimeout(() => window.location.reload(), 2000);
    } else {
      showErrorBanner('Failed to refuse invitation');
    }
  };

  return (
    <div className="invitations-container">
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <h2>Invitations</h2>
      {invitations.map((invitation, index) => {
        if (!invitation.group || !invitation.group.id) {
          console.error('Invalid invitation structure:', invitation);
          return null;
        }

        return (
          <div key={index} className="invitation-card">
            <p>Group: {invitation.groupName}</p>
            <p>Description: {invitation.groupDescription}</p>
            <p>Admin: {invitation.isGroupAdmin ? 'Yes' : 'No'}</p>
            <button className="main-button" onClick={() => acceptInvitation(invitation.group.id)}>Accept</button>
            <button className="refuse-button" onClick={() => refuseInvitation(invitation.group.id)}>Refuse</button>
          </div>
        );
      })}
    </div>
  );
}

export default Creategroup;
