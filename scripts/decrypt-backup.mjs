import { createDecipheriv, scryptSync } from "node:crypto";
import { createReadStream, createWriteStream } from "node:fs";
import { open, rename, rm, stat } from "node:fs/promises";
import { pipeline } from "node:stream/promises";
import process from "node:process";

const [inputPath, outputPath] = process.argv.slice(2);
const password = process.env.BACKUP_ENCRYPTION_PASSWORD;
const magic = Buffer.from("BDNYBKP1", "ascii");
const headerLength = magic.length + 16 + 12;
const tagLength = 16;

if (!inputPath || !outputPath) {
  throw new Error("Kullanim: node scripts/decrypt-backup.mjs <input> <output>");
}
if (!password) throw new Error("BACKUP_ENCRYPTION_PASSWORD tanimli degil.");

const inputStat = await stat(inputPath);
if (inputStat.size <= headerLength + tagLength) throw new Error("Sifreli yedek gecersiz veya eksik.");

const handle = await open(inputPath, "r");
const header = Buffer.alloc(headerLength);
const tag = Buffer.alloc(tagLength);
await handle.read(header, 0, header.length, 0);
await handle.read(tag, 0, tag.length, inputStat.size - tagLength);
await handle.close();

if (!header.subarray(0, magic.length).equals(magic)) throw new Error("Yedek formati taninmadi.");

const salt = header.subarray(magic.length, magic.length + 16);
const iv = header.subarray(magic.length + 16, headerLength);
const key = scryptSync(password, salt, 32);
const decipher = createDecipheriv("aes-256-gcm", key, iv);
decipher.setAuthTag(tag);

const temporaryPath = `${outputPath}.partial`;
try {
  await pipeline(
    createReadStream(inputPath, { start: headerLength, end: inputStat.size - tagLength - 1 }),
    decipher,
    createWriteStream(temporaryPath, { flags: "wx" }),
  );
  await rename(temporaryPath, outputPath);
} catch (error) {
  await rm(temporaryPath, { force: true });
  throw error;
}

console.log(`Yedek sifresi dogrulandi: ${outputPath}`);
