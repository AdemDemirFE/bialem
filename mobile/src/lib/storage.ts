import { Platform } from "react-native";
import { api } from "./api";

export type PickedImage = {
  uri: string;
  fileName: string;
  mimeType: string;
  bytes?: ArrayBuffer;
};

const COVER_MIME_TYPES = new Set(["image/jpeg", "image/png", "image/webp"]);
const MAX_COVER_SIZE = 5 * 1024 * 1024;

function sanitizeFileName(fileName: string) {
  return fileName.replace(/[^a-zA-Z0-9._-]/g, "_");
}

export async function requestMediaLibraryPermission() {
  return true;
}

export async function requestCameraPermission() {
  return { granted: Platform.OS === "web", canAskAgain: true };
}

export async function takePhotoWithCamera(): Promise<PickedImage | null> {
  return pickImageFromLibrary();
}

export async function pickImageFromLibrary(_options?: { square?: boolean }): Promise<PickedImage | null> {
  if (typeof document === "undefined") return null;
  return new Promise((resolve) => {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "image/*";
    input.onchange = async () => {
      const file = input.files?.[0];
      if (!file) {
        resolve(null);
        return;
      }
      resolve({
        uri: URL.createObjectURL(file),
        fileName: sanitizeFileName(file.name || `image-${Date.now()}.jpg`),
        mimeType: file.type || "image/jpeg",
        bytes: await file.arrayBuffer()
      });
    };
    input.click();
  });
}

async function uriToArrayBuffer(uri: string) {
  const response = await fetch(uri);
  return response.arrayBuffer();
}

async function uploadCoverImage(params: { bucket: string; userId: string; image: PickedImage; prefix: string }) {
  if (!COVER_MIME_TYPES.has(params.image.mimeType)) {
    throw new Error("Kapak görseli JPEG, PNG veya WebP olmalıdır.");
  }
  const fileData = params.image.bytes ?? (await uriToArrayBuffer(params.image.uri));
  if (fileData.byteLength > MAX_COVER_SIZE) {
    throw new Error("Kapak görseli en fazla 5 MB olabilir.");
  }
  const extension = params.image.mimeType === "image/png" ? "png" : params.image.mimeType === "image/webp" ? "webp" : "jpg";
  const path = `${params.userId}/${params.prefix}-${Date.now()}.${extension}`;
  const { error: uploadError } = await api.storage.from(params.bucket).upload(path, fileData, {
    contentType: params.image.mimeType
  });
  if (uploadError) throw uploadError;
  const { data } = api.storage.from(params.bucket).getPublicUrl(path);
  return { storagePath: data.publicUrl, bucketPath: path };
}

export function uploadEventCover(params: { userId: string; image: PickedImage }) {
  return uploadCoverImage({ bucket: "event-covers", userId: params.userId, image: params.image, prefix: "event" });
}

export function uploadCommunityCover(params: { userId: string; image: PickedImage }) {
  return uploadCoverImage({ bucket: "community-covers", userId: params.userId, image: params.image, prefix: "group" });
}

export async function removeUploadedImage(bucket: "event-covers" | "community-covers", path: string) {
  const { error } = await api.storage.from(bucket).remove([path]);
  if (error) throw error;
}

export async function uploadPostImage(params: { userId: string; postId: string; image: PickedImage }) {
  const extension = params.image.fileName.split(".").pop() || "jpg";
  const path = `${params.userId}/${params.postId}-${Date.now()}.${extension}`;
  const fileData = params.image.bytes ?? (await uriToArrayBuffer(params.image.uri));
  const { error: uploadError } = await api.storage.from("post-media").upload(path, fileData, { contentType: params.image.mimeType });
  if (uploadError) throw uploadError;
  const { data } = api.storage.from("post-media").getPublicUrl(path);
  return { storagePath: data.publicUrl, bucketPath: path };
}

export async function uploadStoryImage(params: { userId: string; storyId: string; image: PickedImage }) {
  const extension = params.image.fileName.split(".").pop() || "jpg";
  const path = `${params.userId}/${params.storyId}-${Date.now()}.${extension}`;
  const fileData = params.image.bytes ?? (await uriToArrayBuffer(params.image.uri));
  const { error: uploadError } = await api.storage.from("stories").upload(path, fileData, { contentType: params.image.mimeType });
  if (uploadError) throw uploadError;
  const { data } = api.storage.from("stories").getPublicUrl(path);
  return { storagePath: data.publicUrl, bucketPath: path };
}

export async function removeStoryImage(publicUrl: string) {
  const marker = "/api/app/media/stories/";
  const path = publicUrl.split(marker)[1]?.split("?")[0];
  if (!path) return;
  await api.storage.from("stories").remove([decodeURIComponent(path)]);
}

export async function uploadProfileAvatar(params: { userId: string; image: PickedImage }) {
  const path = `${params.userId}/avatar`;
  const fileData = params.image.bytes ?? (await uriToArrayBuffer(params.image.uri));
  const { error: uploadError } = await api.storage.from("profile-avatars").upload(path, fileData, { contentType: params.image.mimeType });
  if (uploadError) throw uploadError;
  const { data } = api.storage.from("profile-avatars").getPublicUrl(path);
  return `${data.publicUrl}?v=${Date.now()}`;
}
