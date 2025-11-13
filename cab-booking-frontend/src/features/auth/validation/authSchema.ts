import { z } from 'zod';

// This schema validates the data for our login form
export const loginSchema = z.object({
  // Use simple string for message
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

export type LoginFormInputs = z.infer<typeof loginSchema>;

// This schema validates the data for our signup form
export const signupSchema = z
  .object({
    username: z
      .string()
      .min(3, 'Username must be at least 3 characters'),
    
    email: z.string().email('Invalid email address'), // No object
    
    password: z
      .string()
      .min(6, 'Password must be at least 6 characters'),
    
    confirmPassword: z
      .string()
      .min(1, 'Please confirm your password'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    // .refine() is the exception and STILL uses an object
    message: "Passwords don't match",
    path: ['confirmPassword'], 
  });

export type SignupFormInputs = z.infer<typeof signupSchema>;