import React, { useState } from 'react';

function Creategroup() {
  const [members, setMembers] = useState(['']);
  const [showMembers, setShowMembers] = useState(false);
  const [buttonText, setButtonText] = useState('Add users now');

  const handleInputChange = (index, event) => {
    const values = [...members];
    values[index] = event.target.value;
    setMembers(values);
  };

  const handleAddField = () => {
    setMembers([...members, '']);
  };

  const handleButtonClick = () => {
    if (showMembers) {
      setButtonText('Add users now');
      setMembers(['']);
    } else {
      setButtonText('Add users later');
    }
    setShowMembers(!showMembers);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    // Handle form submission
  };

  return (
    <div>
      <h2>Create Group</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Group name:
          <input type="text" name="title" />
        </label>
        <label>
          Description:
          <input type="text" name="description" />
        </label>
        {showMembers && members.map((member, index) => (
          <label key={index}>
            Member {index + 1}:
            <input type="text" name={`member${index}`} value={member} onChange={event => handleInputChange(index, event)} />
          </label>
        ))}
        {showMembers && <button type="button" onClick={handleAddField}>Add member</button>}
        <button type="button" onClick={handleButtonClick}>{buttonText}</button>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
}

export default Creategroup;