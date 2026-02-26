import { useLocation, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { AxiosError } from 'axios';

// API & Store
import { login } from '@/api/authApi';
import { useAuthStore } from '@/store/useAuthStore';
import { type LoginRequest } from '@/types/api';

// Components
import LoginForm from '../components/LoginForm';

// Adjust this type based on exactly what your backend AuthResponse looks like
interface ApiErrorResponse {
    message: string;
}

export const LoginPage = () => {
    // 1. Hooks for Global State and Routing
    const setCredentials = useAuthStore((state) => state.setCredentials);
    const navigate = useNavigate();
    const location = useLocation();

    // 2. TanStack Query Mutation (The Industry Standard for API calls)
    const { mutate: loginUser, isPending, error } = useMutation({
        mutationFn: (data: LoginRequest) => login(data),

        // 3. onSuccess: Handles the logic AFTER a successful API call
        onSuccess: (response) => {
            // A. Save tokens & user data to global state
            setCredentials(response);

            // B. --- THE REDIRECT LOGIC ---

            // Did the ProtectedRoute send them here from a specific URL?
            const intendedDestination = location.state?.from?.pathname;

            // Determine role-based fallback dashboard
            // Safety check: ensure response.data.user.role exists!
            const userRole = response.user?.roles?.toLowerCase();
            const defaultDashboard = userRole === 'driver' ? '/driver/dashboard' : '/passenger/dashboard';

            // Navigate using Intended Destination, or fallback to Default Dashboard
            const navigateTo = intendedDestination || defaultDashboard;

            // Navigate with 'replace: true' to prevent breaking the browser's Back button
            navigate(navigateTo, { replace: true });
        },
    });

    // 4. Extract dynamic error message from Axios error
    const serverError = error instanceof AxiosError
        ? (error.response?.data as ApiErrorResponse)?.message || "Invalid email or password. Please try again."
        : null;

    // 5. Presentational Wrapper
    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 sm:px-6 lg:px-8">
            <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-xl shadow-lg border border-gray-100">
                <div>
                    <h2 className="text-center text-3xl font-extrabold text-gray-900">
                        Welcome Back
                    </h2>
                    <p className="mt-2 text-center text-sm text-gray-600">
                        Sign in to your account to continue
                    </p>
                </div>

                {/* 6. Render the "Dumb" form component */}
                {/* We pass the mutate function directly, along with Tanstack's isPending state */}
                <LoginForm
                    onSubmit={loginUser}
                    isLoading={isPending}
                    serverError={serverError}
                />
            </div>
        </div>
    );
};

export default LoginPage;