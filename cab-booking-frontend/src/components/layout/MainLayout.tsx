import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import Footer from './Footer';

const MainLayout = () => {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />

      {/* This 'main' element will grow to fill available space */}
      <main className="flex-1">
        
        {/* The 'Outlet' is the magic part! */}
        {/* React Router will render your page components (like HomePage) right here. */}
        <Outlet />

      </main>

      <Footer />
    </div>
  );
};

export default MainLayout;