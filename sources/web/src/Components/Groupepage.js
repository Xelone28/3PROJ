import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Bar } from 'react-chartjs-2';
import jsPDF from 'jspdf';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons';
import '../assets/css/App.css';
import '../assets/css/Groupepage.css';

const APP_NAME = 'Your App Name'; // Define your app name here

const GroupPage = () => {
  const { Id } = useParams();
  const navigate = useNavigate();
  const [group, setGroup] = useState(null);
  const [users, setUsers] = useState([]);
  const [debts, setDebts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [inviteUserId, setInviteUserId] = useState('');
  const [isInviteUserAdmin, setIsInviteUserAdmin] = useState(false);
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [message, setMessage] = useState(null);
  const [bannerClass, setBannerClass] = useState('');
  const [showBanner, setShowBanner] = useState(false);
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const user = JSON.parse(localStorage.getItem('user'));
  const userId = user.id;

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
    const fetchExpenses = async () => {
        const response = await fetch(`http://
      :5000/expense/group/${Id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setExpenses(data);
      } else {
        showErrorBanner(`Failed to fetch expenses: ${response.status}`);
      }
    };

    fetchExpenses();
  }, [token, Id]);

  useEffect(() => {
    const fetchGroup = async () => {
      try {
          const response = await fetch(`http://176.189.185.253:5000/group/${Id}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        const data = await response.json();
        setGroup(data);
      } catch (error) {
        showErrorBanner('Failed to fetch group');
      }
    };
    fetchGroup();
  }, [Id, token]);

  useEffect(() => {
    const fetchCategories = async () => {
        const response = await fetch(`http://176.189.185.253:5000/category/group/${Id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setCategories(data);
      } else {
        showErrorBanner(`Failed to fetch categories: ${response.status}`);
      }
    };

    fetchCategories();
  }, [token, Id]);

  useEffect(() => {
    const fetchUsers = async () => {
        const response = await fetch(`http://176.189.185.253:5000/useringroup/users/${Id}`, {
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
        const response = await fetch(`http://176.189.185.253:5000/DebtAdjustment/group/${Id}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDebts(data);
      } else {
        showErrorBanner(`Failed to fetch debts: ${response.status}`);
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

  const fetchUserData = async (userId) => {
      const response = await fetch(`http://176.189.185.253:5000/api/users/${userId}`, {
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
      showErrorBanner(`Error: ${response.status}`);
    }
  };

  const handleInviteSubmit = async (event) => {
    event.preventDefault();

      const response = await fetch('http://176.189.185.253:5000/useringroup', {
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
      showSuccessBanner('User invited successfully!');
      setTimeout(() => {
        window.location.reload();
      }, 2000);
    } else {
      showErrorBanner('Failed to invite user');
    }
  };

  const handleDeleteExpense = async (expenseId) => {
      const response = await fetch(`http://176.189.185.253:5000/expense/${expenseId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.ok) {
      showSuccessBanner('Expense deleted successfully!');
      setTimeout(() => {
        window.location.reload();
      }, 2000);
    } else {
      showErrorBanner(`Failed to delete expense: ${response.status}`);
    }
  };

  const generatePDF = () => {
    const doc = new jsPDF();

    // Add app name
    doc.setFontSize(18);
    doc.text(APP_NAME, 10, 10);

    doc.setFontSize(14);
    doc.text(`Group: ${group ? group.groupName : ''}`, 10, 20);
    doc.text(`Description: ${group ? group.groupDesc : ''}`, 10, 30);
    doc.text(`Total Expense: ${totalExpense}`, 10, 40);

    let yPosition = 50; // Starting y position for the expenses
    const lineSpacing = 10; // Spacing between each line of text

    expenses.forEach((expense, index) => {
        const category = categories.find(category => category.id === expense.categoryId);
        doc.text(`Expense ${index + 1}`, 10, yPosition);
        yPosition += lineSpacing;
        doc.text(`Amount: ${expense.amount}`, 20, yPosition);
        yPosition += lineSpacing;
        doc.text(`Place: ${expense.place}`, 20, yPosition);
        yPosition += lineSpacing;
        doc.text(`Date: ${new Date(expense.date * 1000).toLocaleDateString()}`, 20, yPosition);
        yPosition += lineSpacing;
        doc.text(`Category: ${category ? category.name : 'Unknown'}`, 20, yPosition);
        yPosition += lineSpacing;
        doc.text(`Created by: ${expense.user.username}`, 20, yPosition);
        yPosition += lineSpacing;
        yPosition += lineSpacing; // Extra space between expenses
    });

    doc.save('expenses.pdf');
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

  const totalExpense = expenses.reduce((total, expense) => total + expense.amount, 0);

  // Filter debts where userInDebtId is the same as the logged-in user ID
  const userDebts = debts.filter(debt => debt.userInDebtId === userId);

  // Create a mapping from user IDs to usernames
  const userIdToUsername = {};
  users.forEach(user => {
    userIdToUsername[user.userId] = user.username;
  });

  return (
    <div className="group-page-container">
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <h1>{group ? group.groupName : 'Loading...'}</h1>
      <p>{group ? group.groupDesc : 'Loading...'}</p>

      <div className="chart-container">
        <div style={{ height: '300px', width: '50%' }}>
          <Bar data={balanceData} options={options} />
        </div>
      </div>

      <button className="main-button" onClick={() => navigate(`/reimbursements/${Id}`)}>Reimbursements</button>

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
        <input className="main-button" type="submit" value="Invite" />
      </form>

      {showModal && selectedUser && (
        <div className="modal">
          <h2>{selectedUser.username}</h2>
          {selectedUser.image && <img src={selectedUser.image} style={{ width: '300px' }} alt="User" />}
          <p>Email: {selectedUser.email}</p>
          <p>RIB: {selectedUser.rib}</p>
          <p>Paypal Username: {selectedUser.paypalUsername}</p>
          <button className="main-button" onClick={() => setShowModal(false)}>Close</button>
        </div>
      )}

      <button className="main-button" onClick={() => navigate(`/createexpense/${group.id}`)}>Create Expense</button>
      {expenses.map(expense => {
        const category = categories.find(category => category.id === expense.categoryId);
        return (
          <div key={expense.id} className="expense">
            <p>Amount: {expense.amount}</p>
            <p>Place: {expense.place}</p>
            <p>Date: {new Date(expense.date * 1000).toLocaleDateString()}</p>
            <p>Category: {category ? category.name : 'Unknown'}</p>
            <p>Created by: {expense.user.username}</p>
            <button className="main-button" onClick={() => navigate(`/Expensepage/${expense.id}`)}>View</button>
            <button className="main-button" onClick={() => handleDeleteExpense(expense.id)}>Delete</button>
            <button className="main-button" onClick={() => navigate(`/editexpense/${expense.id}`)}>Edit</button>
          </div>
        );
      })}

      <h3>Total Expense: {totalExpense}</h3>
      <button className="main-button" onClick={generatePDF}>Generate PDF</button>
      <button className="main-button" onClick={() => navigate(`/Message/${Id}`)}>Group Chat</button>

      <h3>Your Debts</h3>
      <ul>
        {userDebts.map(debt => (
          <li key={debt.id}>
            You owe {debt.adjustmentAmount} to {userIdToUsername[debt.userInCreditId]} on {new Date(debt.adjustmentDate).toLocaleDateString()}
            <button className="main-button" onClick={() => navigate(`/reimburse/${debt.id}/${debt.adjustmentAmount}/${Id}`)}>Reimburse</button>
          </li>
        ))}
      </ul>
      <button className="gear-button" onClick={() => navigate(`/editgroup/${Id}`)}>
        <FontAwesomeIcon icon={faGear} />
      </button>
    </div>
  );
}

export default GroupPage;
