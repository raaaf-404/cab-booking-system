import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/features/booking/routes/HomePage';
import LoginPage from '@/features/auth/routes/LoginPage';

export const router = createBrowserRouter([

    {
    path: '/',
    element: <HomePage />,
    // We'll add a <MainLayout /> component here later
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
]);