import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../assets/css/App.css';
import '../assets/css/Groupepage.css';

function Groups() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user'));
  const [groups, setGroups] = useState([]);
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const userId = user.id;

  useEffect(() => {
    const fetchGroups = async () => {
      try {
          const response = await fetch(`http://176.189.185.253:5000/useringroup/user/${userId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setGroups(data);
        } else {
          console.error('Failed to fetch groups');
        }
      } catch (error) {
        console.error('An error occurred:', error);
      }
    };

    fetchGroups();
  }, [token, userId]);

  return (
    <div className="groups-container">
      <div>
        <button className="main-button" onClick={() => navigate('/Creategroup')}>Add Group</button>
      </div>
      {groups.map(group => (
        <div key={group.id} className="group-card">
          <h2>{group.groupName}</h2>
          <p>{group.groupDesc}</p>
          <button className="main-button" onClick={() => navigate(`/group/${group.id}`)}>View</button>
        </div>
      ))}
    </div>
  );
}

export default Groups;
