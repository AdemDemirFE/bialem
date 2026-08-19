import { createHash } from "node:crypto";
import { readFile } from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { createClient } from "@supabase/supabase-js";

const backupDirectory = path.resolve(process.argv[2] ?? "backups/20260802-165133");
const targetProjectRef = process.env.TARGET_SUPABASE_PROJECT_REF;
const targetUrl = process.env.TARGET_SUPABASE_URL;
const serviceRoleKey = process.env.TARGET_SUPABASE_SERVICE_ROLE_KEY;
const productionProjectRef = "tvaatpmlqlcnyjsvzlcy";

if (!targetProjectRef || !targetUrl || !serviceRoleKey) {
  throw new Error("Staging Supabase hedef bilgileri bulunamadi.");
}

if (targetProjectRef === productionProjectRef || targetUrl.includes(productionProjectRef)) {
  throw new Error("Guvenlik engeli: production Storage hedefine restore yapilamaz.");
}

const expectedUrl = `https://${targetProjectRef}.supabase.co`;
if (targetUrl !== expectedUrl) {
  throw new Error(`Guvenlik engeli: hedef URL ${expectedUrl} ile eslesmiyor.`);
}

const manifest = JSON.parse(
  await readFile(path.join(backupDirectory, "storage-manifest.json"), "utf8"),
);
const storageRoot = path.join(backupDirectory, "storage");
const client = createClient(targetUrl, serviceRoleKey, {
  auth: { autoRefreshToken: false, persistSession: false },
});

let verifiedFiles = 0;

for (const bucket of manifest.buckets) {
  const { data: existingBucket, error: bucketReadError } = await client.storage.getBucket(bucket.id);

  if (bucketReadError && bucketReadError.statusCode !== "404") {
    throw new Error(`${bucket.id}: bucket okunamadi: ${bucketReadError.message}`);
  }

  const bucketOptions = {
    public: bucket.public,
    fileSizeLimit: bucket.file_size_limit,
    allowedMimeTypes: bucket.allowed_mime_types,
  };

  if (existingBucket) {
    const { error } = await client.storage.updateBucket(bucket.id, bucketOptions);
    if (error) throw new Error(`${bucket.id}: bucket guncellenemedi: ${error.message}`);
  } else {
    const { error } = await client.storage.createBucket(bucket.id, bucketOptions);
    if (error) throw new Error(`${bucket.id}: bucket olusturulamadi: ${error.message}`);
  }

  for (const file of bucket.files) {
    const source = safeStoragePath(storageRoot, bucket.id, file.path);
    const buffer = await readFile(source);
    verifyBuffer(buffer, file, `${bucket.id}/${file.path} yerel`);

    const { error: uploadError } = await client.storage.from(bucket.id).upload(file.path, buffer, {
      contentType: detectMimeType(buffer),
      upsert: true,
    });
    if (uploadError) {
      throw new Error(`${bucket.id}/${file.path}: yuklenemedi: ${uploadError.message}`);
    }

    const { data: downloaded, error: downloadError } = await client.storage
      .from(bucket.id)
      .download(file.path);
    if (downloadError) {
      throw new Error(`${bucket.id}/${file.path}: dogrulama indirmesi basarisiz: ${downloadError.message}`);
    }

    const restoredBuffer = Buffer.from(await downloaded.arrayBuffer());
    verifyBuffer(restoredBuffer, file, `${bucket.id}/${file.path} staging`);
    verifiedFiles += 1;
    console.log(`Dogrulandi: ${bucket.id}/${file.path}`);
  }
}

console.log(`Storage restore tamamlandi: ${manifest.buckets.length} bucket, ${verifiedFiles} dosya.`);

function verifyBuffer(buffer, file, label) {
  const hash = createHash("sha256").update(buffer).digest("hex");
  if (buffer.length !== file.bytes || hash !== file.sha256) {
    throw new Error(`${label}: boyut veya SHA-256 eslesmedi.`);
  }
}

function safeStoragePath(root, bucketId, objectPath) {
  const destination = path.resolve(root, bucketId, ...objectPath.split("/"));
  const expectedRoot = `${path.resolve(root)}${path.sep}`;
  if (!destination.startsWith(expectedRoot)) {
    throw new Error(`Guvenli olmayan Storage yolu reddedildi: ${bucketId}/${objectPath}`);
  }
  return destination;
}

function detectMimeType(buffer) {
  if (buffer.subarray(0, 3).equals(Buffer.from([0xff, 0xd8, 0xff]))) return "image/jpeg";
  if (buffer.subarray(0, 8).equals(Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]))) {
    return "image/png";
  }
  if (buffer.subarray(0, 4).toString("ascii") === "RIFF" && buffer.subarray(8, 12).toString("ascii") === "WEBP") {
    return "image/webp";
  }
  return "application/octet-stream";
}
