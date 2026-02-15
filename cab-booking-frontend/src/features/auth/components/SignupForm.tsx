import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { passengerSignupSchema, driverSignupSchema, PassengerFormData, DriverFormData } from '../validation/authSchema';
import { registerDriver, registerPassenger } from '@/api/authApi';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';

type Role = 'passenger' | 'driver';

type FormData = PassengerFormData | DriverFormData;

const setCredentials = useAuthStore((state) => state.setCredentials);
const navigate = useNavigate();

export const SignupForm = () => {
    const [role, setRole] = useState<Role>('passenger');
    const [serverError, setServerError] = useState<string | null>(null);

    // We switch the schema based on the selected role
    const { register, handleSubmit, formState: { errors, isSubmitting }, reset} = useForm<FormData>({
        resolver: zodResolver(role === 'passenger' ? passengerSignupSchema : driverSignupSchema),
    });

    //Event Handlers
    const onSubmit = async (data: any) => {
        try {
            setServerError(null);
            if (role === 'passenger') {
                await registerPassenger(data);
            } else {
                await registerDriver(data);
            }
            // Redirect to log in or auto-login here
        } catch (err: any) {
                setServerError(err.response?.data?.message || "Registration failed");
        }
    };

    const handleRoleChange = (newRole: Role) => {
        setRole(newRole);
        reset(); // Clear form when switching roles
    };

    return (
        <div className="max-w-md mx-auto p-6 bg-white shadow-md rounded-lg">
            <h2 className="text-2xl font-bold mb-4">Register as {role}</h2>

            {/* Role Toggle */}
            <div className="flex gap-4 mb-6">
                <button
                    onClick={() => handleRoleChange('passenger')}
                    className={`px-4 py-2 rounded ${role === 'passenger' ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}
                >
                    Passenger
                </button>
                <button
                    onClick={() => handleRoleChange('driver')}
                    className={`px-4 py-2 rounded ${role === 'driver' ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}
                >
                    Driver
                </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium">Email</label>
                    <input {...register('email')} className="w-full border p-2 rounded" />
                    {errors.email && <p className="text-red-500 text-xs">{errors.email.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium">Password</label>
                    <input type="password" {...register('password')} className="w-full border p-2 rounded" />
                    {errors.password && <p className="text-red-500 text-xs">{errors.password.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium">Phone Number</label>
                    <input {...register('phoneNumber')} className="w-full border p-2 rounded" />
                    {errors.phoneNumber && <p className="text-red-500 text-xs">{errors.phoneNumber.message}</p>}
                </div>

                {/* Conditional Field for Driver */}
                {role === 'driver' && (
                    <div>
                        <label className="block text-sm font-medium">License Number</label>
                        <input {...register('licenseNumber' as const)} className="w-full border p-2 rounded"/>

                        {'licenseNumber' in errors && (
                            <p className="text-red-500 text-xs">
                                {errors.licenseNumber?.message as string}
                            </p>
                        )}
                    </div>
                )}

                {serverError && <p className="text-red-600 font-bold">{serverError}</p>}

                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 disabled:bg-gray-400"
                >
                    {isSubmitting ? 'Registering...' : 'Sign Up'}
                </button>
            </form>
        </div>
    );
};