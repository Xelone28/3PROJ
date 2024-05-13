import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';



function GroupPage() {
  const navigate = useNavigate();
  const { Id } = useParams();
  const [group, setGroup] = useState(null);
  const [users, setUsers] = useState([]);
  // const [userId, setUserId] = useState('');
  // const [isGroupAdmin, setIsGroupAdmin] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [groupName, setGroupName] = useState(group ? group.name : '');
  const [groupDesc, setGroupDesc] = useState(group ? group.description : '');
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const [categoryName, setCategoryName] = useState('');
  // const [categories, setCategories] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [inviteUserId, setInviteUserId] = useState('')
  const [isInviteUserAdmin, setIsInviteUserAdmin] = useState(false)

  


// -----------------------------get group info  ---------------------------------------
  useEffect(() => {
    // console.log('Id:', Id);
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


    // -----------------------------get categories---------------------------------------

    useEffect(() => {
      const fetchCategories = async () => {
        if (group) {  // Check if group is not null
          const response = await fetch(`http://localhost:5000/category/group/${group.id}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
          if (response.ok) {
            const data = await response.json();
            setCategories(data);
          } else {
            console.error(`Error: ${response.status}`);
          }
        }
      };
      fetchCategories();
    }, [group, token]); 

    const handleCategoryChange = (event) => {
      setSelectedCategory(event.target.value);
      console.log("Selected category ID: ", event.target.value);
    };

// -----------------------------delete group---------------------------------------

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

// -----------------------------get users in group---------------------------------------
  useEffect(() => {
    const fetchUsers = async () => {
      const response = await fetch(`http://localhost:5000/useringroup/users/${Id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
  
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
      }
    };
  
    fetchUsers();
  }, [Id,token]);
// -----------------------------modify group---------------------------------------
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
// -----------------------------create category---------------------------------------
  const handleCreateCategory = async () => {
    const response = await fetch(`http://localhost:5000/category`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        groupId: group.id,
        name: categoryName,
      }),
    });
  
    if (response.ok) {
      alert("good")
      window.location.reload();
    } else {
      alert("bad")
    }
  };
// -----------------------------show user info---------------------------------------

  const fetchUserData = async (userId) => {
    const response = await fetch(`http://localhost:5000/api/users/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
  
    if (response.ok) {
      const data = await response.json();
      setSelectedUser(data);
      setShowModal(true);
    } else {
      console.error(`Error: ${response.status}`);
    }
  };

// -----------------------------invite user---------------------------------------

  const handleInviteSubmit = async (event) => {
    event.preventDefault();
  
    const response = await fetch('http://localhost:5000/useringroup', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        userId: inviteUserId,
        GroupId: group.id,  // Use the Id of the current group
        IsGroupAdmin: isInviteUserAdmin,  // Replace with your actual value
      }),
    });
  
    if (response.ok) {
      alert('User invited successfully!');
      window.location.reload();
    } else {
      console.error('Failed to invite user:', await response.json());
    }
  };

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

      <input type="text" value={categoryName} onChange={e => setCategoryName(e.target.value)} placeholder="Category name" />
      <button onClick={handleCreateCategory}>Create Category</button>
      <h3>
      users in this group
      </h3>
      <ul>
  {users.map(user => (
    <li key={user.userId} onClick={() => fetchUserData(user.userId)}>
      {user.username}
    </li>
  ))}
</ul>

<h2>Invite User</h2>
    <form onSubmit={handleInviteSubmit}>
      <label>
        User ID to invite:
        <input type="text" value={inviteUserId} onChange={e => setInviteUserId(e.target.value)} />
      </label>
      <label>
          Set as admin:
          <input type="checkbox" checked={isInviteUserAdmin} onChange={e => setIsInviteUserAdmin(e.target.checked)} />
        </label>
      <input type="submit" value="Invite" />
    </form>

{showModal && selectedUser && (
  <div className="modal">
    <h2>{selectedUser.username}</h2>
    <p>Email: {selectedUser.email}</p>
    <p>RIB: {selectedUser.rib}</p>
    <p>Paypal Username: {selectedUser.paypalUsername}</p>
    <button onClick={() => setShowModal(false)}>Close</button>
  </div>
)}

<select value={selectedCategory} onChange={handleCategoryChange}>
  <option value="">Select a category</option>
  {categories.map((category) => (
    <option key={category.id} value={category.id}>
      {category.name}
    </option>
  ))}
</select>

    </div>
  );
}

export default GroupPage;