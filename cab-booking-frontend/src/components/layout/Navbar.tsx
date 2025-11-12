import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { useMutation } from '@tanstack/react-query';
import { logout as logoutApi } from '@/api/authApi';

const Navbar = () => {
  // 1. Get state and actions from our store
  const { user, logout: logoutFromStore } = useAuthStore();
  const navigate = useNavigate();

  // 2. Create a mutation to call the logout API
  const { mutate: logoutUser, isPending } = useMutation({
    mutationFn: logoutApi, // Points to our API function

    // 3. Use 'onSettled' for logout
    // This runs after the mutation is successful *or* fails.
    // We want to log the user out locally no matter what.
    onSettled: () => {
      // Clear the user data from Zustand and localStorage
      logoutFromStore();
      
      // Redirect to the login page
      navigate('/login');
    },
  });

  // 4. Update the handler to call the mutation
  const handleLogout = () => {
    logoutUser();
  };

  return (
    <nav className="flex items-center justify-between bg-gray-800 p-4 text-white">
      <Link to="/" className="text-xl font-bold">
        CabBooking
      </Link>

      <div className="flex gap-4">
        {/* Public Links */}
        <Link to="/" className="hover:text-gray-300">
          Home
        </Link>

        {user ? (
          // --- Show these links if user IS logged in ---
          <>
            <Link to="/profile" className="hover:text-gray-300">
              Profile ({user.username})
            </Link>
            <button
              onClick={handleLogout}
              className="rounded bg-red-600 px-3 py-1 text-sm font-medium hover:bg-red-700 disabled:opacity-50"
              disabled={isPending} // 5. Disable button while logging out
            >
              {isPending ? 'Logging out...' : 'Logout'}
            </button>
          </>
        ) : (
          // --- Show these links if user is NOT logged in ---
          <>
            <Link to="/login" className="hover:text-gray-300">
              Login
            </Link>
            <Link
              to="/signup"
              className="rounded bg-blue-600 px-3 py-1 text-sm font-medium hover:bg-blue-700"
            >
              Sign Up
            </Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;