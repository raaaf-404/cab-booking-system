import axiosClient from './axiosClient';
import type {
  LoginRequest,
  LogoutRequest,
  MessageResponse,
  TokenRefreshRequest,
  TokenRefreshResponse,
} from '@/types/api';

import type {
  PassengerSignupRequest,
  DriverSignupRequest,
  AuthResponse
}
  from '../types/auth';

export const registerPassenger = async (
  data: PassengerSignupRequest
): Promise<AuthResponse> => {
  // We wrap this in a try/catch if we want global logging,
  // otherwise, let the caller (like TanStack Query) handle the error.
  const { data: response } = await axiosClient.post<AuthResponse>('/auth/register/passenger', data);
  return response;
};

export const registerDriver = async (
  data: DriverSignupRequest
): Promise<AuthResponse> => {
  const { data: response } = await axiosClient.post<AuthResponse>('/auth/signup/driver', data);
  return response;
};
/**
 * Sends a login request to the server.
 */
export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  // We use .data because axios wraps the response
  const { data: response } = await axiosClient.post('/auth/login', data);
  return response;
};

/**
 * Sends a logout request to the server.
 */
export const logout = async (data: LogoutRequest): Promise<MessageResponse> => {
  const { data: response } = await axiosClient.post('/auth/logout', data);
  return response;
};

/**
 * Sends a token refresh request to the server.
 */
export const refreshToken = async (
  data: TokenRefreshRequest
): Promise<TokenRefreshResponse> => {
  const { data: response } = await axiosClient.post('/auth/refreshtoken', data);
  return response;
};