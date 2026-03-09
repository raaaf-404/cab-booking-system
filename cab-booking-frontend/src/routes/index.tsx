import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/pages/HomePage.tsx';
import LoginPage from '@/features/auth/routes/LoginPage';
import SignupPage from '@/features/auth/routes/SignupPage';
import MainLayout from '@/components/common/MainLayout';
import ProfilePage from '@/features/profile/routes/ProfilePage';
import ProtectedRoute from './ProtectedRoute';
import NotFoundPage from "@/pages/NotFoundPage.tsx";

// Assuming you have these imported
// import PassengerDashboard from '...';
// import DriverDashboard from '...';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
        // Optional: Add a global error boundary here
        // errorElement: <GlobalErrorPage />,
        children: [
            // ==========================================
            // 1. PUBLIC ROUTES
            // ==========================================
            {
                index: true,
                element: <HomePage />,
            },
            {
                path: 'login', // Note: no leading slash needed in children
                element: <LoginPage />,
            },
            {
                path: 'signup',
                element: <SignupPage />,
            },
            {
                path: 'unauthorized',
                element: <div>Access Denied</div>,
            },

            // ==========================================
            // 2. SHARED PROTECTED ROUTES (Any logged-in user)
            // ==========================================
            {
                // Notice there is no 'path' here! This is a Layout Route.
                // It wraps all children.
                element: <ProtectedRoute />,
                children: [
                    {
                        path: 'profile',
                        element: <ProfilePage />,
                    },
                    // Add other shared protected routes here...
                ],
            },

            // ==========================================
            // 3. PASSENGER ONLY ROUTES
            // ==========================================
            {
                // Remember to use the exact string casing your backend uses!
                element: <ProtectedRoute allowedRoles={['passenger']} />,
                children: [
                    {
                        path: 'passenger/dashboard',
                        element: <div>Passenger Dashboard placeholder</div>, // <PassengerDashboard />
                    },
                    {
                        path: 'book-ride',
                        element: <div>Book Ride placeholder</div>, // <BookRidePage />
                    },
                ],
            },

            // ==========================================
            // 4. DRIVER ONLY ROUTES
            // ==========================================
            {
                element: <ProtectedRoute allowedRoles ={['driver']} />,
                children: [
                    {
                        path: 'driver/dashboard',
                        element: <div>Driver Dashboard placeholder</div>, // <DriverDashboard />
                    },
                    {
                        path: 'active-rides',
                        element: <div>Active Rides placeholder</div>, // <ActiveRidesPage />
                    },
                ],
            },

            // ==========================================
            // 4. NO Pages Found
            // ==========================================
            {
                path: '*',
                element: <NotFoundPage />
            }
        ],
    },
]);