import { api } from "./api";

export type DirectConversation = {
  id: number;
  otherProfileId: number;
  displayName: string;
  username: string;
  avatarUrl: string | null;
  lastMessage: string | null;
  lastMessageAt: string;
  unreadCount: number;
};

export type DirectMessage = {
  id: number;
  conversationId: number;
  senderProfileId: number;
  body: string;
  createdAt: string;
  readAt: string | null;
};

export type MessageRecipient = {
  profileId: number;
  displayName: string;
  username: string;
  avatarUrl: string | null;
};

export async function getConversations(filter: "ALL" | "UNREAD", query = "") {
  const data = await api.rest.get<DirectConversation[]>(`/api/app/messages/conversations?filter=${filter}&q=${encodeURIComponent(query)}`);
  return Array.isArray(data) ? data : [];
}

export async function searchMessageRecipients(query = "") {
  const data = await api.rest.get<MessageRecipient[]>(`/api/app/messages/people?q=${encodeURIComponent(query)}`);
  return Array.isArray(data) ? data : [];
}

export function startConversation(recipientProfileId: number) {
  return api.rest.post<DirectConversation>("/api/app/messages/conversations", { recipientProfileId });
}

export async function getDirectMessages(conversationId: number) {
  const data = await api.rest.get<DirectMessage[]>(`/api/app/messages/conversations/${conversationId}/messages`);
  return Array.isArray(data) ? data : [];
}

export function sendDirectMessage(conversationId: number, body: string) {
  return api.rest.post<DirectMessage>(`/api/app/messages/conversations/${conversationId}/messages`, { body });
}

export function markConversationRead(conversationId: number) {
  return api.rest.put<void>(`/api/app/messages/conversations/${conversationId}/read`);
}
