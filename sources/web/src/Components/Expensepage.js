import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Bar } from 'react-chartjs-2';
import '../assets/css/App.css';
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

  const fetchDebts = useCallback(async () => {
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
      const creditor = debt.userInDebt.username;
      const debtor = debt.userInCredit.username;

      if (!debt.isPaid && !debt.isCanceled) {
        amountsDue[creditor] = (amountsDue[creditor] || 0) + debt.amount;
        amountsToPay[debtor] = (amountsToPay[debtor] || 0) + debt.amount;
      }
    });

    const labels = Array.from(new Set([...Object.keys(amountsDue), ...Object.keys(amountsToPay)]));
    const dueData = labels.map(label => amountsDue[label] || 0);
    const payData = labels.map(label => amountsToPay[label] || 0);

    return {
      labels,
      datasets: [
        {
          label: 'Somme à payer',
          data: dueData.map(value => -Math.abs(value)),
          backgroundColor: 'rgba(255, 99, 132, 0.6)',
          borderColor: 'rgba(255, 99, 132, 1)',
          borderWidth: 1,
        },
        {
          label: 'Somme due',
          data: payData,
          backgroundColor: 'rgba(75, 192, 192, 0.6)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1,
        },
      ],
    };
  }, [expenseId, token]);

  useEffect(() => {
    const fetchData = async () => {
      const result = await fetchDebts();
      setData(result);
    };

    fetchData();
  }, [fetchDebts]);

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

  const back = () => {
    navigate(`/group/${expense.groupId}`);
  };

  return (
    <div>
      <button className="main-button" onClick={back}>Back</button>
      <p>Amount: {expense.amount}</p>
      <p>Place: {expense.place}</p>
      <p>Description: {expense.description}</p>
      <p>Date: {new Date(expense.date * 1000).toLocaleDateString()}</p>
      <p>Category: {expense.category ? expense.category.name : 'Loading...'}</p>
      <p>Users Involved: {expense.usersInvolved.map(user => user.username).join(', ')}</p>
      {expense.image && (
        <p>
          <a href={expense.image} target="_blank" rel="noopener noreferrer">
            <img src={expense.image} alt="User" style={{ width: '400px' }} />
          </a>
        </p>
      )}
      {data && (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <div style={{ width: '400px', height: '400px' }}>
            <h2>Debts</h2>
            <Bar
              data={data}
              options={{
                indexAxis: 'y',
                scales: {
                  x: { stacked: true },
                },
                maintainAspectRatio: false,
                plugins: {
                  afterDatasetsDraw: function(chart) {
                    const ctx = chart.ctx;
                    const xAxis = chart.scales['x'];
                    const yAxis = chart.scales['y'];
                    ctx.save();
                    ctx.strokeStyle = '#000000';
                    ctx.lineWidth = 2;
                    ctx.beginPath();
                    ctx.moveTo(xAxis.getPixelForValue(0), yAxis.top);
                    ctx.lineTo(xAxis.getPixelForValue(0), yAxis.bottom);
                    ctx.stroke();
                    ctx.restore();
                  },
                },
              }}
            />
          </div>
        </div>
      )}
    </div>
  );
}

export default ExpensePage;
