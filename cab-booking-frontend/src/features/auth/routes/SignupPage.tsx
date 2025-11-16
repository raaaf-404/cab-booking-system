import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { signup as signupApi } from '@/api/authApi';
import SignupForm from '../components/SignupForm';
import { type SignupRequest, type MessageResponse } from '@/types/api';
import { AxiosError } from 'axios';

const SignupPage = () => {
  const navigate = useNavigate();
  // We'll use state to show a success message
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // 1. Set up the mutation hook
  const { mutate: signupUser, isPending, error } = useMutation<
    MessageResponse, // On success, we get a MessageResponse
    AxiosError,      // On error, we get an AxiosError
    SignupRequest    // We send a SignupRequest
  >({
    mutationFn: signupApi,
    onSuccess: (data) => {
      // 2. On success, show the message
      setSuccessMessage(data.message + ' You will be redirected to login.');

      // 3. Redirect to the login page after 3 seconds
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    },
    // onError is handled by the 'error' variable
  });

  // 4. Handle the form submission
  const handleSubmit = (data: SignupRequest) => {
    setSuccessMessage(null); // Clear any old messages
    signupUser(data);
  };

  // 5. Best Practice: Show a success UI, don't just flash and redirect
  if (successMessage) {
    return (
      <div className="mx-auto mt-10 max-w-md rounded-lg bg-white p-8 text-center shadow-md">
        <h1 className="mb-4 text-3xl font-bold text-green-600">
          Account Created!
        </h1>
        <p className="text-lg text-gray-700">{successMessage}</p>
      </div>
    );
  }

  // 6. Render the form if we are not in a success state
  return (
    <div className="mx-auto mt-10 max-w-md rounded-lg bg-white p-8 shadow-md">
      <h1 className="mb-6 text-center text-3xl font-bold text-gray-800">
        Create an Account
      </h1>

      <SignupForm onSubmit={handleSubmit} isLoading={isPending} />

      {/* 7. Best Practice: Show the *actual* error from the server */}
      {error && (
        <p className="mt-4 text-center text-sm text-red-600">
          {(error.response?.data as MessageResponse)?.message ||
            'An unknown error occurred.'}
        </p>
      )}
    </div>
  );
};

export default SignupPage;