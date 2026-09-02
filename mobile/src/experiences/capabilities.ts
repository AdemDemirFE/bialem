export type ExperienceTier = "off" | "low" | "medium" | "high";

let cachedTier: ExperienceTier | null = null;

export function prefersReducedMotion(): boolean {
  if (typeof window === "undefined" || typeof window.matchMedia !== "function") return false;
  try {
    return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  } catch {
    return false;
  }
}

export function detectWebGL(): boolean {
  if (typeof document === "undefined" || typeof document.createElement !== "function") return false;
  try {
    const canvas = document.createElement("canvas");
    const gl =
      canvas.getContext("webgl2") ??
      canvas.getContext("webgl") ??
      (canvas.getContext("experimental-webgl") as WebGLRenderingContext | null);
    return Boolean(gl);
  } catch {
    return false;
  }
}

function scoreDevice(): number {
  let score = 0;
  if (typeof navigator !== "undefined" && navigator.hardwareConcurrency) {
    if (navigator.hardwareConcurrency >= 8) score += 2;
    else if (navigator.hardwareConcurrency >= 4) score += 1;
  }
  if (typeof navigator !== "undefined") {
    const memory = (navigator as unknown as { deviceMemory?: number }).deviceMemory;
    if (typeof memory === "number" && memory >= 8) score += 1;
    else if (typeof memory === "number" && memory >= 4) score += 0;
  }
  return score;
}

export function getExperienceTier(): ExperienceTier {
  if (cachedTier) return cachedTier;

  if (!detectWebGL()) {
    cachedTier = "off";
    return cachedTier;
  }

  const score = scoreDevice();
  const dpr = typeof window !== "undefined" && window.devicePixelRatio ? window.devicePixelRatio : 1;
  if (score >= 3 && dpr >= 2) cachedTier = "high";
  else if (score >= 2 || dpr > 1) cachedTier = "medium";
  else cachedTier = "low";
  return cachedTier;
}

export function isExperienceEnabled(): boolean {
  return getExperienceTier() !== "off";
}