import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../assets/css/Creategroup.css';
import '../assets/css/App.css';

function Creategroup() {
  const [groupName, setGroupName] = useState('');
  const [groupDesc, setGroupDesc] = useState('');
  const [message, setMessage] = useState(null);
  const [bannerClass, setBannerClass] = useState('');
  const [showBanner, setShowBanner] = useState(false);
  const navigate = useNavigate();
  const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('token='))
    .split('=')[1];

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

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!groupName.trim()) {
      showErrorBanner('Group name is required');
      return;
    }

      const response = await fetch('http://176.189.185.253:5000/group', {
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

    if (!response.ok) {
      showErrorBanner(`HTTP error! status: ${response.status}`);
    } else {
      showSuccessBanner('Group created successfully!');
      setTimeout(() => {
        navigate('/groups');
      }, 2000);
    }
  };

  return (
    <div>
      <h2>Create Group</h2>
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <label>
          Group name:
          <input type="text" name="title" value={groupName} onChange={e => setGroupName(e.target.value)} required />
        </label>
        <label>
          Description:
          <input type="text" name="description" value={groupDesc} onChange={e => setGroupDesc(e.target.value)} />
        </label>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
}

export default Creategroup;
