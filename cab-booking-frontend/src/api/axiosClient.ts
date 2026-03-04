import axios , { AxiosError, InternalAxiosRequestConfig}from 'axios';
import { useAuthStore } from '@/store/useAuthStore'


// 1. Create the main instance
const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: {
      'Content-Type': 'application/json',
    },
    withCredentials: true, // If you ever use cookies, this is ready
});


// 2. Flags and Queue for Concurrency Management
let isRefreshing = false;

let  failedQueue: Array<{
    resolve: (token: string) => void,
    reject: (error: any) => void
}> = [];

// Helper to process the queue
const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach((prom) => {
        if (error || !token) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// 3. Request Interceptor: Attaches the Access Token
axiosClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = useAuthStore.getState().accessToken;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
        (error) => Promise.reject(error)
);

// 4. Create a reusable cleanup function
const handleSessionFailure = (error: any) => {
    processQueue(error, null);    // A. Clear the queue
    useAuthStore.getState().clearCredentials();     // B. Clear Credentials
    return Promise.reject(error);     // C. Return the rejection
};

// 5. Response Interceptor: Handles 401 Errors
axiosClient.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        // If error is not 401 or request was already retried, reject immediately
        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }
        // Mark this request as a retry to prevent infinite loops
        originalRequest._retry = true;
        // A. If refreshing is already in progress, queue this request
        if (isRefreshing) {
            return new Promise((resolve, reject) => {
                failedQueue.push({
                    resolve: (newToken: string) => {
                        originalRequest.headers.Authorization = `Bearer ${newToken}`;
                        resolve(axiosClient(originalRequest));
                    },
                    reject: (err) => {
                        reject(err);
                    },
                });
            });
        }
        // B. If not refreshing, start the refresh process
        isRefreshing = true;

        try {
            const refreshToken = useAuthStore.getState().refreshToken;
           // Handle the missing refresh token explicitly
            if (!refreshToken) {
                return handleSessionFailure(new Error("No refresh token available"));
            }

            const currentUser = useAuthStore.getState().user;

            if (!currentUser) {
                return handleSessionFailure(new Error("No user found in state during refresh"));
            }

            // We use a clean axios instance to avoid interceptors on the refresh call itself
            // This prevents circular dependencies and infinite loops
            const response = await axios.post(
                `${import.meta.env.VITE_API_URL}/auth/refresh-token`,
                { refreshToken }
            );

            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data; // Adjust based on your actual API response structure

            // Update the store
            useAuthStore.getState().setCredentials({
                accessToken: newAccessToken,
                refreshToken: newRefreshToken || refreshToken,
                user: currentUser
            });

            // Process the queue with the new token
            processQueue(null, newAccessToken);

            // Update the original header and retry
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return axiosClient(originalRequest);

        } catch (refreshError) {
            return handleSessionFailure(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

export default axiosClient;