import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function Groups() {
  const user = JSON.parse(localStorage.getItem('user'));
  const [groups, setGroups] = useState([]);
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  // console.log(token);
  const Id = user.id;

  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const response = await fetch(`http://localhost:5000/useringroup/user/${Id}`, {
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
  }, []);

  return (
    <div>
      <Link to="/Creategroup">
        <button>Add Group</button>
      </Link>
      {groups.map(group => (
      <Link to={`/group/${group.id}`} key={group.id}>
        <div className="group-card">
          <h2>{group.groupName}</h2>
          <p>{group.groupDesc}</p>
        </div>
      </Link>
    ))}
    </div>
  );
}

export default Groups;