import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

function Reimburse() {
  const { DebtAdjustmentId, adjustmentAmount, groupId } = useParams();
  const [userId, setUserId] = useState(null);
  const [userInCredit, setUserInCredit] = useState(null);
  const [image, setImage] = useState(null);
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];

  useEffect(() => {
    const storedUser = JSON.parse(localStorage.getItem('user'));
    if (storedUser) {
      setUserId(storedUser.id);
    }
  }, []);

  useEffect(() => {
    const fetchDebtDetails = async () => {
      try {
        const response = await fetch(`http://localhost:5000/DebtAdjustment/${DebtAdjustmentId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const debtData = await response.json();
          fetchUserInCredit(debtData.userInCreditId);
        } else {
          alert('Failed to fetch debt details');
        }
      } catch (error) {
        console.error('Error fetching debt details:', error);
        alert('Failed to fetch debt details');
      }
    };

    const fetchUserInCredit = async (userInCreditId) => {
      try {
        const response = await fetch(`http://localhost:5000/api/users/${userInCreditId}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const userData = await response.json();
          setUserInCredit(userData);
        } else {
          alert('Failed to fetch user details');
        }
      } catch (error) {
        console.error('Error fetching user details:', error);
        alert('Failed to fetch user details');
      }
    };

    fetchDebtDetails();
  }, [DebtAdjustmentId, token]);

  const handleImageChange = (e) => {
    setImage(e.target.files[0]);
  };

  const handleReimburse = async (e) => {
    e.preventDefault();

    if (!userId || !groupId || !adjustmentAmount || !DebtAdjustmentId || !image) {
      alert('All fields are required.');
      return;
    }

    const formData = new FormData();
    formData.append('UserId', userId);
    formData.append('GroupId', groupId);
    formData.append('Amount', adjustmentAmount);
    formData.append('DebtAdjustmentId', DebtAdjustmentId);
    formData.append('PaymentDate', new Date().toISOString());
    formData.append('type', 1);  // Assuming 1 is the type for reimbursement
    formData.append('Image', image);

    // Log the FormData contents
    for (const [key, value] of formData.entries()) {
      console.log(`${key}: ${value instanceof File ? value.name : value}`);
    }

    try {
      const response = await fetch(`http://localhost:5000/api/payment`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        body: formData,
      });

      if (response.ok) {
        alert('Payment successfully submitted!');
      } else {
        const errorText = await response.text();
        console.error('Error response:', errorText);
        alert(`Failed to submit payment: ${errorText}`);
      }
    } catch (error) {
      console.error('Error submitting payment:', error);
      alert('Failed to submit payment');
    }
  };

  return (
    <div>
      <h2>Reimburse Debt</h2>
      {userInCredit && (
        <div>
          <p>Reimbursing: {userInCredit.username}</p>
        </div>
      )}
      <form onSubmit={handleReimburse}>
        <div>
          <label>Amount to Reimburse:</label>
          <p>{adjustmentAmount}</p>        
        </div>
        <div>
          <label>Image:</label>
          <input type="file" onChange={handleImageChange} required />
        </div>
        <button type="submit">Reimburse</button>
      </form>
    </div>
  );
}

export default Reimburse;
