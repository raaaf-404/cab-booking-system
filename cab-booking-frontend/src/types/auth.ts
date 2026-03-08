import { type UserResponse } from './api';

// Shared fields between both roles
export interface BaseSignupRequest {
    name: string;
    email: string;
    password: string;
    phoneNumber: string;
}

// Passenger specific
export interface PassengerSignupRequest extends BaseSignupRequest { }

export interface DriverSignupRequest extends BaseSignupRequest {
    licenseNumber: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    user: UserResponse;
}