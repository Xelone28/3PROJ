import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

function ExpensePage() {
  const token = document.cookie.split('; ').find(row => row.startsWith('token=')).split('=')[1];
  const navigate = useNavigate();
  const { expenseId } = useParams();
  const [expense, setExpense] = useState(null);
  const [data, setData] = useState(null);

  const fetchDebts = async () => {
    const response = await fetch(`http://localhost:5000/debt/expense/${expenseId}`, {
      headers: {
        'Authorization': 'Bearer ' + token,
      },
    });

    if (!response.ok) {
      console.error(`Failed to fetch debts: ${response.status}`);
      return;
    }

    const debts = await response.json();

    let amountsDue = {};
    let amountsToPay = {};

    debts.forEach(debt => {
      const creditor = debt.userInDebt.username; // Swapped
      const debtor = debt.userInCredit.username; // Swapped
    
      // If the debt is not paid and not canceled, add it to the amounts
      if (!debt.isPaid && !debt.isCanceled) {
        amountsDue[creditor] = (amountsDue[creditor] || 0) + debt.amount;
        amountsToPay[debtor] = (amountsToPay[debtor] || 0) + debt.amount;
      }
    });

    const labels = Array.from(new Set([...Object.keys(amountsDue), ...Object.keys(amountsToPay)]));
    const dueData = labels.map(label => amountsDue[label] || 0);
    const payData = labels.map(label => amountsToPay[label] || 0);

    const data = {
      labels,
      datasets: [
        {
          label: 'Somme à payer',
          data: dueData.map(value => -Math.abs(value)), // Make all values negative
          backgroundColor: 'rgba(255, 0, 0, 0.2)', // Red
          borderColor: 'rgba(255, 0, 0, 1)', // Red
          borderWidth: 1,
        },
        {
          label: 'Somme due',
          data: payData,
          backgroundColor: 'rgba(0, 0, 255, 0.2)', // Blue
          borderColor: 'rgba(0, 0, 255, 1)', // Blue
          borderWidth: 1,
        },
      ],
    };

    return data;
  };

  useEffect(() => {
    const fetchData = async () => {
      const result = await fetchDebts();
      setData(result);
    };

    fetchData();
  }, []);

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
      } else {
        console.error(`Failed to fetch expense: ${response.status}`);
      }
    };

    fetchExpense();
  }, [expenseId, token]);

  if (!expense) {
    return <div>Loading...</div>;
  }

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
      <p>Category: {expense.category ? expense.category.name : 'Loading...'}</p> 
      <p>Users Involved: {expense.usersInvolved.map(user => user.username).join(', ')}</p>
      <p>
  {expense.image && 
    <a href={expense.image} target="_blank" rel="noopener noreferrer">
      <img src={expense.image} alt="User" style={{width: "400px"}} />
    </a>
  }
</p>

      {data && (
  <div style={{ 
    display: 'flex', 
    justifyContent: 'center', 
    alignItems: 'center', 
     // This makes the div take up the full height of the viewport
  }}>
    <div style={{ width: '400px', height: '400px' }}>
      <h2>Debts</h2>
      <Bar
        data={data}
        options={{
          indexAxis: 'y', // This makes the chart horizontal
          scales: {
            x: {
              stacked: true, // This makes the bars appear to extend from a central line
            },
          },
          maintainAspectRatio: false, // This allows the chart to resize based on the div size
          plugins: {
            afterDatasetsDraw: function(chart, options) {
              var ctx = chart.ctx;
              var xAxis = chart.scales['x'];
              var yAxis = chart.scales['y'];
              ctx.save();
              ctx.strokeStyle = '#000000'; // Color of the line
              ctx.lineWidth = 2; // Thickness of the line
              ctx.beginPath();
              ctx.moveTo(xAxis.getPixelForValue(0), yAxis.top);
              ctx.lineTo(xAxis.getPixelForValue(0), yAxis.bottom);
              ctx.stroke();
              ctx.restore();
            }
          }
        }}
      />
    </div>
  </div>
)}
    </div>
  );
}

export default ExpensePage;
