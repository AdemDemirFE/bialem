import { createCipheriv, randomBytes, scryptSync } from "node:crypto";
import { createReadStream, createWriteStream } from "node:fs";
import { rename, rm } from "node:fs/promises";
import { finished, pipeline } from "node:stream/promises";
import process from "node:process";

const [inputPath, outputPath] = process.argv.slice(2);
const password = process.env.BACKUP_ENCRYPTION_PASSWORD;
const magic = Buffer.from("BDNYBKP1", "ascii");

if (!inputPath || !outputPath) {
  throw new Error("Kullanim: node scripts/encrypt-backup.mjs <input> <output>");
}
if (!password || password.length < 20) {
  throw new Error("BACKUP_ENCRYPTION_PASSWORD en az 20 karakter olmali.");
}

const salt = randomBytes(16);
const iv = randomBytes(12);
const key = scryptSync(password, salt, 32);
const cipher = createCipheriv("aes-256-gcm", key, iv);
const temporaryPath = `${outputPath}.partial`;
const output = createWriteStream(temporaryPath, { flags: "wx" });

output.write(Buffer.concat([magic, salt, iv]));

try {
  await pipeline(createReadStream(inputPath), cipher, output, { end: false });
  output.end(cipher.getAuthTag());
  await finished(output);
  await rename(temporaryPath, outputPath);
} catch (error) {
  output.destroy();
  await rm(temporaryPath, { force: true });
  throw error;
}

console.log(`Sifreli yedek olusturuldu: ${outputPath}`);
