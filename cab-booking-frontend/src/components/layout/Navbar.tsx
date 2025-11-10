import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav className="flex items-center justify-between bg-gray-800 p-4 text-white">
      <Link to="/" className="text-xl font-bold">
        CabBooking
      </Link>
      <div className="flex gap-4">
        <Link to="/" className="hover:text-gray-300">
          Home
        </Link>
        <Link to="/login" className="hover:text-gray-300">
          Login
        </Link>
        <Link to="/profile" className="hover:text-gray-300">
          Profile
        </Link>
      </div>
    </nav>
  );
};

export default Navbar;