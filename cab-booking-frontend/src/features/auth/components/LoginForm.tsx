import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema, type LoginFormInputs } from '../validation/authSchema';
import { type LoginRequest } from '@/types/api';

type LoginFormProps = {
  onSubmit: (data: LoginRequest) => void;
  isLoading: boolean;
  serverError?: string | null;
};

const LoginForm = ({ onSubmit, isLoading, serverError }: LoginFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormInputs>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const handleFormSubmit = (data: LoginFormInputs) => {
    onSubmit({
      email: data.email,
      password: data.password,
    });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="flex flex-col gap-5">

      {/* Email Field */}
      <div>
        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
          Email Address
        </label>
        <input
          id="email"
          type="email"
          {...register('email')}
          className={`block w-full rounded-md shadow-sm sm:text-sm p-2.5 border outline-none transition-all ${errors.email ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500'
            }`}
          disabled={isLoading}
          placeholder="you@example.com"
        />
        {errors.email && (
          <p className="mt-1 text-sm font-medium text-red-600">
            {errors.email.message}
          </p>
        )}
      </div>

      {/* Password Field */}
      <div>
        <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
          Password
        </label>
        <input
          id="password"
          type="password"
          {...register('password')}
          className={`block w-full rounded-md shadow-sm sm:text-sm p-2.5 border outline-none transition-all ${errors.password ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:border-blue-500 focus:ring-1 focus:ring-blue-500'
            }`}
          disabled={isLoading}
          placeholder="••••••••"
        />
        {errors.password && (
          <p className="mt-1 text-sm font-medium text-red-600">
            {errors.password.message}
          </p>
        )}
      </div>

      {/* Display Backend Error */}
      {serverError && (
        <div className="rounded-md bg-red-50 p-3 text-sm font-medium text-red-700 border border-red-200">
          {serverError}
        </div>
      )}

      {/* Submit Button */}
      <button
        type="submit"
        className="mt-2 flex w-full justify-center rounded-md border border-transparent bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-70 transition-colors"
        disabled={isLoading}
      >
        {isLoading ? (
          <span className="flex items-center gap-2">
            <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
            Signing in...
          </span>
        ) : (
          'Sign In'
        )}
      </button>
    </form>
  );
};

export default LoginForm;