import { useAuthStore } from '@/store/useAuthStore';
import { Navigate, useLocation } from 'react-router-dom';

type ProtectedRouteProps = {
  children: React.ReactNode;
};

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  // 1. Get the user from our global store
  const { user } = useAuthStore();
  const location = useLocation();

  if (!user) {
    // 2. If no user, redirect to the login page
    // We pass 'state={{ from: location }}' so we can redirect
    // them back to the page they were trying to visit after they log in.
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 3. If there is a user, render the child component (the page)
  return <>{children}</>;
};

export default ProtectedRoute;