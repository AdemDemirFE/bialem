import { createHash } from "node:crypto";
import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { createClient } from "@supabase/supabase-js";

const backupDirectory = process.argv[2];

if (!backupDirectory) {
  throw new Error("Kullanim: node scripts/backup-supabase-storage.mjs <backup-directory>");
}

const env = await loadEnvFile(path.resolve("admin/.env.local"));
const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL ?? env.NEXT_PUBLIC_SUPABASE_URL;
const serviceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY ?? env.SUPABASE_SERVICE_ROLE_KEY;

if (!supabaseUrl || !serviceRoleKey) {
  throw new Error("NEXT_PUBLIC_SUPABASE_URL ve SUPABASE_SERVICE_ROLE_KEY bulunamadi.");
}

const outputRoot = path.resolve(backupDirectory, "storage");
await mkdir(outputRoot, { recursive: true });

const client = createClient(supabaseUrl, serviceRoleKey, {
  auth: { autoRefreshToken: false, persistSession: false },
});

const { data: buckets, error: bucketError } = await client.storage.listBuckets();
if (bucketError) throw bucketError;

const manifest = {
  created_at: new Date().toISOString(),
  buckets: [],
};

for (const bucket of buckets ?? []) {
  const bucketRecord = {
    id: bucket.id,
    name: bucket.name,
    public: bucket.public,
    file_size_limit: bucket.file_size_limit,
    allowed_mime_types: bucket.allowed_mime_types,
    files: [],
  };

  const files = await listFilesRecursively(client, bucket.id);
  console.log(`${bucket.id}: ${files.length} dosya yedekleniyor...`);

  for (const objectPath of files) {
    const { data, error } = await client.storage.from(bucket.id).download(objectPath);
    if (error) throw new Error(`${bucket.id}/${objectPath}: ${error.message}`);

    const buffer = Buffer.from(await data.arrayBuffer());
    const destination = safeStoragePath(outputRoot, bucket.id, objectPath);
    await mkdir(path.dirname(destination), { recursive: true });
    await writeFile(destination, buffer);

    bucketRecord.files.push({
      path: objectPath,
      bytes: buffer.length,
      sha256: createHash("sha256").update(buffer).digest("hex"),
    });
  }

  manifest.buckets.push(bucketRecord);
}

await writeFile(
  path.resolve(backupDirectory, "storage-manifest.json"),
  `${JSON.stringify(manifest, null, 2)}\n`,
  "utf8",
);

const fileCount = manifest.buckets.reduce((total, bucket) => total + bucket.files.length, 0);
console.log(`Storage yedegi tamamlandi: ${manifest.buckets.length} bucket, ${fileCount} dosya.`);

async function listFilesRecursively(storageClient, bucketId, prefix = "") {
  const files = [];
  let offset = 0;

  while (true) {
    const { data, error } = await storageClient.storage.from(bucketId).list(prefix, {
      limit: 1000,
      offset,
      sortBy: { column: "name", order: "asc" },
    });
    if (error) throw new Error(`${bucketId}/${prefix}: ${error.message}`);

    const entries = data ?? [];
    for (const entry of entries) {
      const objectPath = prefix ? `${prefix}/${entry.name}` : entry.name;
      if (entry.id === null) {
        files.push(...(await listFilesRecursively(storageClient, bucketId, objectPath)));
      } else {
        files.push(objectPath);
      }
    }

    if (entries.length < 1000) break;
    offset += entries.length;
  }

  return files;
}

function safeStoragePath(root, bucketId, objectPath) {
  const destination = path.resolve(root, bucketId, ...objectPath.split("/"));
  const expectedRoot = `${path.resolve(root)}${path.sep}`;
  if (!destination.startsWith(expectedRoot)) {
    throw new Error(`Guvenli olmayan Storage yolu reddedildi: ${bucketId}/${objectPath}`);
  }
  return destination;
}

async function loadEnvFile(filePath) {
  const values = {};
  const content = await readFile(filePath, "utf8");
  for (const line of content.split(/\r?\n/)) {
    const match = line.match(/^\s*([^#=\s]+)\s*=\s*(.*)\s*$/);
    if (!match) continue;
    values[match[1]] = match[2].replace(/^['"]|['"]$/g, "");
  }
  return values;
}
