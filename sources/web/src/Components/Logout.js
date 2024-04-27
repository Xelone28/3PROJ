import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function Logout({ onLogout }) {
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.removeItem('user');
    document.cookie = "token=; Max-Age=0";
    navigate('/login');
    if (onLogout) {
      onLogout();
    }
  }, [navigate, onLogout]);

  return null;
}

export default Logout;