import { useEffect, useState } from 'react';
import axios from 'axios';

export default function Transactions() {
  const [payments, setPayments] = useState([]);
  const apiKey = localStorage.getItem('merchant_api_key');
  const apiSecret = localStorage.getItem('merchant_api_secret');

      const formatDate = (dateInput) => {
    if (!dateInput) return 'N/A';
    try {
        let date;
        if (Array.isArray(dateInput)) {
        date = new Date(
            dateInput[0],
            dateInput[1] - 1,
            dateInput[2],
            dateInput[3] || 0,
            dateInput[4] || 0,
            dateInput[5] || 0
        );
        } else {
        date = new Date(dateInput);
        }
        
        if (isNaN(date.getTime())) return 'Invalid Date';

        return new Intl.DateTimeFormat('en-US', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
        }).format(date);
    } catch (e) {
        return 'Error';
    }
    };

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const res = await axios.get('http://localhost:8000/api/v1/payments', {
          headers: { 'X-Api-Key': apiKey, 'X-Api-Secret': apiSecret }
        });
        setPayments(res.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchPayments();
  }, []);

  return (
    <div className="card">
      <h2>Transaction History</h2>
      <table data-test-id="transactions-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Order ID</th>
            <th>Amount</th>
            <th>Method</th>
            <th>Status</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>
          {payments.map(p => (
            <tr key={p.id} data-test-id="transaction-row" data-payment-id={p.id}>
              <td data-test-id="payment-id">{p.id}</td>
              <td data-test-id="order-id">{p.orderId}</td>
              <td data-test-id="amount">₹{(p.amount / 100).toFixed(2)}</td>
              <td data-test-id="method">{p.method}</td>
              <td data-test-id="status">
                <span style={{
                  color: p.status === 'success' ? 'green' : p.status === 'failed' ? 'red' : 'orange'
                }}>
                  {p.status}
                </span>
              </td>
              <td data-test-id="created-at">{formatDate(p.createdAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}