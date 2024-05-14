import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';

function CreateExpense() {
  const { groupId } = useParams(); // Retrieve groupId from URL parameters
  const user = JSON.parse(localStorage.getItem('user')); // Retrieve user from localStorage
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1]; // Get the auth token from cookies
  const navigate = useNavigate(); // Correct usage of useNavigate

  const [users, setUsers] = useState([]);
  const [categories, setCategories] = useState([]);
  const [expense, setExpense] = useState({
    userId: user.id.toString(),
    groupId,
    userIdInvolved: [],
    categoryId: '',
    amount: '',
    date: '',
    place: '',
    description: ''
  });

  useEffect(() => {
    // Fetch users and categories from the server
    fetch(`http://localhost:5000/useringroup/users/${groupId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => setUsers(data))
      .catch(err => console.error('Error fetching users:', err));

    fetch(`http://localhost:5000/category/group/${groupId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(response => response.json())
      .then(data => setCategories(data))
      .catch(err => console.error('Error fetching categories:', err));
  }, [groupId, token]);

  const handleInputChange = event => {
    const { name, value } = event.target;
    setExpense(prevExpense => ({ ...prevExpense, [name]: value }));
  };

  const handleCheckboxChange = event => {
    const userId = parseInt(event.target.value, 10);
    setExpense(prevExpense => ({
      ...prevExpense,
      userIdInvolved: event.target.checked
        ? [...prevExpense.userIdInvolved, userId]
        : prevExpense.userIdInvolved.filter(id => id !== userId)
    }));
  };

  const handleSubmit = event => {
    event.preventDefault();
  
    // Convert the date to a Unix timestamp
    expense.date = Math.floor(new Date(expense.date).getTime() / 1000);
  
    // Prepare the payload
    const payload = JSON.stringify(expense);
  
    // Log the payload to see what is being sent
    console.log("Sending Expense:", payload);
  
    fetch('http://localhost:5000/expense', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
      },
      body: payload // Use the payload here instead of the original expense object
    })
    .then(response => {
      if (!response.ok) {
        // Log more detailed response for debugging
        response.json().then(data => console.error('Response:', data));
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(() => navigate(`/group/${groupId}`)) // Navigate on successful creation
    .catch(error => console.error('There has been a problem with your fetch operation:', error));
  };

  return (
    <form onSubmit={handleSubmit}>
      {users.map(user => (
        <label key={user.userId}>
          <input type="checkbox" value={user.userId} onChange={handleCheckboxChange} />
          {user.username}
        </label>
      ))}
      <select name="categoryId" onChange={handleInputChange} required>
        <option value="">Select Category</option>
        {categories.map(category => (
          <option key={category.id} value={category.id}>{category.name}</option>
        ))}
      </select>
      <input type="number" name="amount" placeholder="Amount" onChange={handleInputChange} required />
      <input type="date" name="date" onChange={handleInputChange} required />
      <input type="text" name="place" placeholder="Place" onChange={handleInputChange} required />
      <textarea name="description" placeholder="Description" onChange={handleInputChange} required />
      <button type="submit">Create Expense</button>
    </form>
  );
}

export default CreateExpense;
