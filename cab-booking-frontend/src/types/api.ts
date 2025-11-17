// Corresponds to UserResponse.java
export type User = {
  id: number; // Java Long is best represented as number in TS
  username: string;
  email: string;
  roles: string[];
};

// Corresponds to LoginRequest.java
export type LoginRequest = {
  username: string;
  password: string;
};

// Corresponds to SignupRequest.java
export type SignupRequest = {
  username: string;
  email: string;
  password: string;
  roles?: string[]; // We make this optional on the frontend
};

// Corresponds to JwtResponse.java
// We'll call it AuthResponse as this is a common frontend convention
export type AuthResponse = {
    token: string;
    refreshToken: string;
    user: User;
    type: string; // e.g., "Bearer"
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


