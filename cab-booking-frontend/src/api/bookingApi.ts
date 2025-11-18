import axiosClient from './axiosClient';
import type { Booking } from '@/types/api';

/**
 * Fetches the current user's booking history.
 * Corresponds to: GET /api/bookings/my-bookings
 */
export const getMyBookings = async (): Promise<Booking[]> => {
  // We don't need to pass an ID because the backend 
  // gets the user from the JWT token in the header.
    const { data } = await axiosClient.get('/booking/my-bookings');
    return data; 
};