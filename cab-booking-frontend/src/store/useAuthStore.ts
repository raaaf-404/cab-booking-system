import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { type User } from '@/types/api';

// 1. Define the shape of your store's state
interface AuthState {
  token: string | null;
  refreshToken: string | null;
  user: User | null;
  
  // Actions to update the state
    setCredentials: (data: {
    token: string;
    refreshToken: string;
    user: User;
  }) => void;


  clearCredentials: () => void;
}

// 2. Create the store
export const useAuthStore = create<AuthState>()(
  // 3. Use the `persist` middleware
  persist(
    (set) => ({
      // 4. Initial state
        token: null,
        refreshToken: null,
        user: null,

        //Actions
        setCredentials: (data) =>
            set({
                token: data.token,
                refreshToken: data.refreshToken,
                user: data.user,
            }),

      clearCredentials: () =>
        set({
          token: null,
          refreshToken: null,
          user: null,
        }),
    }),
    {
      // 6. Configuration for persistence
      name: 'auth-storage', // The key in localStorage
    }
  )
);