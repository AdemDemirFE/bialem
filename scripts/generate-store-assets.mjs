import fs from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import sharp from "sharp";

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const rootDirectory = path.resolve(scriptDirectory, "..");
const assetsDirectory = path.join(rootDirectory, "store-assets");
const sourceImage = path.join(rootDirectory, "mobile", "assets", "onboarding-worlds.png");
const sourceIcon = path.join(rootDirectory, "mobile", "assets", "app-icon.png");

await fs.mkdir(assetsDirectory, { recursive: true });

const iconBuffer = await sharp(sourceIcon)
  .resize(512, 512, { fit: "cover" })
  .png({ compressionLevel: 9, palette: true, quality: 90 })
  .toBuffer();

await fs.writeFile(path.join(assetsDirectory, "google-play-icon-512.png"), iconBuffer);

const background = await sharp(sourceImage)
  .resize(1024, 500, { fit: "cover", position: "attention" })
  .modulate({ brightness: 0.88, saturation: 1.08 })
  .png()
  .toBuffer();

const featureIcon = await sharp(sourceIcon)
  .resize(86, 86, { fit: "cover" })
  .composite([
    {
      input: Buffer.from(
        '<svg width="86" height="86"><rect width="86" height="86" rx="22" fill="white"/></svg>'
      ),
      blend: "dest-in"
    }
  ])
  .png()
  .toBuffer();

const overlay = Buffer.from(`
  <svg width="1024" height="500" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="shade" x1="0" y1="0" x2="1" y2="0">
        <stop offset="0" stop-color="#071b4b" stop-opacity="0.98"/>
        <stop offset="0.48" stop-color="#071b4b" stop-opacity="0.90"/>
        <stop offset="0.73" stop-color="#071b4b" stop-opacity="0.15"/>
        <stop offset="1" stop-color="#071b4b" stop-opacity="0"/>
      </linearGradient>
      <linearGradient id="accent" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0" stop-color="#ffac12"/>
        <stop offset="1" stop-color="#ff6b00"/>
      </linearGradient>
    </defs>
    <rect width="1024" height="500" fill="url(#shade)"/>
    <circle cx="510" cy="-110" r="250" fill="#7b35ff" opacity="0.14"/>
    <circle cx="420" cy="560" r="240" fill="#00c8ef" opacity="0.12"/>
    <rect x="68" y="174" width="88" height="7" rx="3.5" fill="url(#accent)"/>
    <text x="68" y="238" fill="#ffffff" font-family="Trebuchet MS, sans-serif" font-size="54" font-weight="700">
      \u015eehrin ritmine kat\u0131l.
    </text>
    <text x="68" y="292" fill="#dce7ff" font-family="Trebuchet MS, sans-serif" font-size="27" font-weight="400">
      Topluluklar\u0131 ve etkinlikleri ke\u015ffet,
    </text>
    <text x="68" y="329" fill="#dce7ff" font-family="Trebuchet MS, sans-serif" font-size="27" font-weight="400">
      yeni insanlarla bir araya gel.
    </text>
    <text x="174" y="118" fill="#ffffff" font-family="Trebuchet MS, sans-serif" font-size="36" font-weight="700">
      B\u0130D\u00dcNYA
    </text>
    <text x="68" y="420" fill="#ffb21a" font-family="Trebuchet MS, sans-serif" font-size="19" font-weight="700" letter-spacing="3">
      ANKARA'DAN BA\u015eLAYAN SOSYAL KE\u015e\u0130F
    </text>
  </svg>
`);

await sharp(background)
  .composite([
    { input: overlay, left: 0, top: 0 },
    { input: featureIcon, left: 68, top: 55 }
  ])
  .flatten({ background: "#071b4b" })
  .removeAlpha()
  .png({ compressionLevel: 9 })
  .toFile(path.join(assetsDirectory, "google-play-feature-graphic-1024x500.png"));

const outputs = await Promise.all(
  ["google-play-icon-512.png", "google-play-feature-graphic-1024x500.png"].map(async (name) => {
    const filePath = path.join(assetsDirectory, name);
    const metadata = await sharp(filePath).metadata();
    const stats = await fs.stat(filePath);
    return {
      name,
      width: metadata.width,
      height: metadata.height,
      bytes: stats.size,
      hasAlpha: metadata.hasAlpha
    };
  })
);

console.log(JSON.stringify(outputs, null, 2));
