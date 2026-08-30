export type SpringClientOptions = {
  getBaseUrl: () => string;
  getToken: () => Promise<string | null>;
  setToken: (token: string | null) => Promise<void>;
  diagnostics?: boolean;
};

type Filter = {
  op: string;
  column: string;
  value: unknown;
  negate?: boolean;
};

type AuthUser = { id: string; email: string };
type AuthSession = { access_token: string; user: AuthUser } | null;

type FollowDto = {
  id: number;
  createdAt: string;
  follower?: { id: number } | null;
  followed?: { id: number } | null;
};

type CommunityMemberDto = {
  id: number;
  role: string;
  status: string;
  createdAt: string;
  community?: { id: number; name?: string; slug?: string; coverImageUrl?: string | null } | null;
  user?: { id: number } | null;
};

type CommunityDto = {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  coverImageUrl?: string | null;
  communityType?: "category_hub" | "partner_hub";
  partnerTrustLevel?: "new" | "verified" | "trusted";
  isVerifiedPartner?: boolean;
  createdAt?: string;
  createdBy?: { id: number } | null;
  parent?: { id: number } | null;
};

type EventDto = {
  id: number;
  title: string;
  description?: string | null;
  startsAt?: string;
  endsAt?: string | null;
  locationName?: string | null;
  addressText?: string | null;
  coverImageUrl?: string | null;
  capacity?: number | null;
  status?: string;
  groupModerationStatus?: string;
  platformModerationStatus?: string;
  createdAt?: string;
  community?: { id: number; name?: string; slug?: string } | null;
  category?: { id: number; name?: string; slug?: string } | null;
  createdBy?: { id: number } | null;
};

type PostMediaDto = {
  id: number;
  mediaType?: string;
  storagePath?: string;
  sortOrder?: number;
};

type PostDto = {
  id: number;
  body?: string | null;
  visibility?: string;
  moderationStatus?: string;
  createdAt?: string;
  updatedAt?: string;
  author?: { id: number } | null;
  community?: { id: number; name?: string; slug?: string } | null;
  event?: { id: number; title?: string } | null;
  media?: PostMediaDto[] | null;
};

type ProfileDto = {
  id: number;
  displayName?: string;
  username?: string;
  avatarUrl?: string | null;
  bio?: string | null;
  city?: string | null;
  birthDate?: string | null;
  status?: string;
  isVerified?: boolean;
  user?: { email?: string } | null;
};

type ProfileShape = {
  id: string;
  email: string;
  display_name: string;
  username: string;
  avatar_url: string | null;
  bio: string | null;
  city: string | null;
  birth_date: string | null;
  status: string;
  is_verified: boolean;
};

type AccountPreferencesDto = {
  id: number;
  discoverable?: boolean;
  showCity?: boolean;
  showFollowConnections?: boolean;
  allowFollows?: boolean;
  requireFollowApproval?: boolean;
  allowMessagesFrom?: string;
  notifyEvents?: boolean;
  notifyCommunities?: boolean;
  notifySocial?: boolean;
  notifyAdvantages?: boolean;
  notifySystem?: boolean;
  updatedAt?: string;
};

type AccountPreferencesShape = {
  id: number;
  discoverable: boolean;
  show_city: boolean;
  show_follow_connections: boolean;
  allow_follows: boolean;
  require_follow_approval: boolean;
  allow_messages_from: "everyone" | "following" | "no_one";
  notify_events: boolean;
  notify_communities: boolean;
  notify_social: boolean;
  notify_advantages: boolean;
  notify_system: boolean;
};

type CommentDto = {
  id: number;
  targetType?: string;
  targetId?: string;
  body?: string;
  moderationStatus?: string;
  createdAt?: string;
  author?: { id: number; displayName?: string } | null;
};

type CommentShape = {
  id: string;
  author_id: string | null;
  body: string;
  created_at: string;
  profiles?: { display_name?: string | null } | null;
};

