import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { type UserResponse } from '@/types/api';
import { type AuthResponse } from '@/types/auth.ts';

// 1. Define the shape of your store's state
interface AuthState {
    accessToken: string | null;
    refreshToken: string | null;
    user: UserResponse | null;
    isHydrated: boolean;
    setCredentials: (data: AuthResponse) => void;
    clearCredentials: () => void;
    setHydrated: (state: boolean) => void;
}

// 2. Create the store
export const useAuthStore = create<AuthState>()(
  // 3. Use the `persist` middleware
  persist(
    (set) => ({
      // 4. Initial state
        accessToken: null,
        refreshToken: null,
        user: null,
        isHydrated: false,

        //Actions
        setCredentials: (data) =>
            set({
                accessToken: data.accessToken,
                refreshToken: data.refreshToken,
                user: data.user,
            }),

      clearCredentials: () =>
        set({
            accessToken: null,
          refreshToken: null,
          user: null,
        }),
        setHydrated: (state) => set({isHydrated: state}),
    }),
    {
      // 6. Configuration for persistence
      name: 'auth-storage', // The key in localStorage
      onRehydrateStorage: () => (state) => {
          state?.setHydrated(true);
      },
    }
  )
);