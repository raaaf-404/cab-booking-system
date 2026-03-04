import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { AxiosError } from 'axios';

// Stores & APIs
import { useAuthStore } from '@/store/useAuthStore';
import { registerDriver, registerPassenger } from '@/api/authApi';
// Types
import { PassengerFormData, DriverFormData } from '../validation/authSchema';
import { PassengerSignupRequest, DriverSignupRequest } from '@/types/auth';
import { Role } from '../components/SignupForm';
// Components
import { SignupForm } from '../components/SignupForm';

// 1. Mutation Args now strictly demand the API Contract types
interface SignupArgs {
    data: PassengerSignupRequest | DriverSignupRequest;
    role: Role;
}

export const SignupPage = () => {
    const navigate = useNavigate();
    const setCredentials = useAuthStore((state) => state.setCredentials);

    // 1. Set up the Mutation
    const { mutate: signupUser, isPending, error } = useMutation({
        // The mutation function decides which endpoint to hit based on the role
        mutationFn: (args: SignupArgs) => {
            if (args.role === 'passenger') {
                return registerPassenger(args.data as PassengerSignupRequest);
            }
            return registerDriver(args.data as DriverSignupRequest);
        },

        // 2. On Success: Auto-login and Redirect!
        onSuccess: (response) => {
            // Save tokens & user data directly to global state
            setCredentials(response);

            // Determine the dashboard based on the role returned from the server
            const userRole = response.user?.role?.toLowerCase();
            const dashboardPath = userRole === 'driver' ? '/driver/dashboard' : '/passenger/dashboard';
            // Redirect
            navigate(dashboardPath, { replace: true });
        },
    });

    // 3. Handle the form submission from the dumb component
    const handleSignupSubmit = (data: PassengerFormData | DriverFormData, role: Role) => {
            signupUser({ data, role });
    };

    // 4. Extract standard Axios error
    const serverError = error instanceof AxiosError
        ? error.response?.data?.message || "Registration failed. Please try again or use a different email."
        : null;

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 sm:px-6 lg:px-8 py-12">
            <div className="w-full max-w-md">
                {/* We pass our handler, loading state, and error to the presentational component */}
                <SignupForm
                    onSubmit={handleSignupSubmit}
                    isLoading={isPending}
                    serverError={serverError}
                />
            </div>
        </div>
    );
};

export default SignupPage;