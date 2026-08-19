import fs from "node:fs";
import path from "node:path";
import { spawnSync } from "node:child_process";
import sharp from "sharp";
import ffmpegPath from "ffmpeg-static";

const root = process.cwd();
const outDir = path.join(root, "output", "reels");
const hero = path.join(outDir, "bialem-cosmic-community.png");
const logo = path.join(root, "mobile", "assets", "app-icon.png");
const width = 1080;
const height = 1920;
const fps = 30;
const duration = 15;

fs.mkdirSync(outDir, { recursive: true });

const escapeXml = (value) => value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

function textOverlay(name, eyebrow, lines, accent = "#f6a51c") {
  const lineMarkup = lines.map((line, index) =>
    `<text x="540" y="${270 + index * 108}" text-anchor="middle" fill="#ffffff" font-family="Segoe UI, Arial" font-size="88" font-weight="800" letter-spacing="-2">${escapeXml(line)}</text>`
  ).join("\n");
  const svg = `
  <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <filter id="shadow"><feDropShadow dx="0" dy="10" stdDeviation="12" flood-color="#081326" flood-opacity="0.75"/></filter>
      <linearGradient id="scrim" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0" stop-color="#07132e" stop-opacity="0.93"/>
        <stop offset="0.72" stop-color="#07132e" stop-opacity="0.25"/>
        <stop offset="1" stop-color="#07132e" stop-opacity="0"/>
      </linearGradient>
    </defs>
    <rect width="1080" height="680" fill="url(#scrim)"/>
    <g filter="url(#shadow)">
      <rect x="365" y="104" width="350" height="72" rx="36" fill="${accent}"/>
      <text x="540" y="153" text-anchor="middle" fill="#0a1833" font-family="Segoe UI, Arial" font-size="34" font-weight="800" letter-spacing="3">${escapeXml(eyebrow)}</text>
      ${lineMarkup}
    </g>
  </svg>`;
  return sharp(Buffer.from(svg)).png().toFile(path.join(outDir, name));
}

async function endCard() {
  const logoPng = await sharp(logo).resize(520, 520).png().toBuffer();
  const svg = Buffer.from(`
    <svg width="1080" height="1920" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <radialGradient id="g" cx="50%" cy="34%" r="82%">
          <stop offset="0" stop-color="#7047d7"/>
          <stop offset="0.48" stop-color="#172c68"/>
          <stop offset="1" stop-color="#07132e"/>
        </radialGradient>
        <filter id="glow"><feGaussianBlur stdDeviation="35"/></filter>
      </defs>
      <rect width="1080" height="1920" fill="url(#g)"/>
      <circle cx="190" cy="330" r="170" fill="#1699b8" opacity="0.32" filter="url(#glow)"/>
      <circle cx="920" cy="690" r="210" fill="#f6a51c" opacity="0.24" filter="url(#glow)"/>
      <circle cx="80" cy="1170" r="9" fill="#fff"/><circle cx="940" cy="210" r="7" fill="#fff"/>
      <circle cx="840" cy="1280" r="5" fill="#fff"/><circle cx="250" cy="1450" r="6" fill="#fff"/>
      <text x="540" y="1145" text-anchor="middle" fill="#ffffff" font-family="Segoe UI, Arial" font-size="108" font-weight="900" letter-spacing="-3">Bi Alem</text>
      <text x="540" y="1230" text-anchor="middle" fill="#c3cee3" font-family="Segoe UI, Arial" font-size="46" font-weight="600">Senin dünyan. Senin topluluğun.</text>
      <rect x="285" y="1325" width="510" height="116" rx="58" fill="#f6a51c"/>
      <text x="540" y="1400" text-anchor="middle" fill="#0a1833" font-family="Segoe UI, Arial" font-size="42" font-weight="900" letter-spacing="2">ŞİMDİ KEŞFET</text>
      <text x="540" y="1535" text-anchor="middle" fill="#ffffff" font-family="Segoe UI, Arial" font-size="32" font-weight="600" opacity="0.88">Topluluklar • Etkinlikler • Yeni insanlar</text>
    </svg>`);
  await sharp({ create: { width, height, channels: 4, background: "#07132e" } })
    .composite([{ input: svg }, { input: logoPng, left: 280, top: 490 }])
    .png()
    .toFile(path.join(outDir, "end-card.png"));
}

