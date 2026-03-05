import { useLogout } from '@/features/auth/hooks/useLogout';
import { useAuthStore } from '@/store/useAuthStore';
import { Link } from 'react-router-dom';

export const Navbar = () => {
    const user = useAuthStore((state) => state.user);
    const { mutate: logoutUser, isPending } = useLogout();

    return (
        <nav className="flex justify-between items-center p-4 bg-white shadow-sm">
            <div className="font-bold text-xl">CabBooking</div>

            {user ? (
                <div className="flex items-center gap-4">
                    <span className="text-sm text-gray-600">
                        Welcome, {user.email}
                    </span>
                    <button
                        onClick={() => logoutUser()}
                        disabled={isPending}
                        className="bg-red-50 text-red-600 px-4 py-2 rounded hover:bg-red-100 disabled:opacity-50 transition-colors"
                    >
                        {isPending ? 'Logging out...' : 'Logout'}
                    </button>
                </div>
            ) : (
                // Links for unauthenticated users
                <div className="flex gap-4">
                        <Link to="/login" className="text-blue-600">Login</Link>
                    <Link to="/signup" className="bg-blue-600 text-white px-4 py-2 rounded">Sign Up</Link>
                </div>
            )}
        </nav>
    );
};

export default Navbar;