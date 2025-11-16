import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/features/booking/routes/HomePage';
import LoginPage from '@/features/auth/routes/LoginPage';
import SignupPage from '@/features/auth/routes/SignupPage';
import MainLayout from '@/components/layout/MainLayout';

import ProfilePage from '@/features/profile/routes/ProfilePage';

import ProtectedRoute from './ProtectedRoute';

export const router = createBrowserRouter([
  {
    // This is now your main "layout" route
    path: '/',
    element: <MainLayout />, // <-- 2. Set it as the element
    children: [
      // 3. These routes will render *inside* MainLayout's <Outlet>
      {
        index: true, // This means it's the default child route
        element: <HomePage />,
      },
      {
        path: '/login',
        element: <LoginPage />,
      },
      {
        path: '/signup',
        element: <SignupPage />,
      },
      {
        path: '/profile',
        element: (
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        ),
      },
    ],
  },
]);