function makeMusic(file) {
  const rate = 44100;
  const samples = rate * duration;
  const channels = 2;
  const pcm = Buffer.alloc(samples * channels * 2);
  const progression = [261.63, 329.63, 392.0, 293.66, 369.99, 440.0, 220.0, 329.63];
  for (let i = 0; i < samples; i++) {
    const t = i / rate;
    const beat = t % 0.5;
    const note = progression[Math.floor(t / 0.5) % progression.length];
    const pluckEnv = Math.exp(-5.2 * beat);
    const pluck = Math.sin(2 * Math.PI * note * t) * pluckEnv * 0.18;
    const shimmer = Math.sin(2 * Math.PI * note * 2 * t) * pluckEnv * 0.055;
    const kickT = t % 0.5;
    const kick = Math.sin(2 * Math.PI * (72 - 28 * kickT) * kickT) * Math.exp(-20 * kickT) * 0.34;
    const clapT = (t + 0.25) % 0.5;
    const noise = (Math.sin(i * 12.9898) * 43758.5453) % 1;
    const clap = noise * Math.exp(-40 * clapT) * 0.07;
    const fade = Math.min(1, t / 0.35, (duration - t) / 0.65);
    const sample = Math.max(-1, Math.min(1, (pluck + shimmer + kick + clap) * fade));
    const value = Math.round(sample * 32767);
    pcm.writeInt16LE(value, i * 4);
    pcm.writeInt16LE(value, i * 4 + 2);
  }
  const header = Buffer.alloc(44);
  header.write("RIFF", 0); header.writeUInt32LE(36 + pcm.length, 4); header.write("WAVE", 8);
  header.write("fmt ", 12); header.writeUInt32LE(16, 16); header.writeUInt16LE(1, 20);
  header.writeUInt16LE(channels, 22); header.writeUInt32LE(rate, 24);
  header.writeUInt32LE(rate * channels * 2, 28); header.writeUInt16LE(channels * 2, 32);
  header.writeUInt16LE(16, 34); header.write("data", 36); header.writeUInt32LE(pcm.length, 40);
  fs.writeFileSync(file, Buffer.concat([header, pcm]));
}

if (!fs.existsSync(hero)) throw new Error(`Hero artwork missing: ${hero}`);

await Promise.all([
  textOverlay("copy-1.png", "BİR DÜNYA DÜŞÜN", ["Herkesin", "bir yeri var."]),
  textOverlay("copy-2.png", "EVET, HERKESİN", ["Uzaylı da. Kedi de.", "Flamingo da."], "#55c7da"),
  textOverlay("copy-3.png", "KENDİ DÜNYANI BUL", ["Topluluğunu keşfet.", "Etkinliğe katıl."], "#ffb12b"),
  textOverlay("copy-4.png", "AYNI DÜNYADA", ["Yeni insanlarla", "gerçekten buluş."], "#a98bea"),
  endCard()
]);

const music = path.join(outDir, "bialem-jingle.wav");
makeMusic(music);

const output = path.join(outDir, "bialem-instagram-reels.mp4");
const filter = [
  `[0:v]scale=1200:2133,crop=1080:1920:x='60+28*sin(t*0.70)':y='106+22*cos(t*0.55)',eq=brightness=-0.035:saturation=1.08,fps=${fps}[base]`,
  `[1:v]format=rgba,fade=t=in:st=0:d=0.35:alpha=1,fade=t=out:st=2.25:d=0.35:alpha=1[c1]`,
  `[2:v]format=rgba,fade=t=in:st=2.55:d=0.35:alpha=1,fade=t=out:st=4.95:d=0.35:alpha=1[c2]`,
  `[3:v]format=rgba,fade=t=in:st=5.25:d=0.35:alpha=1,fade=t=out:st=7.95:d=0.35:alpha=1[c3]`,
  `[4:v]format=rgba,fade=t=in:st=8.25:d=0.35:alpha=1,fade=t=out:st=10.95:d=0.35:alpha=1[c4]`,
  `[5:v]format=rgba,fade=t=in:st=11.15:d=0.55:alpha=1[end]`,
  `[base][c1]overlay=0:0[tmp1]`,
  `[tmp1][c2]overlay=0:0[tmp2]`,
  `[tmp2][c3]overlay=0:0[tmp3]`,
  `[tmp3][c4]overlay=0:0[tmp4]`,
  `[tmp4][end]overlay=0:0,format=yuv420p[v]`
].join(";");

const args = [
  "-y", "-loop", "1", "-i", hero,
  "-loop", "1", "-i", path.join(outDir, "copy-1.png"),
  "-loop", "1", "-i", path.join(outDir, "copy-2.png"),
  "-loop", "1", "-i", path.join(outDir, "copy-3.png"),
  "-loop", "1", "-i", path.join(outDir, "copy-4.png"),
  "-loop", "1", "-i", path.join(outDir, "end-card.png"),
  "-i", music,
  "-filter_complex", filter,
  "-map", "[v]", "-map", "6:a", "-t", String(duration),
  "-c:v", "libx264", "-preset", "medium", "-crf", "19", "-r", String(fps),
  "-c:a", "aac", "-b:a", "192k", "-movflags", "+faststart", output
];
const result = spawnSync(ffmpegPath, args, { stdio: "inherit" });
if (result.status !== 0) process.exit(result.status ?? 1);
console.log(output);
