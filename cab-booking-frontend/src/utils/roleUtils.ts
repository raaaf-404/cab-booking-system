
import { AppRole } from '@/types/api';

export const normalizeRole = (role: string): AppRole => {
    return role?.toLowerCase().replace('role_', '') as AppRole;
};