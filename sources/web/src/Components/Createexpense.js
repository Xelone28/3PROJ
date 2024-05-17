import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';

function CreateExpense() {
  const { groupId } = useParams(); // Retrieve groupId from URL parameters
  const user = JSON.parse(localStorage.getItem('user')); // Retrieve user from localStorage
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1]; // Get the auth token from cookies
  const navigate = useNavigate(); 
  const [categoryName, setCategoryName] = useState('');
  const [users, setUsers] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [categories, setCategories] = useState([]);
  const [expense, setExpense] = useState({
    userId: user.id.toString(),
    groupId,
    UserIdInvolved: [],
    categoryId: '',
    amount: '',
    date: '',
    Place: '',
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
      UserIdInvolved: event.target.checked
        ? [...prevExpense.UserIdInvolved, userId]
        : prevExpense.UserIdInvolved.filter(id => id !== userId)
    }));
  };

  const handleSubmit = event => {
    event.preventDefault();
  
    // Convert the date to a Unix timestamp
    const expenseData = { 
      ...expense, 
      date: Math.floor(new Date(expense.date).getTime() / 1000),
      Place: expense.place, // Use 'Place' instead of 'place'
      UserIdInvolved: expense.UserIdInvolved.map(id => Number(id)) // Convert UserIdInvolved to an array of numbers
    };
  
    // Ensure UserIdInvolved is an array and Place is a non-empty string
    if (!Array.isArray(expenseData.UserIdInvolved) || expenseData.UserIdInvolved.length === 0) {
      alert('Please select at least one user involved in the expense.');
      return;
    }
    if (!expenseData.Place || expenseData.Place.trim() === '') {
      alert('Please provide a place for the expense.');
      return;
    }
  
    // Log the expense data to be sent
    console.log('Expense data:', expenseData);
  
    // Prepare the payload
    const formData = new FormData();
    formData.append('userId', expenseData.userId);
    formData.append('UserIdInvolved', expenseData.UserIdInvolved);
    formData.append('groupId', expenseData.groupId);
    formData.append('categoryId', expenseData.categoryId);
    formData.append('amount', expenseData.amount);
    formData.append('date', expenseData.date);
    formData.append('place', expenseData.place);
    formData.append('description', expenseData.description);
    formData.append('image', selectedFile);
    for (let pair of formData.entries()) {
      console.log(pair[0]+ ', ' + pair[1]); 
  }
  
    fetch('http://localhost:5000/expense', {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${token}` 
      },
      body: formData // Use the formData here instead of the original expense object
    })
    .then(response => response.json().then(data => ({ status: response.status, body: data })))
    .then(({ status, body }) => {
      if (status !== 201) {
        console.error('Response:', body);
        console.log(status)
        if (body.errors) {
          console.error('Errors:', body.errors);
        }
        throw new Error('Network response was not ok');
      }
      else{
        alert('Expense created successfully!');
        navigate(`/group/${groupId}`) // Navigate on successful creation
      }
      return body;
    })
    .then(() => navigate(`/group/${groupId}`)) // Navigate on successful creation
    .catch(error => console.error('There has been a problem with your fetch operation:', error));
  };

  const handleCategoryNameChange = (event) => { // New function to handle category name changes
    setCategoryName(event.target.value);
  };

  const handleFileChange = event => {
    setSelectedFile(event.target.files[0]);
  };

  const handleCategoryFormSubmit = (event) => { // New function to handle category form submission
    event.preventDefault();
    handleCategoryCreation(categoryName);
    setCategoryName(''); // Clear the category name input
  };

  const handleCategoryCreation = (categoryName) => {
    // Create category object
    const category = {
      groupId: groupId,
      name: categoryName
    };

    // Send POST request
    fetch('http://localhost:5000/category', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(category),
    })
    .then(response => response.json())
    .then(data => {
      // Handle response data
      if (data.id) {
        alert('Category created successfully!');
        window.location.reload();
        // Update categories
        setCategories([...categories, data]);
      } else {
        console.error('Server response:', data);
        alert('An error occurred while creating the category.');
      }
    })
    .catch((error) => {
      console.error('Error:', error);
      alert('An error occurred while creating the category.');
    });
  };

  return (
    <div>
      <form onSubmit={handleCategoryFormSubmit}>
        <input type="text" value={categoryName} onChange={handleCategoryNameChange} placeholder="Category Name" required />
        <input type="submit" value="Create Category" />
      </form>
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
        <input type="text" name="place" placeholder="place" onChange={handleInputChange} required />
        <textarea name="description" placeholder="Description" onChange={handleInputChange} required />
        <input type="file" onChange={handleFileChange} />
        <button type="submit">Create Expense</button>
      </form>
    </div>
  );
}

export default CreateExpense;
