import { useState, useEffect } from 'react';
import axios from 'axios';



export default function Dashboard() {
    const [payments, setPayments] = useState([]);
    const [error, setError] = useState('');

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
        const fetchTransactions = async () => {
            try {
                const apiKey = localStorage.getItem('merchant_api_key');
                const apiSecret = localStorage.getItem('merchant_api_secret');

                if (!apiKey || !apiSecret) {
                    window.location.href = '/login';
                    return;
                }

                const res = await axios.get('http://localhost:8000/api/v1/payments', {
                    headers: {
                        'X-Api-Key': apiKey,
                        'X-Api-Secret': apiSecret
                    }
                });
                setPayments(res.data);
            } catch (err) {
                console.error("Fetch error:", err);
                setError('Failed to fetch transactions');
            }
        };

        fetchTransactions();
    }, []);

    return (
        <div className="container" style={{ padding: '2rem' }}>
            <h1>Merchant Dashboard</h1>

            <div className="card" style={{ marginBottom: '2rem' }}>
                <h3>Your Credentials</h3>
                <p><strong>Merchant Email:</strong> <code style={{ background: '#f4f4f4', padding: '2px 5px' }}>{localStorage.getItem('merchant_email')}</code></p>
                <p><strong>API Key:</strong> <code style={{ background: '#f4f4f4', padding: '2px 5px' }}>{localStorage.getItem('merchant_api_key')}</code></p>
                <p><strong>API Secret:</strong> <code style={{ background: '#f4f4f4', padding: '2px 5px' }}>{localStorage.getItem('merchant_api_secret')}</code></p>
            </div>

            <div className="card">
                <h2>Transaction History</h2>
                {error && <p style={{ color: 'red' }}>{error}</p>}

                <table data-test-id="transactions-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ textAlign: 'left', borderBottom: '2px solid #ddd' }}>
                            <th style={{ padding: '10px' }}>ID</th>
                            <th style={{ padding: '10px' }}>Order ID</th>
                            <th style={{ padding: '10px' }}>Amount</th>
                            <th style={{ padding: '10px' }}>Method</th>
                            <th style={{ padding: '10px' }}>Status</th>
                            <th style={{ padding: '10px' }}>Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        {payments.map(p => (
                            <tr key={p.id} style={{ borderBottom: '1px solid #eee' }}>
                                <td style={{ padding: '10px' }}>{p.id}</td>
                                <td style={{ padding: '10px' }}>{p.orderId}</td>
                                <td style={{ padding: '10px' }}>₹{(p.amount / 100).toFixed(2)}</td>
                                <td style={{ padding: '10px' }}>{p.method}</td>
                                <td style={{ padding: '10px' }}>
                                    <span style={{
                                        color: p.status === 'success' ? 'green' : p.status === 'failed' ? 'red' : 'orange',
                                        textTransform: 'capitalize'
                                    }}>
                                        {p.status}
                                    </span>
                                </td>
                                <td>{formatDate(p.createdAt || p.created_at)}</td>
                            </tr>
                        ))}
                        {payments.length === 0 && !error && (
                            <tr><td colSpan="6" style={{ textAlign: 'center', padding: '20px' }}>No transactions found</td></tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}