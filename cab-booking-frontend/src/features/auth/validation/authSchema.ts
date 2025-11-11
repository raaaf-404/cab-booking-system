import { z } from 'zod';

// This schema validates the data for our login form
// It's based on the LoginRequest type
export const loginSchema = z.object({
  username: z.string().min(1, { message: 'Username is required' }),
  password: z.string().min(1, { message: 'Password is required' }),
});

// We can also infer the TypeScript type from the schema
export type LoginFormInputs = z.infer<typeof loginSchema>;