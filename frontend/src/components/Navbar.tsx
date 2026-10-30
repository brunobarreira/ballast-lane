import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar() {
  const { isAuthenticated, username, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="bg-red-600 text-white shadow-md">
      <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="text-xl font-bold flex items-center gap-2">
          <span className="text-2xl">⚡</span> PokéDex
        </Link>
        <div className="flex items-center gap-4">
          <Link to="/" className="hover:text-red-200 transition-colors">Browse</Link>
          {isAuthenticated && (
            <Link to="/local" className="hover:text-red-200 transition-colors">My Pokémon</Link>
          )}
          {isAuthenticated ? (
            <div className="flex items-center gap-3">
              <span className="text-sm text-red-200">👤 {username}</span>
              <button
                onClick={handleLogout}
                className="bg-red-700 hover:bg-red-800 px-3 py-1 rounded-lg text-sm transition-colors"
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="flex gap-2">
              <Link
                to="/login"
                className="bg-red-700 hover:bg-red-800 px-3 py-1 rounded-lg text-sm transition-colors"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="bg-white text-red-600 hover:bg-red-100 px-3 py-1 rounded-lg text-sm font-semibold transition-colors"
              >
                Register
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
