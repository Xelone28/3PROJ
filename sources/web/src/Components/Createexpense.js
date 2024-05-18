import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';

function CreateExpense() {
  const { groupId } = useParams();
  const user = JSON.parse(localStorage.getItem('user'));
  const token = document
  .cookie.split('; ').find(row => row.startsWith('token='))
  .split('=')[1];
  const navigate = useNavigate();
  const [categoryName, setCategoryName] = useState('');
  const [users, setUsers] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [categories, setCategories] = useState([]);
  const [weights, setWeights] = useState({});
  const [expense, setExpense] = useState({
    userId: user.id.toString(),
    groupId,
    UserIdsInvolved: [],
    categoryId: '',
    amount: '',
    date: '',
    place: '',
    description: ''
  });

  useEffect(() => {
    fetch(`http://localhost:5000/useringroup/users/${groupId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.ok ? response.json() : Promise.reject('Failed to fetch users'))
      .then(data => setUsers(data))
      .catch(err => console.error('Error fetching users:', err));
  
    fetch(`http://localhost:5000/category/group/${groupId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(response => response.ok ? response.json() : Promise.reject('Failed to fetch categories'))
      .then(data => setCategories(data))
      .catch(err => console.error('Error fetching categories:', err));
  }, [groupId, token]);
  

  const handleInputChange = event => {
    const { name, value } = event.target;
    setExpense(prevExpense => ({ ...prevExpense, [name]: value }));
  };

  const handleCheckboxChange = event => {
    const userId = parseInt(event.target.value, 10);
    const updatedUserIds = event.target.checked
      ? [...expense.UserIdsInvolved, userId]
      : expense.UserIdsInvolved.filter(id => id !== userId);

    setExpense(prevExpense => ({
      ...prevExpense,
      UserIdsInvolved: updatedUserIds
    }));

    setWeights(prevWeights => ({
      ...prevWeights,
      [userId]: event.target.checked ? 1 : 0
    }));
  };

  const handleWeightChange = (userId, weight) => {
    setWeights(prevWeights => ({
      ...prevWeights,
      [userId]: weight
    }));
  };

  const handleSubmit = event => {
    event.preventDefault();
  
    const expenseData = { 
      ...expense, 
      date: Math.floor(new Date(expense.date).getTime() / 1000),
      place: expense.place,
      UserIdsInvolved: expense.UserIdsInvolved.map(id => Number(id)),
      weights: expense.UserIdsInvolved.map(id => weights[id] || 0)
    };
  
    if (!Array.isArray(expenseData.UserIdsInvolved) || expenseData.UserIdsInvolved.length === 0) {
      alert('Please select at least one user involved in the expense.');
      return;
    }
    if (!expenseData.place || expenseData.place.trim() === '') {
      alert('Please provide a place for the expense.');
      return;
    }
  
    console.log('Expense data:', expenseData);
  
    const formData = new FormData();
    formData.append('userId', expenseData.userId);
    expenseData.UserIdsInvolved.forEach((userId, index) => {
      formData.append(`UserIdsInvolved[${index}]`, userId);
      formData.append(`weights[${index}]`, expenseData.weights[index]);
    });
    formData.append('groupId', expenseData.groupId);
    formData.append('categoryId', expenseData.categoryId);
    formData.append('amount', expenseData.amount);
    formData.append('date', expenseData.date);
    formData.append('place', expenseData.place);
    formData.append('description', expenseData.description);
    if (selectedFile) {
      formData.append('image', selectedFile);
    }
  
    for (let pair of formData.entries()) {
      console.log(pair[0] + ', ' + pair[1]); 
    }
  
    fetch('http://localhost:5000/expense', {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${token}` 
      },
      body: formData,
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          console.error('Response:', text);
          throw new Error('Network response was not ok');
        });
      }
      return response.text(); // Instead of assuming JSON, we get the text response
    })
    .then(text => {
      // Handle the case where the response might be empty or not JSON
      if (text) {
        try {
          const data = JSON.parse(text);
          alert('Expense created successfully!');
          navigate(`/group/${groupId}`);
        } catch (err) {
          console.error('Failed to parse JSON:', err);
          alert('Expense created successfully!');
          navigate(`/group/${groupId}`);
        }
      } else {
        alert('Expense created successfully!');
        navigate(`/group/${groupId}`);
      }
    })
    .catch(error => console.error('There has been a problem with your fetch operation:', error));
  };
  
  

  const handleCategoryNameChange = event => {
    setCategoryName(event.target.value);
  };

  const handleFileChange = event => {
    setSelectedFile(event.target.files[0]);
  };

  const handleCategoryFormSubmit = event => {
    event.preventDefault();
    handleCategoryCreation(categoryName);
    setCategoryName('');
  };

  const handleCategoryCreation = categoryName => {
    const category = {
      groupId: groupId,
      name: categoryName
    };

    fetch('http://localhost:5000/category', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(category),
    })
    .then(response => {
      if (!response.ok) {
        return response.json().then(data => {
          console.error('Server response:', data);
          alert('An error occurred while creating the category.');
        }).catch(() => {
          throw new Error('Received an empty response from the server');
        });
      }
      return response.json();
    })
    .then(data => {
      if (data.id) {
        alert('Category created successfully!');
        window.location.reload();
        setCategories([...categories, data]);
      } else {
        console.error('Server response:', data);
        alert('An error occurred while creating the category.');
      }
    })
    .catch(error => {
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
          <div key={user.userId}>
            <label>
              <input type="checkbox" value={user.userId} onChange={handleCheckboxChange} />
              {user.username}
            </label>
            {expense.UserIdsInvolved.includes(user.userId) && (
              <input
                type="number"
                placeholder="Weight"
                value={weights[user.userId] || 0}
                onChange={e => handleWeightChange(user.userId, parseFloat(e.target.value))}
                required
              />
            )}
          </div>
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
        <input type="file" onChange={handleFileChange} />
        <button type="submit">Create Expense</button>
      </form>
    </div>
  );
}

export default CreateExpense;
