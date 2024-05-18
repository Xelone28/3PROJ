import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { Bar } from 'react-chartjs-2';
import '../assets/css/Groupepage.css';

function GroupPage() {
  const { Id } = useParams();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [users, setUsers] = useState([]);
  const [debts, setDebts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [groupName, setGroupName] = useState(group ? group.name : '');
  const [groupDesc, setGroupDesc] = useState(group ? group.description : '');
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const [inviteUserId, setInviteUserId] = useState('');
  const [isInviteUserAdmin, setIsInviteUserAdmin] = useState(false);
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  
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
        console.log('expenses:', data);
      } else {
        console.error(`Failed to fetch expenses: ${response.status}`);
      }
    };

    fetchExpenses();
  }, [token, Id]);

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
      } catch (error) {
        console.error('Failed to fetch group:', error);
      }
    };
    fetchGroup();
  }, [Id, token]);

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
  }, [token, Id]);

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
      }
    };

    fetchUsers();
  }, [Id, token]);

  useEffect(() => {
    const fetchDebts = async () => {
      const response = await fetch(`http://localhost:5000/DebtAdjustment/group/${Id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDebts(data);
      } else {
        console.error(`Failed to fetch debts: ${response.status}`);
      }
    };

    fetchDebts();
  }, [token, Id]);

  const calculateBalances = () => {
    const balances = {};

    users.forEach(user => {
      balances[user.userId] = 0;
    });

    debts.forEach(debt => {
      balances[debt.userInCreditId] += debt.adjustmentAmount;
      balances[debt.userInDebtId] -= debt.adjustmentAmount;
    });

    return balances;
  };

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
        }
      } else {
        console.error('Failed to update group:', response.status);
      }

      navigate('/groups');
    } catch (error) {
      console.error('Failed to update group:', error);
    }
  };

  if (!group) {
    return <div>Loading...</div>;
  }

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
        GroupId: group.id,
        IsGroupAdmin: isInviteUserAdmin,
      }),
    });

    if (response.ok) {
      alert('User invited successfully!');
      window.location.reload();
    } else {
      console.error('Failed to invite user:', await response.json());
    }
  };

  const handleDeleteExpense = async (expenseId) => {
    const response = await fetch(`http://localhost:5000/expense/${expenseId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.ok) {
      alert('Expense deleted successfully!');
      window.location.reload();
    } else {
      console.error(`Error: ${response.status}`);
    }
  };

  const balances = calculateBalances();
  const balanceData = {
    labels: users.map(user => user.username),
    datasets: [
      {
        label: 'Balance',
        data: users.map(user => balances[user.userId] || 0),
        backgroundColor: users.map(user => (balances[user.userId] >= 0 ? 'rgba(75, 192, 192, 0.6)' : 'rgba(255, 99, 132, 0.6)')),
        borderColor: users.map(user => (balances[user.userId] >= 0 ? 'rgba(75, 192, 192, 1)' : 'rgba(255, 99, 132, 1)')),
        borderWidth: 1,
      },
    ],
  };

  const options = {
    indexAxis: 'y',
    scales: {
      x: {
        beginAtZero: true,
      },
    },
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
      tooltip: {
        callbacks: {
          label: function(tooltipItem) {
            return tooltipItem.dataset.label + ': ' + (tooltipItem.raw >= 0 ? 'Somme due' : 'Somme à payer') + ' ' + tooltipItem.raw;
          }
        }
      }
    }
  };

  return (
    <div>
      <h1>{group.groupName}</h1>
      <p>{group.groupDesc}</p>

      <div className='chart-container'>
        <div style={{ height: '300px', width: '50%' }}>
          <Bar data={balanceData} options={options} />
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <input type="text" value={groupName} onChange={handleNameChange} placeholder="New group name" />
        <input type="text" value={groupDesc} onChange={handleDescChange} placeholder="New group description" />
        <button type="submit">Update Group</button>
      </form>
      <button onClick={handleDelete}>Delete Group</button>

      <h3>Users in this group</h3>
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
          {selectedUser.image && <img src={selectedUser.image} style={{ width: "300px" }} alt="User" />}
          <p>Email: {selectedUser.email}</p>
          <p>RIB: {selectedUser.rib}</p>
          <p>Paypal Username: {selectedUser.paypalUsername}</p>
          <button onClick={() => setShowModal(false)}>Close</button>
        </div>
      )}

      <button onClick={() => navigate(`/createexpense/${group.id}`)}>Create Expense</button>
      {expenses.map(expense => {
        const category = categories.find(category => category.id === expense.categoryId);
        return (
          <div key={expense.id} className='expense'>
            <p>Amount: {expense.amount}</p>
            <p>Place: {expense.place}</p>
            <p>Date: {new Date(expense.date * 1000).toLocaleDateString()}</p>
            <p>Category: {category ? category.name : 'Unknown'}</p>
            <p>Created by: {expense.user.username}</p>
            <button onClick={() => navigate(`/Expensepage/${expense.id}`)}>View</button>
            <button onClick={() => handleDeleteExpense(expense.id)}>Delete</button>
            <button onClick={() => navigate(`/editexpense/${expense.id}`)}>Edit</button>
          </div>
        );
      })}


    </div>
  );
}

export default GroupPage;
