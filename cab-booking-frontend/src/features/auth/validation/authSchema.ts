import { z } from 'zod';

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