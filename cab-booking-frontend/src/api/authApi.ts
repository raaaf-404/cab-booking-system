import axiosClient from './axiosClient';
import type {
  LoginRequest,
  AuthResponse,
  SignupRequest,
  MessageResponse,
  TokenRefreshRequest,
  TokenRefreshResponse,
} from '@/types/api';

/**
 * Sends a login request to the server.
 * Corresponds to: POST /api/auth/signin
 */
export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  // We use .data because axios wraps the response
  const { data: response } = await axiosClient.post('v1/auth/signin', data);
  return response;
};

/**
 * Sends a signup request to the server.
 * Corresponds to: POST /api/auth/signup
 */
export const signup = async (data: SignupRequest): Promise<MessageResponse> => {
  const { data: response } = await axiosClient.post('v1/auth/signup', data);
  return response;
};

/**
 * Sends a logout request to the server.
 * Corresponds to: POST /api/auth/signout
 */
export const logout = async (): Promise<MessageResponse> => {
  const { data: response } = await axiosClient.post('/auth/signout');
  return response;
};

/**
 * Sends a token refresh request to the server.
 * Corresponds to: POST /api/auth/refreshtoken
 */
export const refreshToken = async (
  data: TokenRefreshRequest
): Promise<TokenRefreshResponse> => {
  const { data: response } = await axiosClient.post('/auth/refreshtoken', data);
  return response;
};