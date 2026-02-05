// Shared fields between both roles
export interface BaseSignupRequest {
    email: string;
    password: string;
    phoneNumber: string;
}

// Passenger specific
export interface PassengerSignupRequest extends BaseSignupRequest {}

export interface DriverSignupRequest extends BaseSignupRequest {
    licenseNumber: number;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    email: string;
    roles: string[];
}