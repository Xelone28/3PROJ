import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Creategroup() {
  const user = JSON.parse(localStorage.getItem('user'));
  const [members, setMembers] = useState(['']);
  const [groupName, setGroupName] = useState('');
  const [groupDesc, setGroupDesc] = useState('');
  const navigate = useNavigate();
  const userId = user.id;
  const token = document.cookie
  .split('; ')
  .find(row => row.startsWith('token='))
  .split('=')[1];

  const handleSubmit = async (event) => {
    event.preventDefault();

    const response = await fetch('http://localhost:5000/group', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        groupName: groupName, 
        groupDesc: groupDesc,
      }),
    }); 
    try {
      alert('Group created successfully!');
      navigate('/groups');
    }
    catch (error) {
      console.error('Error handling response:', error);
      alert('An error occurred while processing your request.');
    }
  };

  return (
    <div>
      <h2>Create Group</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Group name:
          <input type="text" name="title" value={groupName} onChange={e => setGroupName(e.target.value)} />
        </label>
        <label>
          Description:
          <input type="text" name="description"value={groupDesc} onChange={e => setGroupDesc(e.target.value)} />
        </label>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
}

export default Creategroup;