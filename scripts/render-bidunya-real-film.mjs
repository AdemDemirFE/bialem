import fs from "node:fs";
import path from "node:path";
import { spawnSync } from "node:child_process";
import sharp from "sharp";
import ffmpegPath from "ffmpeg-static";

const root = process.cwd();
const project = path.join(root, "output", "bialem-real-film");
const media = path.join(project, "media");
const work = path.join(project, "work");
const width = 1080;
const height = 1920;
const fps = 30;
const transition = 0.35;
fs.mkdirSync(work, { recursive: true });

const clips = [
  { type: "black", duration: 1.5 },
  { file: "wake-phone.mp4", start: 0, duration: 5.0 },
  { file: "coffee-phone.mp4", start: 0, duration: 3.0 },
  { file: "subway-phones.mp4", start: 0, duration: 3.0 },
  { file: "office-laptop.mp4", start: 0, duration: 2.5 },
  { file: "office-meeting.mp4", start: 0, duration: 2.5 },
  { file: "istanbul-square.mp4", start: 0, duration: 3.0 },
  { file: "cafe-phone.mp4", start: 0, duration: 4.0 },
  { file: "coffee-phone.mp4", start: 3, duration: 4.0 },
  { file: "subway-phones.mp4", start: 2, duration: 5.0 },
  { file: "istanbul-square.mp4", start: 3, duration: 5.0 },
  { file: "birthday-alone.mp4", start: 0, duration: 5.5 },
  { file: "office-laptop.mp4", start: 1, duration: 3.5 },
  { file: "office-meeting.mp4", start: 1, duration: 3.5 },
  { file: "cafe-phone.mp4", start: 4, duration: 4.0 },
  { file: "birthday-alone.mp4", start: 3, duration: 4.0 },
  { type: "end", duration: 6.5 }
];

const totalDuration = clips.reduce((sum, clip) => sum + clip.duration, 0) - transition * (clips.length - 1);

async function createCards() {
  const blackSvg = Buffer.from(`<svg width="1080" height="1920" xmlns="http://www.w3.org/2000/svg"><rect width="1080" height="1920" fill="#03060d"/></svg>`);
  await sharp(blackSvg).png().toFile(path.join(work, "black.png"));

  const logo = await sharp(path.join(root, "mobile", "assets", "app-icon.png")).resize(460, 460).png().toBuffer();
  const endSvg = Buffer.from(`
  <svg width="1080" height="1920" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <radialGradient id="bg" cx="48%" cy="38%" r="80%"><stop offset="0" stop-color="#182d68"/><stop offset="1" stop-color="#07132e"/></radialGradient>
      <filter id="shadow"><feDropShadow dx="0" dy="16" stdDeviation="20" flood-color="#020711" flood-opacity="0.65"/></filter>
    </defs>
    <rect width="1080" height="1920" fill="url(#bg)"/>
    <circle cx="145" cy="300" r="5" fill="#f4f1ea"/><circle cx="920" cy="460" r="7" fill="#f4f1ea"/><circle cx="850" cy="1280" r="4" fill="#f4f1ea"/>
    <g filter="url(#shadow)">
      <text x="540" y="1130" text-anchor="middle" fill="#f4f1ea" font-family="Arial" font-size="90" font-weight="900">Bi’Dünya</text>
      <text x="540" y="1245" text-anchor="middle" fill="#f4f1ea" font-family="Segoe UI" font-size="52" font-weight="350">Birbirimizi bulmanın zamanı.</text>
      <rect x="278" y="1350" width="524" height="110" rx="55" fill="#f6a51c"/>
      <text x="540" y="1421" text-anchor="middle" fill="#07132e" font-family="Segoe UI" font-size="39" font-weight="800" letter-spacing="2">DÜNYANI KEŞFET</text>
    </g>
  </svg>`);
  await sharp({ create: { width, height, channels: 4, background: "#07132e" } })
    .composite([{ input: endSvg }, { input: logo, left: 310, top: 500 }])
    .png()
    .toFile(path.join(work, "end-card.png"));
}

function createAtmosphere(output) {
  const rate = 44100;
  const samples = Math.ceil(rate * totalDuration);
  const pcm = Buffer.alloc(samples * 4);
  const notes = [110, 130.81, 146.83, 98];
  for (let i = 0; i < samples; i++) {
    const t = i / rate;
    const base = notes[Math.floor(t / 8) % notes.length];
    const drone = Math.sin(2 * Math.PI * base * t) * 0.035 + Math.sin(2 * Math.PI * base * 0.5 * t) * 0.045;
    const pulsePhase = t % 1.2;
    const pulse = Math.sin(2 * Math.PI * (55 - pulsePhase * 12) * pulsePhase) * Math.exp(-12 * pulsePhase) * 0.09;
    const pianoPhase = t % 4;
    const piano = Math.sin(2 * Math.PI * base * 2 * t) * Math.exp(-2.8 * pianoPhase) * (t > 20 ? 0.055 : 0.025);
    const fade = Math.min(1, t / 1.5, (totalDuration - t) / 2);
    const sample = Math.max(-1, Math.min(1, (drone + pulse + piano) * fade));
    const value = Math.round(sample * 32767);
    pcm.writeInt16LE(value, i * 4);
    pcm.writeInt16LE(value, i * 4 + 2);
  }
  const header = Buffer.alloc(44);
  header.write("RIFF", 0); header.writeUInt32LE(36 + pcm.length, 4); header.write("WAVE", 8);
  header.write("fmt ", 12); header.writeUInt32LE(16, 16); header.writeUInt16LE(1, 20); header.writeUInt16LE(2, 22);
  header.writeUInt32LE(rate, 24); header.writeUInt32LE(rate * 4, 28); header.writeUInt16LE(4, 32); header.writeUInt16LE(16, 34);
  header.write("data", 36); header.writeUInt32LE(pcm.length, 40);
  fs.writeFileSync(output, Buffer.concat([header, pcm]));
}

