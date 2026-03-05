// ==========================================

export type AppRole = 'passenger' | 'driver' | 'admin';
// 1. CORE DOMAIN MODELS (The "Things")
// ==========================================
// Corresponds to UserResponse.java
export interface User {
  id: number;
  email: string;
  role: AppRole;
}


// Corresponds to CabResponse.java
export type Cab = {
    cabId: number;
    cabType: string;
    licensePlate: string;
    driver: User; //User type is from UserResponse
    available: boolean;
    currenLocation: string;
};

export type Booking = {
    bookingId: number;
    user: User;
    cab: Cab;
    pickupLocation: string;
    dropoffLocation: string;
    pickupTime: string;
    dropOffTime: string;
    bookingStatus: string;
    fare: number;
};

// Corresponds to LoginRequest.java
export type LoginRequest = {
  email: string;
  password: string;
};

export type LogoutRequest = {
    refreshToken: string;
}
// Corresponds to SignupRequest.java
export type SignupRequest = {
  name: string;
  email: string;
  password: string;
  phone: string;
  roles?: string[]; // We make this optional on the frontend
};
// Corresponds to MessageResponse.java
export type MessageResponse = {
  message: string;
};

export type TokenRefreshRequest = {
    refreshToken: string;
};

export type TokenRefreshResponse = {
    accessToken: string;
    refreshToken: string;
    tokenType: string;  // e.g., "Bearer"
};


