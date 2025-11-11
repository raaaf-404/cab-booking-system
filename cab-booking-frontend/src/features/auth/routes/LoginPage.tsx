import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { useAuthStore } from '@/store/useAuthStore';
import { login as loginApi } from '@/api/authApi';
import LoginForm from '../components/LoginForm';
import { type LoginRequest, type AuthResponse } from '@/types/api';
import { AxiosError } from 'axios';

const LoginPage = () => {
  // Hook for programmatic navigation
  const navigate = useNavigate();
  
  // Get the 'login' action from our Zustand store
  const { login: loginToStore } = useAuthStore();

  // 1. Set up the mutation hook from TanStack Query
  const { mutate: loginUser, isPending, error } = useMutation<
    AuthResponse, // Type of the data on success
    AxiosError,    // Type of the error
    LoginRequest   // Type of the data passed to the mutate function
  >({
    mutationFn: loginApi, // The function to call (POST /api/auth/signin)

    // 2. Handle success
    onSuccess: (data) => {
      // Save user, token, and refreshToken to our Zustand store
      loginToStore({
        token: data.token,
        refreshToken: data.refreshToken,
        user: data.user,
      });

      // Redirect to the homepage
      navigate('/');
    },

    // 3. Handle error (onStatus 400, 500, etc.)
    // We don't need to do much here, as the 'error' variable
    // will be set, and we can display it.
  });

  // 4. Handle the form submission
  const handleSubmit = (data: LoginRequest) => {
    // This calls the mutationFn with the form data
    loginUser(data);
  };

  // 5. This is our "smart" page component
  return (
    <div className="mx-auto mt-10 max-w-md rounded-lg bg-white p-8 shadow-md">
      <h1 className="mb-6 text-center text-3xl font-bold text-gray-800">
        Sign In
      </h1>
      
      {/* 6. Render the "dumb" form component */}
      <LoginForm onSubmit={handleSubmit} isLoading={isPending} />

      {/* 7. Display server-side errors */}
      {error && (
        <p className="mt-4 text-center text-sm text-red-600">
          {/* A more robust solution would check error.response.data.message */}
          Invalid username or password.
        </p>
      )}
    </div>
  );
};

export default LoginPage;