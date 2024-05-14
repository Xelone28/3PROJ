import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

function ExpensePage() {
    const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
    const navigate = useNavigate();
  const { expenseId } = useParams();
  const [expense, setExpense] = useState(null);
  const [category, setCategory] = useState(null); 
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      if (!expense || !expense.userIdInvolved) return;  // Skip if expense or userIdInvolved is not available

      const fetchedUsers = await Promise.all(expense.userIdInvolved.map(async (userId) => {
        const response = await fetch(`http://localhost:5000/api/users/${userId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (response.ok) {
          return await response.json();
        } else {
          console.error(`Failed to fetch user: ${response.status}`);
          return null;
        }
      }));

      setUsers(fetchedUsers.filter(Boolean));  // Remove null values
    };

    fetchUsers();
  }, [expense, token]);

  useEffect(() => {
    const fetchExpense = async () => {
      const response = await fetch(`http://localhost:5000/expense/${expenseId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setExpense(data);
        console.log(data)
      } else {
        console.error(`Failed to fetch expense: ${response.status}`);
      }
    };

    fetchExpense();
  }, [expenseId, token]);


  useEffect(() => {
    const fetchCategory = async () => {
      if (!expense || !expense.categoryId) return;  // Skip if expense or categoryId is not available

      const response = await fetch(`http://localhost:5000/category/${expense.categoryId}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setCategory(data);
      } else {
        console.error(`Failed to fetch category: ${response.status}`);
      }
    };

    fetchCategory();
  }, [expense, token]);  // Run when expense or token changes

  if (!expense) {
    return <div>Loading...</div>;
  }

  const deleteExpense = async () => {
    const response = await fetch(`http://localhost:5000/expense/${expenseId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });

    if (response.ok) {
      navigate(`/group/${expense.groupId}`);
    } else {
      console.error(`Failed to delete expense: ${response.status}`);
    }
  };

  const back = async () => {
    navigate(`/group/${expense.groupId}`);
  }

  return (
    <div>
    <button onClick={back}>Back</button>
      <p>Amount: {expense.amount}</p>
      <p>Place: {expense.place}</p>
      <p>Description: {expense.description}</p>
      <p>Date: {new Date(expense.date * 1000).toLocaleDateString()}</p>
      <p>Category: {category ? category.name : 'Loading...'}</p> 
      <p>Users Involved: {users.map(user => user.username).join(', ')}</p>     
      <button onClick={deleteExpense}>Delete</button>

    </div>
  );
}

export default ExpensePage;