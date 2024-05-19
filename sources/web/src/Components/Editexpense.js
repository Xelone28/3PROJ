import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../assets/css/Groupepage.css';

function EditExpense({ match }) {
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1]; // Get the auth token from cookies
  const navigate = useNavigate();
  const { expenseId } = useParams();
  const [expenseData, setExpenseData] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);
  const [categories, setCategories] = useState([]);
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [message, setMessage] = useState(null); // State variable for banner message
  const [bannerClass, setBannerClass] = useState(''); // State variable for banner class
  const [showBanner, setShowBanner] = useState(false); // State variable for showing banner

  useEffect(() => {
    if (showBanner) {
      const timer = setTimeout(() => {
        setShowBanner(false);
      }, 5000);
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
    // Fetch the current expense data when the component mounts
    const fetchExpense = async () => {
      const response = await fetch(`http://localhost:5000/expense/${expenseId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setExpenseData(data);
      } else {
        showErrorBanner('Failed to fetch expense data');
      }
    };

    fetchExpense();
  }, [expenseId, token]);

  useEffect(() => {
    // Fetch the categories for the group when the component mounts
    const fetchCategories = async () => {
      const response = await fetch(`http://localhost:5000/category/group/${expenseData.groupId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setCategories(data);
      } else {
        showErrorBanner('Failed to fetch categories');
      }
    };

    if (expenseData.groupId) {
      fetchCategories();
    }
  }, [expenseData.groupId, token]);

  useEffect(() => {
    // Fetch the users in the group when the component mounts
    const fetchUsers = async () => {
      const response = await fetch(`http://localhost:5000/useringroup/users/${expenseData.groupId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        showErrorBanner('Failed to fetch users');
      }
    };

    if (expenseData.groupId) {
      fetchUsers();
    }
  }, [expenseData.groupId, token]);

  useEffect(() => {
    if (expenseData) {
      setIsLoading(false);
    }
  }, [expenseData]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();
    Object.keys(expenseData).forEach(key => {
      formData.append(key, expenseData[key]);
    });
    if (selectedFile) {
      formData.append('image', selectedFile);
    }

    const response = await fetch(`http://localhost:5000/expense/${expenseId}`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      body: formData,
    });

    if (response.ok) {
      showSuccessBanner('Expense updated successfully!');
      setTimeout(() => {
        navigate(`/group/${expenseData.groupId}`);
      }, 5000);
    } else {
      console.error(`Error: ${response.status}`);
      showErrorBanner(`Failed to update expense. Status code: ${response.status}`);
    }
  };

  const handleChange = (event) => {
    setExpenseData({
      ...expenseData,
      [event.target.name]: event.target.value,
    });
  };

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleCheckboxChange = (event) => {
    const users = Array.isArray(expenseData.users) ? expenseData.users : [];
    const userId = Number(event.target.value);
    if (event.target.checked) {
      // If the checkbox is checked, add the user's ID to the array
      setExpenseData({
        ...expenseData,
        users: [...users, userId],
      });
    } else {
      // If the checkbox is unchecked, remove the user's ID from the array
      setExpenseData({
        ...expenseData,
        users: users.filter(user => user !== userId),
      });
    }
  };

  const handleInputChange = (event) => {
    setExpenseData({
      ...expenseData,
      [event.target.name]: event.target.value,
    });
  };

  return (
    <div>
      {message && (
        <div className={`banner ${bannerClass} ${showBanner ? 'show-banner' : ''}`}>
          {message}
        </div>
      )}
      <form onSubmit={handleSubmit}>
        {users.map(user => (
          <label key={user.userId}>
            <input type="checkbox" value={user.userId} checked={expenseData.users ? expenseData.users.includes(user.userId) : false} onChange={handleCheckboxChange} />
            {user.username}
          </label>
        ))}
        <select name="categoryId" value={expenseData.categoryId} onChange={handleInputChange} required>
          {categories.map(category => (
            <option key={category.id} value={category.id}>{category.name}</option>
          ))}
        </select>
        <input type="number" name="amount" value={expenseData.amount} onChange={handleChange} />
        <input type="date" name="date" value={expenseData.date ? new Date(expenseData.date * 1000).toISOString().substr(0, 10) : ''} onChange={handleChange} />
        <input name="place" value={expenseData.place} onChange={handleChange} />
        <input name="description" value={expenseData.description} onChange={handleChange} />
        <input type="file" onChange={handleFileChange} />
        <button type="submit">Update Expense</button>
      </form>
    </div>
  );
}

export default EditExpense;