function createAss(file) {
  const events = [
    ["0:00:00.35", "0:00:01.35", "{\\an5\\fs112\\b1\\1c&H00FFFFFF&}06.45"],
    ["0:00:17.10", "0:00:18.15", "{\\an8\\fs78\\b1}20.15"],
    ["0:00:18.20", "0:00:19.25", "{\\an8\\fs78\\b1}22.40"],
    ["0:00:19.30", "0:00:20.80", "{\\an8\\fs78\\b1}00.05"],
    ["0:00:22.50", "0:00:26.20", "Her gün binlerce insan görüyoruz..."],
    ["0:00:26.50", "0:00:30.50", "Ama giderek daha az insan tanıyoruz."],
    ["0:00:32.00", "0:00:35.00", "Aynı şehirde yaşıyoruz."],
    ["0:00:35.30", "0:00:38.70", "Aynı sokaklarda yürüyoruz."],
    ["0:00:39.00", "0:00:43.00", "Belki de aynı şeyleri seviyoruz."],
    ["0:00:43.20", "0:00:47.70", "Ama birbirimizi hiç bulamıyoruz."]
  ];
  const body = events.map(([start, end, text]) => `Dialogue: 0,${start},${end},Narrator,,0,0,0,,${text}`).join("\n");
  fs.writeFileSync(file, `[Script Info]\nScriptType: v4.00+\nPlayResX: 1080\nPlayResY: 1920\nWrapStyle: 2\n\n[V4+ Styles]\nFormat: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\nStyle: Narrator,Segoe UI,58,&H00F4F1EA,&H00F4F1EA,&HCC07132E,&H9907132E,-1,0,0,0,100,100,0,0,1,3,1,2,82,82,260,1\n\n[Events]\nFormat: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n${body}\n`, "utf8");
}

function run(command, args, cwd = root) {
  const result = spawnSync(command, args, { cwd, stdio: "inherit" });
  if (result.status !== 0) throw new Error(`${command} failed with ${result.status}`);
}

await createCards();
const atmosphere = path.join(work, "atmosphere.wav");
createAtmosphere(atmosphere);
createAss(path.join(work, "captions.ass"));

// Optional scratch voiceover. The sandboxed Windows speech engine may reject
// file output, so the default render remains a music + burned-caption master.
if (process.env.BIALEM_GENERATE_VOICE === "1") {
  run("powershell.exe", ["-ExecutionPolicy", "Bypass", "-File", path.join(root, "scripts", "generate-bialem-narration.ps1"), "-OutputDirectory", work]);
}

const inputArgs = [];
clips.forEach((clip) => {
  if (clip.type === "black") inputArgs.push("-loop", "1", "-t", String(clip.duration), "-i", path.join(work, "black.png"));
  else if (clip.type === "end") inputArgs.push("-loop", "1", "-t", String(clip.duration), "-i", path.join(work, "end-card.png"));
  else inputArgs.push("-ss", String(clip.start), "-t", String(clip.duration), "-i", path.join(media, clip.file));
});
inputArgs.push("-i", atmosphere);

const filters = [];
clips.forEach((clip, index) => {
  const grade = index < 9 ? "eq=brightness=-0.10:contrast=1.10:saturation=0.56" : "eq=brightness=-0.075:contrast=1.08:saturation=0.68";
  filters.push(`[${index}:v]scale=${width}:${height}:force_original_aspect_ratio=increase,crop=${width}:${height},setsar=1,fps=${fps},trim=duration=${clip.duration},setpts=PTS-STARTPTS,${grade},format=yuv420p[v${index}]`);
});

let videoLabel = "v0";
let cumulative = clips[0].duration;
for (let i = 1; i < clips.length; i++) {
  const offset = cumulative - transition * i;
  const next = `x${i}`;
  filters.push(`[${videoLabel}][v${i}]xfade=transition=fade:duration=${transition}:offset=${offset.toFixed(3)}[${next}]`);
  videoLabel = next;
  cumulative += clips[i].duration;
}
filters.push(`[${videoLabel}]ass=captions.ass,fade=t=out:st=${(totalDuration - 0.8).toFixed(2)}:d=0.8,format=yuv420p[video]`);

const audioBase = clips.length;
filters.push(`[${audioBase}:a]volume=0.72[bed]`);
filters.push(`sine=frequency=880:duration=0.16,adelay=550|550,volume=0.15[alarm1]`);
filters.push(`sine=frequency=880:duration=0.16,adelay=1050|1050,volume=0.15[alarm2]`);
filters.push(`[bed][alarm1][alarm2]amix=inputs=3:normalize=0,loudnorm=I=-18:LRA=7:TP=-1.5,afade=t=out:st=${(totalDuration - 1.2).toFixed(2)}:d=1.2[audio]`);

const output = path.join(project, "bialem-gercek-reels-filmi.mp4");
run(ffmpegPath, ["-y", ...inputArgs, "-filter_complex", filters.join(";"), "-map", "[video]", "-map", "[audio]", "-t", totalDuration.toFixed(3), "-c:v", "libx264", "-preset", "medium", "-crf", "20", "-r", String(fps), "-c:a", "aac", "-b:a", "192k", "-movflags", "+faststart", output], work);
console.log(output);
