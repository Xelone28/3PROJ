import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Creategroup() {
  const [members, setMembers] = useState(['']);
  const [showMembers, setShowMembers] = useState(false);
  const [buttonText, setButtonText] = useState('Add users now');
  const [groupName, setGroupName] = useState('');
  const [groupDesc, setGroupDesc] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
  
    const response = await fetch('http://localhost:5000/group', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        groupName: groupName,
        groupDesc: groupDesc,
      }),
    });
  
    try {
      if (response.ok) {
        const data = await response.json();
        console.log(data);
        alert('Group created successfully!');
        navigate('/groups');
      } else {
        const errorData = await response.json(); // Only parse the JSON if needed for error handling
        console.error('Failed to create group:', errorData);
        alert('Failed to create group');
      }
    } catch (error) {
      console.error('Error handling response:', error);
      alert('An error occurred while processing your request.');
    }
  };

  const handleInputChange = (index, event) => {
    const values = [...members];
    values[index] = event.target.value;
    setMembers(values);
  };

  // const handleAddField = () => {
  //   setMembers([...members, '']);
  // };


  // const handleButtonClick = () => {
  //   if (showMembers) {
  //     setButtonText('Add users now');
  //     setMembers(['']);
  //   } else {
  //     setButtonText('Add users later');
  //   }
  //   setShowMembers(!showMembers);
  // };


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
        {/* {showMembers && members.map((member, index) => (
          <label key={index}>
            Member {index + 1}:
            <input type="text" name={`member${index}`} value={member} onChange={event => handleInputChange(index, event)} />
          </label>
        ))}
        {showMembers && <button type="button" onClick={handleAddField}>Add member</button>} */}
        {/* <button type="button" onClick={handleButtonClick}>{buttonText}</button> */}
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
}

export default Creategroup;