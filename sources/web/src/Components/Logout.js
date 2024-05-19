import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../assets/css/App.css';

function Logout({ onLogout }) {
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.removeItem('user');
    document.cookie = "token=; Max-Age=0";
    navigate('/login');
    window.location.reload()
    if (onLogout) {
      onLogout();
    }
  }, [navigate, onLogout]);

  return null;
}

export default Logout;