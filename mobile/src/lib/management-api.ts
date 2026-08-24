import { api } from "./api";

export type ManagementDashboard = {
  users: { total:number; active:number; inactive:number; suspended:number; newToday:number; newThisWeek:number };
  communities: { total:number; active:number; pendingRequests:number };
  events: { total:number; upcoming:number; pendingApproval:number };
  moderation: { openReports:number; flaggedPosts:number; flaggedComments:number };
  communications: { notificationsSent:number };
};
export type ManagementContext = { superAdmin:boolean; authorities:string[]; permissions:string[] };
export type ManagedUser = { id:number; login:string; firstName?:string; lastName?:string; email?:string; activated:boolean; authorities:string[]; createdDate?:string };
export type NewManagedUser = Omit<ManagedUser,"id"|"createdDate"> & {langKey?:string};
export type Community = { id?:number; name:string; slug:string; description?:string|null; visibility:string; coverImageUrl?:string|null; communityType:string; partnerTrustLevel:string; isVerifiedPartner:boolean; isDiscoverable:boolean; createdAt?:string; updatedAt?:string; leadModerator?:{id?:number;displayName?:string}|null };
export type ManagedEvent = { id?:number; title:string; description?:string|null; startsAt:string; endsAt?:string|null; locationName?:string|null; addressText?:string|null; latitude?:number|null; longitude?:number|null; coverImageUrl?:string|null; capacity?:number|null; status:string; rejectionReason?:string|null; publishedAt?:string|null; publishedToDiscovery:boolean; groupModerationStatus:string; platformModerationStatus:string; cancelledAt?:string|null; cancellationReason?:string|null; createdAt?:string; updatedAt?:string; community?:{id?:number;name?:string}|null };
export type ManagementNotification={id:number;notificationId:number;title:string;body?:string;notificationType:string;source:string;trigger?:string;referenceType?:string;referenceId?:string;recipientUserId:number;firebaseStatus:string;firebaseMessageId?:string;pushSuccessful:number;pushFailed:number;attemptCount:number;firebaseErrors:Record<string,number>;createdAt:string;sentAt?:string;lastError?:string};

export const managementApi = {
  context: () => api.rest.get<ManagementContext>("/api/admin/context"),
  dashboard: () => api.rest.get<ManagementDashboard>("/api/admin/dashboard"),
  users: (page=0, size=20) => api.rest.get<ManagedUser[]>(`/api/admin/users?page=${page}&size=${size}&sort=createdDate,desc`),
  getUserById: (id:number) => api.rest.get<ManagedUser>(`/api/admin/users/${id}`),
  user: (id:number|string) => {
    const parsed=Number(id);
    if(!Number.isSafeInteger(parsed)||parsed<=0) return Promise.reject(new Error("Geçersiz kullanıcı kimliği."));
    return api.rest.get<ManagedUser>(`/api/admin/users/${parsed}`);
  },
  updateUser: (user:ManagedUser) => api.rest.put<ManagedUser>("/api/admin/users",user),
  createUser: (user:NewManagedUser) => api.rest.post<ManagedUser>("/api/admin/users",user),
  setUserAuthority: (id:number|string, authority:string) => api.rest.put<ManagedUser>(`/api/admin/users/${id}/authority`,{authority}),
  setUserActivated: (id:number, activated:boolean) => api.rest.post(`/api/admin/users/${id}/${activated?"activate":"deactivate"}`),
  communities: (page=0,size=20) => api.rest.get<any[]>(`/api/communities?page=${page}&size=${size}&sort=createdAt,desc`),
  getCommunityById: (id:number) => api.rest.get<Community>(`/api/communities/${id}`),
  createCommunity: (value:Community) => api.rest.post<Community>("/api/communities",value),
  updateCommunity: (id:number,value:Community) => api.rest.put<Community>(`/api/communities/${id}`,{...value,id}),
  deleteCommunity: (id:number) => api.rest.delete(`/api/communities/${id}`),
  deactivateCommunity: (value:Community) => value.id?api.rest.put<Community>(`/api/communities/${value.id}`,{...value,isDiscoverable:false,updatedAt:new Date().toISOString()}):Promise.reject(new Error("Topluluk kimliği yok.")),
  events: (page=0,size=20) => api.rest.get<any[]>(`/api/events?page=${page}&size=${size}&sort=startsAt,desc`),
  getEventById: (id:number) => api.rest.get<ManagedEvent>(`/api/events/${id}`),
  createEvent: (value:ManagedEvent) => api.rest.post<ManagedEvent>("/api/events",value),
  updateEvent: (id:number,value:ManagedEvent) => api.rest.put<ManagedEvent>(`/api/events/${id}`,{...value,id}),
  reports: (page=0,size=20) => api.rest.get<any[]>(`/api/reports?page=${page}&size=${size}&sort=createdAt,desc`),
  notificationOutbox: (page=0,size=20) => api.rest.get<any[]>(`/api/admin/notifications/outbox?page=${page}&size=${size}&sort=createdAt,desc`)
  ,notifications: (page=0,size=20,status?:string) => api.rest.get<ManagementNotification[]>(`/api/admin/notifications?page=${page}&size=${size}&sort=createdAt,desc${status?`&status=${status}`:""}`)
  ,notification: (id:number) => api.rest.get<ManagementNotification>(`/api/admin/notifications/${id}`)
  ,notificationStats: () => api.rest.get<Record<string,number>>("/api/admin/notifications/stats")
  ,notificationIntegration: () => api.rest.get<{firebaseConnected:boolean;fcmReady:boolean;bigQueryConfigured:boolean;bigQueryMessage:string}>("/api/admin/notifications/integration-status")
  ,sendNotification: (value:{userIds:number[];title:string;body:string;route?:string;pushEnabled:boolean;inAppEnabled:boolean;scheduledAt:null;priority:string}) => api.rest.post("/api/admin/notifications/send",value)
  ,retryNotification: (id:number) => api.rest.post(`/api/admin/notifications/${id}/retry`)
  ,authorities: () => api.rest.get<Array<{name:string}>>("/api/authorities")
};
