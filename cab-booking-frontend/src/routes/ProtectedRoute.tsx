import { useAuthStore } from '@/store/useAuthStore';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { AppRole } from '@/types/api';
import { normalizeRole } from '@/utils/roleUtils';

interface ProtectedRouteProps {
    allowedRoles?: AppRole[];
}

export const ProtectedRoute = ({ allowedRoles }: ProtectedRouteProps) => {
  // 1. Get the user from our global store
  const { user, accessToken, isHydrated } = useAuthStore();
  const location = useLocation();

    // 1. Wait for Auth State to load from LocalStorage
    if (!isHydrated) {
        // Return a nice loading spinner here instead of a blank screen
        return (
            <div className="flex h-screen w-full items-center justify-center bg-gray-50">
                <div className="h-10 w-10 animate-spin rounded-full border-4 border-blue-600 border-t-transparent"></div>
            </div>
        );
    }

    // 2. Not Authenticated
    if (!accessToken || !user) {
        // Best Practice: Save the URL the user was trying to access!
        // After they log in, you can redirect them back to this exact page.
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // 3. Role-Based Access Control (RBAC)

    const userRole = normalizeRole(user.role);
    if (allowedRoles) {
        const hasRequiredRole =  allowedRoles.includes(userRole);
        if (!hasRequiredRole) {
            // Redirect to a safe page if they don't have access
            return <Navigate to="/unauthorized" replace />;
        }
    }

    // 4. Authorized: Render the child routes
    return <Outlet />;
};

export default ProtectedRoute;