import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Transactions from './pages/Transactions';

function App() {
  const isAuthenticated = !!localStorage.getItem('merchant_api_key');

  return (
    <BrowserRouter>
      {isAuthenticated && (
        <nav>
          <div className="container" style={{padding: '0', display: 'flex', alignItems: 'center'}}>
            <Link to="/dashboard" style={{marginRight: '20px'}}>Dashboard</Link>
            <Link to="/dashboard/transactions">Transactions</Link>
            <button onClick={() => { localStorage.clear(); window.location.href='/login'; }} style={{marginLeft: 'auto'}}>Logout</button>
          </div>
        </nav>
      )}
      <div className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={isAuthenticated ? <Dashboard /> : <Navigate to="/login" />} />
          <Route path="/dashboard/transactions" element={isAuthenticated ? <Transactions /> : <Navigate to="/login" />} />
          <Route path="/" element={<Navigate to="/dashboard" />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;