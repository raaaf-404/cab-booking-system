import { z } from 'zod';

// Base schema matching your Java @Pattern and @Size constraints
const baseSchema = {
    email: z.email("Invalid email address"),
    password: z.string().min(8, "Password must be at least 8 characters"),
    phoneNumber: z.string().regex(/^\+?[0-9]{10,15}$/, "Invalid phone number"),
};

export const passengerSignupSchema = z.object({
    ...baseSchema,
});

export const driverSignupSchema = z.object({
    ...baseSchema,
    licenseNumber: z.string().min(1, "License number is required"),
});

// Infer types from schemas
export type PassengerFormData = z.infer<typeof passengerSignupSchema>;
export type DriverFormData = z.infer<typeof driverSignupSchema>;

// This schema validates the data for our login form
export const loginSchema = z.object({
  email: z
      .string()
      .min(1, 'Username is required')
      .email('Please provide a valid email address'),
  password: z
      .string()
      .min(1, 'Password is required'),
});

export type LoginFormInputs = z.infer<typeof loginSchema>;

// This schema validates the data for our signup form
export const signupSchema = z
    .object({
        name: z
            .string()
            .min(1, 'Name is required'), // Replaces 'username'

        email: z.string().email('Invalid email address'),

        phone: z
            .string()
            .min(10, 'Phone number must be at least 10 digits') // Basic validation
            .regex(/^\+?[0-9\s-]*$/, 'Invalid phone number format'),

        password: z
            .string()
            .min(6, 'Password must be at least 6 characters'), // Matches @Size(min=6) in Java

        confirmPassword: z
            .string()
            .min(1, 'Please confirm your password'),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: "Passwords don't match",
        path: ['confirmPassword'],
    });

export type SignupFormInputs = z.infer<typeof signupSchema>;