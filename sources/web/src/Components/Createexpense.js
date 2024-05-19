import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';
import '../assets/css/App.css';

function CreateExpense() {
  const { groupId } = useParams();
  const user = JSON.parse(localStorage.getItem('user'));
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
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

  useEffect(() => {
    const fetchData = async () => {
      try {
        const userResponse = await fetch(`http://localhost:5000/useringroup/users/${groupId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        if (userResponse.ok) {
          const usersData = await userResponse.json();
          setUsers(usersData);
        } else {
          showErrorBanner('Failed to fetch users');
        }

        const categoryResponse = await fetch(`http://localhost:5000/category/group/${groupId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (categoryResponse.ok) {
          const categoriesData = await categoryResponse.json();
          setCategories(categoriesData);
        } else {
          showErrorBanner('Failed to fetch categories');
        }
      } catch (err) {
        console.error('Error fetching data:', err);
        showErrorBanner('Error fetching data');
      }
    };
    fetchData();
  }, [groupId, token]);

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

  const handleSubmit = async event => {
    event.preventDefault();

    const expenseData = {
      ...expense,
      date: Math.floor(new Date(expense.date).getTime() / 1000),
      UserIdsInvolved: expense.UserIdsInvolved.map(id => Number(id)),
      weights: expense.UserIdsInvolved.map(id => weights[id] || 0)
    };

    if (!Array.isArray(expenseData.UserIdsInvolved) || expenseData.UserIdsInvolved.length === 0) {
      showErrorBanner('Please select at least one user involved in the expense.');
      return;
    }
    if (!expenseData.place || expenseData.place.trim() === '') {
      showErrorBanner('Please provide a place for the expense.');
      return;
    }

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

    try {
      const response = await fetch('http://localhost:5000/expense', {
        method: 'POST',
        headers: { 
          'Authorization': `Bearer ${token}` 
        },
        body: formData,
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Response:', errorText);
        showErrorBanner('Network response was not ok');
        throw new Error('Network response was not ok');
      }

      const text = await response.text();
      if (text) {
        try {
          JSON.parse(text);
          showSuccessBanner('Expense created successfully!');
        } catch {
          showSuccessBanner('Expense created successfully!');
        }
      } else {
        showSuccessBanner('Expense created successfully!');
      }
      setTimeout(() => {
        navigate(`/group/${groupId}`);
      }, 2000);
    } catch (error) {
      console.error('There has been a problem with your fetch operation:', error);
      showErrorBanner('There has been a problem with your fetch operation');
    }
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

  const handleCategoryCreation = async categoryName => {
    const category = {
      groupId: groupId,
      name: categoryName
    };

    try {
      const response = await fetch('http://localhost:5000/category', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(category),
      });

      if (!response.ok) {
        const errorData = await response.json();
        console.error('Server response:', errorData);
        showErrorBanner('An error occurred while creating the category.');
        return;
      }

      const data = await response.json();
      if (data.id) {
        showSuccessBanner('Category created successfully!');
        window.location.reload();
        setCategories([...categories, data]);
      } else {
        console.error('Server response:', data);
        showErrorBanner('An error occurred while creating the category.');
      }
    } catch (error) {
      console.error('Error:', error);
      showErrorBanner('An error occurred while creating the category.');
    }
  };

  return (
    <div className="form-container">
      <h2>Create Expense</h2>
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <form onSubmit={handleCategoryFormSubmit}>
        <input style={{ maxWidth: '40vh' }}
          type="text"
          value={categoryName}
          onChange={handleCategoryNameChange}
          placeholder="Category Name"
          required
        />
        <input className="main-button" type="submit" value="Create Category" />
      </form>
      <form onSubmit={handleSubmit}>
        {users.map(user => (
          <div key={user.userId}>
            <label>
              <input
                type="checkbox"
                value={user.userId}
                onChange={handleCheckboxChange}
              />
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
        <input
          type="number"
          name="amount"
          placeholder="Amount"
          onChange={handleInputChange}
          required
        />
        <input
          type="date"
          name="date"
          onChange={handleInputChange}
          required
        />
        <input style={{ maxWidth: '40vh' }}
          type="text"
          name="place"
          placeholder="Place"
          onChange={handleInputChange}
          required
        />
        <textarea
          name="description"
          placeholder="Description"
          onChange={handleInputChange}
          required
        />
        <input type="file" onChange={handleFileChange} />
        <button className="main-button" type="submit">Create Expense</button>
      </form>
    </div>
  );
}

export default CreateExpense;
