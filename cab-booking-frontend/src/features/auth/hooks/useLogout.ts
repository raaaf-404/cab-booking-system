import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { logout } from '@/api/authApi';
import { useAuthStore } from '@/store/useAuthStore';

export const useLogout = () => {
    const navigate = useNavigate();
    const {clearCredentials, refreshToken} = useAuthStore();

    return useMutation({
        mutationFn: async () => {

            if (!refreshToken) return;
            // Tell the backend to delete the refresh token from the DB
            await logout({ refreshToken });
        },

        onSettled: () => {
            clearCredentials();
            navigate('/login', { replace: true });
        }
    })


}