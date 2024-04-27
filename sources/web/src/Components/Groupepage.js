import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';


function GroupPage() {
  const navigate = useNavigate();
  const { Id } = useParams();
  const [group, setGroup] = useState(null);
  const [groupName, setGroupName] = useState(group ? group.name : '');
  const [groupDesc, setGroupDesc] = useState(group ? group.description : '');
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];


  useEffect(() => {
    console.log('Id:', Id);
    const fetchGroup = async () => {
      try {
        const response = await fetch(`http://localhost:5000/group/${Id}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const data = await response.json();
        setGroup(data);
      } catch (error) {
        console.error('Failed to fetch group:', error);
      }
      
    };
    fetchGroup();
  }, [Id, token]);

  const handleNameChange = (e) => {
    setGroupName(e.target.value);
  };

  const handleDescChange = (e) => {
    setGroupDesc(e.target.value);
  };


  const handleDelete = async () => {
    try {
      const response = await fetch(`http://localhost:5000/group/${Id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Redirect to /groups after deleting the group
      navigate('/groups');
    } catch (error) {
      console.error('Failed to delete group:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`http://localhost:5000/group/${Id}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          groupName,
          groupDesc
        })
      });
  
      if (response.ok) {
        const text = await response.text();
        if (text) {
          const data = JSON.parse(text);
          setGroup(data);
        }
      } else {
        console.error('Failed to update group:', response.status);
      }
  
      // Redirect to /groups
      navigate('/groups');
    } catch (error) {
      console.error('Failed to update group:', error);
    }
  };

  if (!group) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>{group.groupName}</h1>
      <p>{group.groupDesc}</p>
      <form onSubmit={handleSubmit}>
        <input type="text" value={groupName} onChange={handleNameChange} placeholder="New group name" />
        <input type="text" value={groupDesc} onChange={handleDescChange} placeholder="New group description" />
        <button type="submit">Update Group</button>
      </form>
      
      <button onClick={handleDelete}>Delete Group</button>
    </div>
  );
}

export default GroupPage;