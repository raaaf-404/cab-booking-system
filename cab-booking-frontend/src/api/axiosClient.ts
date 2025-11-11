import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore'; // We'll create this store in the next step

// 1. Get the backend URL.
// We'll hardcode it for now, but in a real app, this should come 
// from an environment variable (e.g., import.meta.env.VITE_API_URL)
const API_BASE_URL = 'http://localhost:8080/api';

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
});

// 2. The "Best Practice" Interceptor
// This function will run before EVERY request
axiosClient.interceptors.request.use(
  (config) => {
    // 3. Get the token from our global state (Zustand)
    // We use .getState() for use *outside* of a React component
    const { token } = useAuthStore.getState();

    if (token) {
      // 4. If the token exists, add it to the Authorization header
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    // This will handle any errors from setting up the request
    return Promise.reject(error);
  }
);

export default axiosClient;