export type AdminEventType =
  | 'LOGIN'
  | 'LOGOUT'
  | 'STARTER_SELECTED'
  | 'WILD_ENCOUNTER'
  | 'BATTLE'
  | 'POKEMON_CAUGHT'
  | 'ITEM_ACQUIRED'
  | 'ITEM_USED'
  | 'ACCOUNT_DISABLED';

export interface AdminGameEvent {
  id: number;
  playerId: number | null;
  type: AdminEventType;
  description: string;
  createdAt: string;
}

export type AdminPresenceStatus = 'ACTIVE' | 'DISCONNECTED';

export interface AdminUserPresence {
  userId: number;
  username: string;
  status: AdminPresenceStatus;
  lastSeenAt: string | null;
}

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  role: 'PLAYER' | 'ADMIN';
  enabled: boolean;
  emailVerified: boolean;
  registeredAt: string | null;
  accountNonExpired?: boolean;
  accountNonLocked?: boolean;
  credentialsNonExpired?: boolean;
}
