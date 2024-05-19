import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../assets/css/App.css';
import '../assets/css/Groupepage.css';

function EditGroupPage() {
  const { Id } = useParams();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [groupName, setGroupName] = useState('');
  const [groupDesc, setGroupDesc] = useState('');
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const [message, setMessage] = useState(null);
  const [bannerClass, setBannerClass] = useState('');
  const [showBanner, setShowBanner] = useState(false);

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
    const fetchGroup = async () => {
      try {
        const response = await fetch(`http://localhost:5000/group/${Id}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        const data = await response.json();
        setGroup(data);
        setGroupName(data.name);
        setGroupDesc(data.description);
      } catch (error) {
        showErrorBanner('Failed to fetch group');
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
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      showSuccessBanner('Group deleted successfully!');
      setTimeout(() => {
        navigate('/groups');
      }, 2000);
    } catch (error) {
      showErrorBanner('Failed to delete group');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`http://localhost:5000/group/${Id}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          groupName,
          groupDesc,
        }),
      });

      if (response.ok) {
        const text = await response.text();
        if (text) {
          const data = JSON.parse(text);
          setGroup(data);
          showSuccessBanner('Group updated successfully!');
        }
      } else {
        showErrorBanner('Failed to update group');
      }

      setTimeout(() => {
        navigate('/groups');
      }, 2000);
    } catch (error) {
      showErrorBanner('Failed to update group');
    }
  };

  if (!group) {
    return <div>Loading...</div>;
  }

  return (
    <div className="group-page-container">
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <h1>Edit Group</h1>

      <form onSubmit={handleSubmit} className="form-container">
        <input type="text" value={groupName} onChange={handleNameChange} placeholder="New group name" />
        <input type="text" value={groupDesc} onChange={handleDescChange} placeholder="New group description" />
        <button className="main-button" type="submit">Update Group</button>
      </form>
      <button className="main-button" onClick={handleDelete}>Delete Group</button>
    </div>
  );
}

export default EditGroupPage;