type EventRatingDto = {
  id: number;
  rating?: number;
  reviewText?: string | null;
  createdAt?: string;
  user?: { id: number } | null;
  event?: { id: number; title?: string } | null;
};

type EventRatingShape = {
  id: string;
  event_id: string;
  user_id: string;
  rating: number;
  review_text: string | null;
  created_at: string;
};

function asError(message: string) {
  return { message };
}

export function createSpringClient(options: SpringClientOptions) {
  const authListeners = new Set<(event: string, session: AuthSession) => void>();

  async function request<T>(path: string, init: RequestInit & { auth?: boolean; json?: unknown } = {}): Promise<T> {
    const headers = new Headers(init.headers);
    if (init.json !== undefined && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
    if (init.auth !== false) {
      const token = await options.getToken();
      if (token) headers.set("Authorization", `Bearer ${token}`);
    }
    const resolvedUrl = `${options.getBaseUrl()}${path}`;
    const response = await fetch(resolvedUrl, {
      ...init,
      headers,
      body: init.json === undefined ? init.body : JSON.stringify(init.json)
    });
    if (options.diagnostics) {
      console.info(`[Bialem HTTP] ${init.method || "GET"} ${resolvedUrl} status=${response.status}`);
    }
    const contentType = response.headers.get("content-type") ?? "";
    const payload = contentType.includes("application/json")
      ? await response.json().catch(() => null)
      : await response.text().catch(() => null);
    if (!response.ok) {
      const message =
        payload && typeof payload === "object" && "detail" in payload
          ? String((payload as { detail: string }).detail)
          : payload && typeof payload === "object" && "title" in payload
            ? String((payload as { title: string }).title)
            : payload && typeof payload === "object" && "error" in payload
              ? String((payload as { error: string }).error)
              : `İstek başarısız (${response.status})`;
      throw Object.assign(new Error(message), { status: response.status, body: payload });
    }
    return payload as T;
  }

  class QueryBuilder<T = any> implements PromiseLike<{ data: T; error: { message: string } | null; count: number | null }> {
    private action = "select";
    private selectClause = "*";
    private filters: Filter[] = [];
    private orderColumn?: string;
    private orderAsc = true;
    private limitValue?: number;
    private singleResult = false;
    private headOnly = false;
    private countExact = false;
    private payload: unknown;
    private onConflict?: string;

    constructor(private table: string) {}

    select(columns = "*", opts?: { count?: "exact"; head?: boolean }) {
      this.selectClause = columns;
      this.countExact = opts?.count === "exact";
      this.headOnly = Boolean(opts?.head);
      return this;
    }

    insert(payload: unknown) {
      this.action = "insert";
      this.payload = payload;
      return this;
    }

    update(payload: unknown) {
      this.action = "update";
      this.payload = payload;
      return this;
    }

    upsert(payload: unknown, opts?: { onConflict?: string }) {
      this.action = "upsert";
      this.payload = payload;
      this.onConflict = opts?.onConflict;
      return this;
    }

    delete() {
      this.action = "delete";
      return this;
    }

    eq(column: string, value: unknown) {
      this.filters.push({ op: "eq", column, value });
      return this;
    }

    neq(column: string, value: unknown) {
      this.filters.push({ op: "neq", column, value });
      return this;
    }

    is(column: string, value: unknown) {
      this.filters.push({ op: "is", column, value });
      return this;
    }

    not(column: string, operator: string, value: unknown) {
      this.filters.push({ op: operator, column, value, negate: true });
      return this;
    }

    in(column: string, value: unknown[]) {
      this.filters.push({ op: "in", column, value });
      return this;
    }

    ilike(column: string, value: string) {
      this.filters.push({ op: "ilike", column, value });
      return this;
    }

    order(column: string, opts?: { ascending?: boolean }) {
      this.orderColumn = column;
      this.orderAsc = opts?.ascending !== false;
      return this;
    }

    limit(value: number) {
      this.limitValue = value;
      return this;
    }

    maybeSingle<U = T>() {
      this.singleResult = true;
      this.limitValue = 1;
      return this as unknown as QueryBuilder<U>;
    }

    single<U = T>() {
      return this.maybeSingle<U>();
    }

    then<TResult1 = { data: T; error: { message: string } | null; count: number | null }, TResult2 = never>(
      onfulfilled?: ((value: { data: T; error: { message: string } | null; count: number | null }) => TResult1 | PromiseLike<TResult1>) | null,
      onrejected?: ((reason: any) => TResult2 | PromiseLike<TResult2>) | null
    ) {
      return this.execute().then(onfulfilled, onrejected);
    }

    private async execute() {
      try {
        const result = await request<{ data: any; error?: string; count?: number }>("/api/app/query", {
          method: "POST",
          json: {
            table: this.table,
            action: this.action,
            select: this.selectClause,
            filters: this.filters,
            orderColumn: this.orderColumn,
            orderAsc: this.orderAsc,
            limit: this.limitValue,
            single: this.singleResult,
            head: this.headOnly,
            count: this.countExact,
            payload: this.payload,
            onConflict: this.onConflict
          }
        });
        return {
          data: result.data ?? (this.singleResult ? null : []),
          error: result.error ? asError(result.error) : null,
          count: result.count ?? null
        };
      } catch (error) {
        return {
          data: this.singleResult ? null : [],
          error: asError(error instanceof Error ? error.message : "İstek başarısız"),
          count: null
        };
      }
    }
  }

  async function currentSession(): Promise<AuthSession> {
    const token = await options.getToken();
    if (!token) return null;
    try {
      const profile = await request<{ id: string; email: string }>("/api/app/me", { method: "GET" });
      return { access_token: token, user: { id: String(profile.id), email: profile.email } };
    } catch {
      await options.setToken(null);
      return null;
    }
  }

    return {
    rest: {
      get<T>(path: string) {
        return request<T>(path);
      },
      post<T>(path: string, json?: unknown) {
        return request<T>(path, { method: "POST", json });
      },
      put<T>(path: string, json?: unknown) {
        return request<T>(path, { method: "PUT", json });
      },
      delete<T>(path: string) {
        return request<T>(path, { method: "DELETE" });
      }
    },
    from(table: string) {
      return new QueryBuilder(table);
    },
    follows: {
      async listByFollower(followerId: string) {
        try {
          const data = await request<FollowDto[]>(`/api/follows?followerId=${encodeURIComponent(followerId)}`);
          return { data, error: null };
        } catch (error) {
          return { data: [] as FollowDto[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    communityMembers: {
      async listByUser(userId: string, status?: string) {
        try {
          const params = new URLSearchParams({ userId });
          if (status) params.set("status", status);
          const data = await request<CommunityMemberDto[]>(`/api/community-members?${params.toString()}`);
          return { data, error: null };
        } catch (error) {
          return { data: [] as CommunityMemberDto[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async listByCommunity(communityId: string, status?: string) {
        try {
          const params = new URLSearchParams();
          if (status) params.set("status", status);
          const data = await request<CommunityMemberDto[]>(`/api/communities/${encodeURIComponent(communityId)}/members${params.toString() ? "?" + params.toString() : ""}`);
          return { data, error: null };
        } catch (error) {
          return { data: [] as CommunityMemberDto[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    communities: {
      async getById(id: string) {
        try {
          const data = await request<CommunityDto>(`/api/communities/${encodeURIComponent(id)}`);
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async list(params?: { parentId?: string | null; communityType?: string; sort?: string; size?: number }) {
        try {
          const query = new URLSearchParams();
          query.set("size", String(params?.size ?? 1000));
          if (params?.sort) query.set("sort", params.sort);
          if (params?.communityType) query.set("communityType.equals", params.communityType);
          if (params?.parentId === null) query.set("parentId.specified", "false");
          else if (params?.parentId) query.set("parentId.equals", params.parentId);
          const data = await request<CommunityDto[]>(`/api/communities?${query.toString()}`);
          return { data, error: null };
        } catch (error) {
          return { data: [] as CommunityDto[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async countByCreator(userId: string) {
        try {
          const data = await request<number>(`/api/communities/count?createdById.equals=${encodeURIComponent(userId)}`);
          return { data, error: null };
        } catch (error) {
          return { data: 0, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    events: {
      async getById(id: string) {
        try {
          const data = await request<EventDto>(`/api/events/${encodeURIComponent(id)}`);
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async list(params?: { status?: string | string[]; communityId?: string; createdById?: string; sort?: string; size?: number }) {
        try {
          const query = new URLSearchParams();
          query.set("size", String(params?.size ?? 1000));
          if (params?.sort) query.set("sort", params.sort);
          if (params?.communityId) query.set("communityId.equals", params.communityId);
          if (params?.createdById) query.set("createdById.equals", params.createdById);
          if (params?.status) {
            const statuses = Array.isArray(params.status) ? params.status : [params.status];
            statuses.forEach((s) => query.append("status.in", s.toUpperCase()));
          }
          const data = await request<EventDto[]>(`/api/events?${query.toString()}`);
          return { data, error: null };
        } catch (error) {
          return { data: [] as EventDto[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async countByCreator(userId: string) {
        try {
          const data = await request<number>(`/api/events/count?createdById.equals=${encodeURIComponent(userId)}`);
          return { data, error: null };
        } catch (error) {
          return { data: 0, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    posts: {
      async getById(id: string) {
        try {
          const data = await request<PostDto>(`/api/posts/${encodeURIComponent(id)}`);
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async list(params?: { authorId?: string; communityId?: string; eventId?: string; sort?: string; size?: number }) {
        try {
          const query = new URLSearchParams();
          query.set("size", String(params?.size ?? 1000));
          if (params?.sort) query.set("sort", params.sort);
          if (params?.authorId) query.set("authorId", params.authorId);
          if (params?.communityId) query.set("communityId", params.communityId);
          if (params?.eventId) query.set("eventId", params.eventId);
          const data = await request<PostDto[]>(`/api/posts?${query.toString()}`);
          return { data, error: null };
        } catch (error) {
          return { data: [] as PostDto[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async countByAuthor(userId: string) {
        try {
          const data = await request<number>(`/api/posts/count?authorId=${encodeURIComponent(userId)}`);
          return { data, error: null };
        } catch (error) {
          return { data: 0, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    profiles: {
      async getById(id: string) {
        try {
          const dto = await request<ProfileDto>(`/api/profiles/${encodeURIComponent(id)}`);
          const data: ProfileShape = {
            id: String(dto.id),
            email: dto.user?.email ?? "",
            display_name: dto.displayName ?? "",
            username: dto.username ?? "",
            avatar_url: dto.avatarUrl ?? null,
            bio: dto.bio ?? null,
            city: dto.city ?? null,
            birth_date: dto.birthDate ?? null,
            status: dto.status ?? "",
            is_verified: dto.isVerified ?? false
          };
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async update(id: string, patch: Partial<ProfileShape>) {
        try {
          const payload: Partial<ProfileDto> = {};
          if (patch.display_name !== undefined) payload.displayName = patch.display_name;
          if (patch.username !== undefined) payload.username = patch.username;
          if (patch.avatar_url !== undefined) payload.avatarUrl = patch.avatar_url;
          if (patch.bio !== undefined) payload.bio = patch.bio;
          if (patch.city !== undefined) payload.city = patch.city;
          if (patch.birth_date !== undefined) payload.birthDate = patch.birth_date;
          const dto = await request<ProfileDto>(`/api/profiles/${encodeURIComponent(id)}`, { method: "PUT", json: payload });
          const data: ProfileShape = {
            id: String(dto.id),
            email: dto.user?.email ?? "",
            display_name: dto.displayName ?? "",
            username: dto.username ?? "",
            avatar_url: dto.avatarUrl ?? null,
            bio: dto.bio ?? null,
            city: dto.city ?? null,
            birth_date: dto.birthDate ?? null,
            status: dto.status ?? "",
            is_verified: dto.isVerified ?? false
          };
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    comments: {
      async listByTarget(targetType: string, targetId: string) {
        try {
          const dtos = await request<CommentDto[]>(`/api/comments?targetType=${encodeURIComponent(targetType.toUpperCase())}&targetId=${encodeURIComponent(targetId)}`);
          const data: CommentShape[] = (dtos ?? []).map((dto) => ({
            id: String(dto.id),
            author_id: dto.author?.id ? String(dto.author.id) : null,
            body: dto.body ?? "",
            created_at: dto.createdAt ?? new Date().toISOString(),
            profiles: dto.author?.displayName ? { display_name: dto.author.displayName } : null
          }));
          return { data, error: null };
        } catch (error) {
          return { data: [] as CommentShape[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async countByAuthor(userId: string) {
        try {
          const data = await request<number>(`/api/comments/count?authorId=${encodeURIComponent(userId)}`);
          return { data, error: null };
        } catch (error) {
          return { data: 0, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    eventRatings: {
      async listByEvent(eventId: string) {
        try {
          const dtos = await request<EventRatingDto[]>(`/api/event-ratings?eventId=${encodeURIComponent(eventId)}`);
          const data: EventRatingShape[] = (dtos ?? []).map((dto) => ({
            id: String(dto.id),
            event_id: dto.event?.id ? String(dto.event.id) : "",
            user_id: dto.user?.id ? String(dto.user.id) : "",
            rating: dto.rating ?? 0,
            review_text: dto.reviewText ?? null,
            created_at: dto.createdAt ?? new Date().toISOString()
          }));
          return { data, error: null };
        } catch (error) {
          return { data: [] as EventRatingShape[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async listByUser(userId: string) {
        try {
          const dtos = await request<EventRatingDto[]>(`/api/event-ratings?userId=${encodeURIComponent(userId)}`);
          const data: EventRatingShape[] = (dtos ?? []).map((dto) => ({
            id: String(dto.id),
            event_id: dto.event?.id ? String(dto.event.id) : "",
            user_id: dto.user?.id ? String(dto.user.id) : "",
            rating: dto.rating ?? 0,
            review_text: dto.reviewText ?? null,
            created_at: dto.createdAt ?? new Date().toISOString()
          }));
          return { data, error: null };
        } catch (error) {
          return { data: [] as EventRatingShape[], error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    accountPreferences: {
      async getByProfileId(profileId: string) {
        try {
          const dto = await request<AccountPreferencesDto>(`/api/account-preferences/by-profile/${encodeURIComponent(profileId)}`);
          const data: AccountPreferencesShape = {
            id: dto.id,
            discoverable: dto.discoverable ?? true,
            show_city: dto.showCity ?? true,
            show_follow_connections: dto.showFollowConnections ?? true,
            allow_follows: dto.allowFollows ?? true,
            require_follow_approval: dto.requireFollowApproval ?? false,
            allow_messages_from: (dto.allowMessagesFrom?.toLowerCase() ?? "following") as AccountPreferencesShape["allow_messages_from"],
            notify_events: dto.notifyEvents ?? true,
            notify_communities: dto.notifyCommunities ?? true,
            notify_social: dto.notifySocial ?? true,
            notify_advantages: dto.notifyAdvantages ?? true,
            notify_system: dto.notifySystem ?? true
          };
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      },
      async update(id: number, patch: Partial<AccountPreferencesShape>) {
        try {
          const payload: Partial<AccountPreferencesDto> = {};
          if (patch.discoverable !== undefined) payload.discoverable = patch.discoverable;
          if (patch.show_city !== undefined) payload.showCity = patch.show_city;
          if (patch.show_follow_connections !== undefined) payload.showFollowConnections = patch.show_follow_connections;
          if (patch.allow_follows !== undefined) payload.allowFollows = patch.allow_follows;
          if (patch.require_follow_approval !== undefined) payload.requireFollowApproval = patch.require_follow_approval;
          if (patch.allow_messages_from !== undefined) payload.allowMessagesFrom = patch.allow_messages_from.toUpperCase() as AccountPreferencesDto["allowMessagesFrom"];
          if (patch.notify_events !== undefined) payload.notifyEvents = patch.notify_events;
          if (patch.notify_communities !== undefined) payload.notifyCommunities = patch.notify_communities;
          if (patch.notify_social !== undefined) payload.notifySocial = patch.notify_social;
          if (patch.notify_advantages !== undefined) payload.notifyAdvantages = patch.notify_advantages;
          if (patch.notify_system !== undefined) payload.notifySystem = patch.notify_system;
          const dto = await request<AccountPreferencesDto>(`/api/account-preferences/${id}`, { method: "PUT", json: payload });
          const data: AccountPreferencesShape = {
            id: dto.id,
            discoverable: dto.discoverable ?? true,
            show_city: dto.showCity ?? true,
            show_follow_connections: dto.showFollowConnections ?? true,
            allow_follows: dto.allowFollows ?? true,
            require_follow_approval: dto.requireFollowApproval ?? false,
            allow_messages_from: (dto.allowMessagesFrom?.toLowerCase() ?? "following") as AccountPreferencesShape["allow_messages_from"],
            notify_events: dto.notifyEvents ?? true,
            notify_communities: dto.notifyCommunities ?? true,
            notify_social: dto.notifySocial ?? true,
            notify_advantages: dto.notifyAdvantages ?? true,
            notify_system: dto.notifySystem ?? true
          };
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İstek başarısız") };
        }
      }
    },
    rpc(name: string, args: Record<string, unknown> = {}) {
      let single = false;
      const pending = Promise.resolve().then(async () => {
        try {
          const result = await request<{ data: any; error?: string }>(`/api/app/rpc/${name}`, { method: "POST", json: args });
          let data = result.data;
          if (single && Array.isArray(data)) data = data[0] ?? null;
          return { data, error: result.error ? asError(result.error) : null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İşlem başarısız") };
        }
      }) as Promise<{ data: any; error: { message: string } | null }> & {
        maybeSingle: <U = any>() => Promise<{ data: U; error: { message: string } | null }>;
      };
      pending.maybeSingle = <U = any>() => {
        single = true;
        return pending as Promise<{ data: U; error: { message: string } | null }>;
      };
      return pending;
    },
    functions: {
      async invoke(name: string, init?: { body?: unknown }) {
        try {
          if (name === "delete-account") {
            await request("/api/app/me", { method: "DELETE" });
            await options.setToken(null);
            return { data: { ok: true }, error: null };
          }
          const data = await request("/api/app/ai/chat", { method: "POST", json: init?.body ?? {} });
          return { data, error: null };
        } catch (error) {
          return { data: null, error: asError(error instanceof Error ? error.message : "İşlem başarısız") };
        }
      }
    },
    storage: {
      from(bucket: string) {
        return {
          async upload(path: string, fileData: ArrayBuffer | Blob, fileOptions?: { contentType?: string }) {
            try {
              const form = new FormData();
              form.append("path", path);
              const blob = fileData instanceof Blob ? fileData : new Blob([fileData], { type: fileOptions?.contentType || "application/octet-stream" });
              form.append("file", blob, path.split("/").pop() || "file");
              const uploaded = await request<{ publicUrl: string; path: string }>(`/api/app/media/${bucket}`, {
                method: "POST",
                body: form
              });
              return { data: uploaded, error: null };
            } catch (error) {
              return { data: null, error: asError(error instanceof Error ? error.message : "Yükleme başarısız") };
            }
          },
          getPublicUrl(path: string) {
            return { data: { publicUrl: `${options.getBaseUrl()}/api/app/media/${bucket}/${path}` } };
          },
          async remove(paths: string[]) {
            try {
              await Promise.all(paths.map((path) => request(`/api/app/media/${bucket}?path=${encodeURIComponent(path)}`, { method: "DELETE" })));
              return { error: null };
            } catch (error) {
              return { error: asError(error instanceof Error ? error.message : "Silme başarısız") };
            }
          }
        };
      }
    },
    channel(_name: string) {
      let timer: ReturnType<typeof setInterval> | null = null;
      const handlers: Array<() => void> = [];
      const channel: {
        on(_event: string, _filter: unknown, callback: () => void): typeof channel;
        subscribe(): typeof channel;
        _cleanup?: () => void;
      } = {
        on(_event: string, _filter: unknown, callback: () => void) {
          handlers.push(callback);
          return channel;
        },
        subscribe() {
          timer = setInterval(() => handlers.forEach((handler) => handler()), 4000);
          return channel;
        }
      };
      channel._cleanup = () => {
        if (timer) clearInterval(timer);
      };
      return channel;
    },
    removeChannel(channel: { _cleanup?: () => void }) {
      channel._cleanup?.();
      return Promise.resolve();
    },
    auth: {
      async getSession() {
        return { data: { session: await currentSession() }, error: null };
      },
      async getUser() {
        const session = await currentSession();
        return { data: { user: session?.user ?? null }, error: null };
      },
      onAuthStateChange(callback: (event: string, session: AuthSession) => void) {
        authListeners.add(callback);
        return {
          data: {
            subscription: {
              unsubscribe() {
                authListeners.delete(callback);
              }
            }
          }
        };
      },
      async signInWithPassword({ email, password }: { email: string; password: string }) {
        try {
          const result = await request<{ id_token: string }>("/api/authenticate", {
            method: "POST",
            auth: false,
            json: { username: email.trim(), password, rememberMe: true }
          });
          await options.setToken(result.id_token);
          const session = await currentSession();
          authListeners.forEach((listener) => listener("SIGNED_IN", session));
          return { data: { session }, error: null };
        } catch (error) {
          return { data: { session: null }, error: asError(error instanceof Error ? error.message : "Giriş başarısız") };
        }
      },
      async signUp({
        email,
        password,
        options: signUpOptions
      }: {
        email: string;
        password: string;
        options?: { data?: { display_name?: string; username?: string } };
      }) {
        try {
          await request("/api/register", {
            method: "POST",
            auth: false,
            json: {
              login: signUpOptions?.data?.username || email.split("@")[0],
              email,
              password,
              firstName: signUpOptions?.data?.display_name || signUpOptions?.data?.username,
              langKey: "tr"
            }
          });
          return { data: { session: null }, error: null };
        } catch (error) {
          return { data: { session: null }, error: asError(error instanceof Error ? error.message : "Kayıt başarısız") };
        }
      },
      async signOut() {
        await options.setToken(null);
        authListeners.forEach((listener) => listener("SIGNED_OUT", null));
        return { error: null };
      },
      async resetPasswordForEmail(email: string) {
        try {
          await request("/api/account/reset-password/init", { method: "POST", auth: false, json: email });
          return { error: null };
        } catch (error) {
          return { error: asError(error instanceof Error ? error.message : "Şifre sıfırlama başarısız") };
        }
      },
      async resend() {
        return { error: asError("E-posta tekrar gönderimi bu sürümde e-posta sunucusu üzerinden yapılır.") };
      },
      startAutoRefresh() {},
      stopAutoRefresh() {},
      async setSession() {
        return { data: { session: await currentSession() }, error: null };
      },
      async updateUser({ password, key }: { password: string; key?: string | null }) {
        const resolvedKey =
          key ||
          (typeof window === "undefined"
            ? null
            : new URLSearchParams(window.location.search).get("key") ||
              new URLSearchParams(window.location.hash.replace(/^#/, "")).get("key"));
        if (!resolvedKey) {
          return { error: asError("Şifre yenileme anahtarı eksik.") };
        }
        try {
          await request("/api/account/reset-password/finish", {
            method: "POST",
            auth: false,
            json: { key: resolvedKey, newPassword: password, confirmPassword: password }
          });
          return { error: null };
        } catch (error) {
          return { error: asError(error instanceof Error ? error.message : "Şifre güncellenemedi") };
        }
      }
    }
  };
}
