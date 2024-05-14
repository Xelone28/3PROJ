import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';



function GroupPage() {
  const { Id } = useParams();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [users, setUsers] = useState([]);
  // const [userId, setUserId] = useState('');
  // const [isGroupAdmin, setIsGroupAdmin] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [groupName, setGroupName] = useState(group ? group.name : '');
  const [groupDesc, setGroupDesc] = useState(group ? group.description : '');
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const [inviteUserId, setInviteUserId] = useState('')
  const [isInviteUserAdmin, setIsInviteUserAdmin] = useState(false)
  const [expenses, setExpenses] = useState([]);
  
  const [categories, setCategories] = useState([]);

  
  
// -----------------------------get expenses---------------------------------------
useEffect(() => {
  const fetchExpenses = async () => {
    const response = await fetch(`http://localhost:5000/expense/group/${Id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      setExpenses(data);
    } else {
      console.error(`Failed to fetch expenses: ${response.status}`);
    }
  };

  fetchExpenses();
}, [token, Id]);

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


  console.log('groupId:', Id);  // Add this line

  useEffect(() => {
    
  
    const fetchCategories = async () => {
      const response = await fetch(`http://localhost:5000/category/group/${Id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
  
      if (response.ok) {
        const data = await response.json();
        setCategories(data);
      } else {
        console.error(`Failed to fetch categories: ${response.status}`);
      }
    };
  
    fetchCategories();
  }, [token,Id]);



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


<button onClick={() => navigate(`/createexpense/${group.id}`)}>Create Expense</button>
{expenses.map(expense => {
  const user = users.find(user => user.userId === expense.userId);
  const category = categories.find(category => category.id === expense.categoryId);
  return (
    <div key={expense.id} className='expense' onClick={() => navigate(`/expensepage/${expense.id}`)}>
      <p>Amount: {expense.amount}</p>
      <p>Place: {expense.place}</p>
      <p>Description: {expense.description}</p>
      <p>Date: {new Date(expense.date * 1000).toLocaleDateString()}</p>
      <p>Category: {category ? category.name : 'Unknown'}</p>
      <p>Created by: {user ? user.username : 'Unknown'}</p>
    </div>
  );
})}

    </div>
  );
}

export default GroupPage;