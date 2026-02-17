import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { passengerSignupSchema, driverSignupSchema, PassengerFormData, DriverFormData } from '../validation/authSchema';
import { registerDriver, registerPassenger } from '@/api/authApi';
import { FormField } from '@/components/ui/FormField';

import { Link ,useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';


const EyeIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5 text-gray-500">
        <path strokeLinecap="round" strokeLinejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
        <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
    </svg>
);

const EyeSlashIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5 text-gray-500">
        <path strokeLinecap="round" strokeLinejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.523 10.523 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L9.88 9.88" />
    </svg>
);

type Role = 'passenger' | 'driver';
type FormData = PassengerFormData | DriverFormData;

export const SignupForm = () => {

    const [role, setRole] = useState<Role>('passenger');
    const [serverError, setServerError] = useState<string | null>(null);
    const [showPassword, setShowPassword] = useState<boolean>(false);
    const setCredentials = useAuthStore((state ) => state.setCredentials);
    const navigate = useNavigate();

    // We switch the schema based on the selected role
    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        reset,
        setValue
    } = useForm<FormData>({
        resolver: zodResolver(role === 'passenger' ? passengerSignupSchema : driverSignupSchema),
        defaultValues: {
        phoneNumber: '+63', // UX: Pre-fill the country code
        }
    });

    // 1. Sanitization Helper
    // Prevents user from typing letters or special chars in phone field
    const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value;
        // Allow only numbers and the '+' symbol
        const sanitizedValue = rawValue.replace(/[^0-9+]/g, '');
        setValue('phoneNumber', sanitizedValue, { shouldValidate: true });
    };

    //Event Handlers
    const onSubmit = async (data: any) => {
        try {
            setServerError(null);

            const cleanData = {
                ...data,
                email: data.email.trim(),
                phoneNumber: data.phoneNumber.replace(/\s/g, '')
            };

            let response;
            if (role === 'passenger') {
               response = await registerPassenger(cleanData);
            } else {
                response = await registerDriver(cleanData);
            }

            setCredentials(response);

            //TODO LATER: Give a small success feedback before redirecting?
            const redirectPath = role === 'driver' ? '/driver/dashboard' : '/home';
            navigate(redirectPath);

            // Redirect to log in or auto-login here
        } catch (err: any) {
            const message = err.response?.data?.message || "Registration failed. Please try again.";
            setServerError(message);
        }
    };

    const handleRoleChange = (newRole: Role) => {
        setRole(newRole);
        setServerError(null);
        reset({ phoneNumber: '+63' }); // Reset form and keep country code
    };

    return (
        <div className="max-w-md mx-auto p-8 bg-white shadow-xl rounded-2xl border border-gray-100">
            <div className="text-center mb-8">
                <h2 className="text-3xl font-extrabold text-gray-900">Create Account</h2>
                <p className="text-sm text-gray-500 mt-2">Join us as a {role}</p>
            </div>

            {/* Custom Role Toggle */}
            <div className="grid grid-cols-2 gap-2 p-1 bg-gray-100 rounded-lg mb-8">
                <button
                    type="button" // Important: prevents form submission
                    onClick={() => handleRoleChange('passenger')}
                    className={`text-sm font-medium py-2 rounded-md transition-all duration-200 ${
                        role === 'passenger'
                            ? 'bg-white text-blue-600 shadow-sm'
                            : 'text-gray-500 hover:text-gray-700'
                    }`}
                >
                    Passenger
                </button>
                <button
                    type="button"
                    onClick={() => handleRoleChange('driver')}
                    className={`text-sm font-medium py-2 rounded-md transition-all duration-200 ${
                        role === 'driver'
                            ? 'bg-white text-blue-600 shadow-sm'
                            : 'text-gray-500 hover:text-gray-700'
                    }`}
                >
                    Driver
                </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
                {/* Email Field */}
                <FormField
                    label="Email Address"
                    htmlFor="email"
                    error={errors.email?.message}
                >
                    <input
                        id="email" // Matches htmlFor
                        {...register('email')}
                        placeholder="you@example.com"
                        className="w-full border border-gray-300 px-4 py-2.5 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                    />
                </FormField>

                {/* 2. Password Field*/}
                <FormField
                    label="Password"
                    htmlFor="password"
                    error={errors.password?.message}
                    description="Must be atleast 8 characters"
                >
                    <div className="relative">
                        <input
                            id="password"
                            {...register('password')}
                            type={showPassword ? "text" : "password"}
                            placeholder="••••••••"
                            className="w-full border border-gray-300 px-4 py-2.5 rounded-lg focus:ring-2 focus:ring-blue-500
                            focus:border-blue-500 outline-none transition-all pr-10"
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                        >
                            {showPassword ? <EyeSlashIcon /> : <EyeIcon />}
                        </button>
                    </div>
                </FormField>

                {/* 1. Phone Number with Sanitization */}
                <FormField
                    label="Phone Number"
                    htmlFor="phone"
                    error={errors.phoneNumber?.message}
                >
                    <input
                        id="phone"
                        {...register('phoneNumber')}
                        onChange={handlePhoneChange}
                        placeholder="+63 900 000 0000"
                        className="w-full border border-gray-300 px-4 py-2.5 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                    />
                </FormField>

                {/* Conditional Field for Driver */}
                {role === 'driver' && (
                    <div className="animate-in fade-in slide-in-from-top-2 duration-300">
                        <label className="block text-sm font-semibold text-gray-700 mb-1">License Number</label>
                        <input
                            {...register('licenseNumber' as const)}
                            placeholder="L00-00-000000"
                            className="w-full border border-gray-300 px-4 py-2.5 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                        />
                        {'licenseNumber' in errors && (
                            <p className="text-red-500 text-xs mt-1 font-medium">
                                {errors.licenseNumber?.message as string}
                            </p>
                        )}

                        <FormField
                            label="License Number"
                            htmlFor="licenseNumber"
                            error={'licenseNumber' in errors ? errors.licenseNumber?.message : undefined}
                        >
                            <input
                                id="licenseNumber"
                                {...register('licenseNumber' as const)}
                                placeholder="L00-00-000000"
                                className="w-full border border-gray-300 px-4 py-2.5 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                            />
                        </FormField>
                    </div>
                )}

                {/* Server Error Alert */}
                {serverError && (
                    <div className="p-3 bg-red-50 border border-red-200 rounded-lg flex items-center gap-2">
                        <svg className="w-5 h-5 text-red-600 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" /></svg>
                        <p className="text-sm text-red-700 font-medium">{serverError}</p>
                    </div>
                )}

                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="w-full bg-blue-600 text-white font-bold py-3 rounded-lg hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed transition-colors shadow-md hover:shadow-lg"
                >
                    {isSubmitting ? (
                        <span className="flex items-center justify-center gap-2">
                            <svg className="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                            Creating Account...
                        </span>
                    ) : 'Sign Up'}
                </button>
            </form>

            {/* 3. "Already have an account?" Link */}
            <div className="mt-8 text-center border-t border-gray-100 pt-6">
                <p className="text-sm text-gray-600">
                    Already have an account?{' '}
                    <Link to="/login" className="font-semibold text-blue-600 hover:text-blue-500 hover:underline">
                        Log in here
                    </Link>
                </p>
            </div>
        </div>
    );
};