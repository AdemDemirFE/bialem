import type { EngineContext, EngineScene } from "./engine";

export type MascotState =
  | "idle"
  | "hello"
  | "happy"
  | "excited"
  | "thinking"
  | "loading"
  | "searching"
  | "exploring"
  | "shopping"
  | "event"
  | "location"
  | "success"
  | "sleep"
  | "error";

const ANIMATED_STATES: ReadonlySet<MascotState> = new Set([
  "searching",
  "loading",
  "exploring",
  "event",
  "location"
]);

function mulberry32(seed: number) {
  return () => {
    seed |= 0;
    seed = (seed + 0x6d2b79f5) | 0;
    let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function buildParticlePositions(count: number, spread: number): Float32Array {
  const random = mulberry32(20260817);
  const positions = new Float32Array(count * 3);
  for (let i = 0; i < count; i++) {
    const radius = spread * (0.45 + random() * 0.55);
    const theta = random() * Math.PI * 2;
    const phi = Math.acos(2 * random() - 1);
    positions[i * 3] = radius * Math.sin(phi) * Math.cos(theta);
    positions[i * 3 + 1] = radius * Math.cos(phi);
    positions[i * 3 + 2] = radius * Math.sin(phi) * Math.sin(theta);
  }
  return positions;
}

export function buildMascotScene(context: EngineContext, state: MascotState): EngineScene {
  const { THREE, palette, tier } = context;
  const root = new THREE.Group();
  const particleCount = tier === "high" ? 46 : tier === "medium" ? 26 : 14;

  const bodyMat = new THREE.MeshStandardMaterial({
    color: palette.accent,
    roughness: 0.42,
    metalness: 0.04,
    emissive: palette.accent,
    emissiveIntensity: tier === "low" ? 0.05 : 0.12
  });
  const body = new THREE.Mesh(new THREE.SphereGeometry(0.98, 28, 28), bodyMat);
  body.scale.set(1, 0.94, 1);
  body.position.y = -0.12;
  root.add(body);

  const pupilMat = new THREE.MeshStandardMaterial({ color: palette.ink, roughness: 0.25, metalness: 0 });
  const whiteMat = new THREE.MeshStandardMaterial({ color: "#ffffff", roughness: 0.3, metalness: 0 });
  const eyes = new THREE.Group();
  const buildEye = (x: number) => {
    const group = new THREE.Group();
    group.position.set(x, 0.18, 0.9);
    const white = new THREE.Mesh(new THREE.SphereGeometry(0.19, 18, 14), whiteMat);
    white.scale.set(1, 1.25, 0.55);
    const pupil = new THREE.Mesh(new THREE.SphereGeometry(0.09, 14, 12), pupilMat);
    pupil.position.set(0, -0.02, 0.1);
    group.add(white, pupil);
    group.userData.pupil = pupil;
    eyes.add(group);
  };
  buildEye(-0.34);
  buildEye(0.34);
  eyes.position.z = 0.12;
  root.add(eyes);

  const browMat = new THREE.MeshBasicMaterial({ color: palette.ink, transparent: true, opacity: 0.9 });
  const brows = new THREE.Group();
  for (const x of [-0.34, 0.34]) {
    const brow = new THREE.Mesh(new THREE.TorusGeometry(0.15, 0.02, 6, 14, Math.PI), browMat.clone());
    brow.position.set(x, 0.52, 0.9);
    brow.rotation.z = -Math.PI * 0.14;
    brow.userData.side = x < 0 ? -1 : 1;
    brows.add(brow);
  }
  root.add(brows);

  const mouthMat = new THREE.MeshStandardMaterial({ color: palette.ink, roughness: 0.4 });
  const mouth = new THREE.Mesh(new THREE.TorusGeometry(0.2, 0.045, 8, 16, Math.PI), mouthMat);
  mouth.position.set(0, -0.34, 0.9);
  mouth.rotation.z = Math.PI;
  root.add(mouth);

  const ringMat = new THREE.MeshBasicMaterial({ color: palette.ringA, transparent: true, opacity: tier === "low" ? 0.3 : 0.55 });
  const ring = new THREE.Group();
  const ringMesh = new THREE.Mesh(new THREE.TorusGeometry(1.48, 0.02, 8, 64), ringMat);
  ringMesh.rotation.x = Math.PI / 2;
  const beaconMat = new THREE.MeshStandardMaterial({ color: palette.ringB, emissive: palette.ringB, emissiveIntensity: 0.9 });
  for (let i = 0; i < 4; i++) {
    const angle = (i / 4) * Math.PI * 2;
    const dot = new THREE.Mesh(new THREE.SphereGeometry(0.045, 8, 8), beaconMat.clone());
    dot.position.set(Math.cos(angle) * 1.48, 0, Math.sin(angle) * 1.48);
    ring.add(dot);
  }
  ring.add(ringMesh);
  ring.rotation.x = 1.15;
  root.add(ring);

  const antennaMat = new THREE.MeshStandardMaterial({ color: palette.muted, roughness: 0.5 });
  const antenna = new THREE.Mesh(new THREE.CylinderGeometry(0.03, 0.045, 0.5, 8), antennaMat);
  antenna.position.set(0, 1.28, 0);
  const beacon = new THREE.Mesh(new THREE.SphereGeometry(0.1, 12, 10), beaconMat);
  beacon.position.set(0, 1.56, 0);
  root.add(antenna, beacon);

  const particleGeo = new THREE.BufferGeometry();
  particleGeo.setAttribute("position", new THREE.BufferAttribute(buildParticlePositions(particleCount, 2.5), 3));
  const particleMat = new THREE.PointsMaterial({
    color: palette.ringA,
    size: tier === "low" ? 0.05 : 0.07,
    transparent: true,
    opacity: 0.5,
    sizeAttenuation: true,
    depthWrite: false
  });
  const particles = new THREE.Points(particleGeo, particleMat);
  root.add(particles);

  const glowMat = new THREE.MeshBasicMaterial({
    color: palette.accent,
    transparent: true,
    opacity: tier === "low" ? 0.05 : 0.09,
    side: THREE.DoubleSide,
    depthWrite: false
  });
  const glow = new THREE.Mesh(new THREE.CircleGeometry(1.45, 40), glowMat);
  glow.rotation.x = -Math.PI / 2;
  glow.position.y = -1.04;
  root.add(glow);

  const hemisphere = new THREE.HemisphereLight(palette.ringA, palette.page, 0.85);
  root.add(hemisphere);
  const key = new THREE.DirectionalLight("#ffffff", 1.15);
  key.position.set(2.5, 4, 3.2);
  root.add(key);
  const under = new THREE.PointLight(palette.accent, 0.55, 8);
  under.position.set(0, -1.4, 1.4);
  root.add(under);

  let smileTarget = Math.PI;
  let browsVisible = false;
  const fastRing = ANIMATED_STATES.has(state);
  if (state === "hello" || state === "happy" || state === "excited" || state === "success") {
    browsVisible = true;
    smileTarget = Math.PI;
  } else if (state === "thinking") {
    smileTarget = Math.PI * 0.82;
  } else if (state === "error") {
    smileTarget = 0;
    browsVisible = true;
  } else if (state === "sleep") {
    smileTarget = Math.PI * 0.6;
  }

  let localElapsed = 0;
  const update = (delta: number) => {
    localElapsed += delta;
    const t = localElapsed;
    const bobPhase = t * 1.8;
    if (state === "error") {
      root.position.x = Math.sin(t * 42) * 0.035;
      root.position.y = -0.12 + Math.sin(t * 6) * 0.02;
    } else {
      const jump = state === "success" || state === "happy" || state === "hello" ? Math.exp(-t * 2.2) * 0.5 : 0;
      root.position.x = 0;
      root.position.y = -0.12 + Math.sin(bobPhase) * 0.07 + jump;
    }

    body.scale.set(1, 0.94 + Math.sin(bobPhase * 0.9) * 0.02, 1);

    const blinkPeriod = state === "sleep" ? 1000 : 3.4;
    const blinkPhase = t % blinkPeriod;
    const targetEyeScale = state === "sleep" ? 0.06 : blinkPhase < 0.13 ? 0.1 : 1;
    eyes.children.forEach((eye) => {
      eye.scale.y += (targetEyeScale - eye.scale.y) * Math.min(1, delta * 12);
      const pupil = eye.userData.pupil as import("three").Mesh | undefined;
      if (pupil && state === "thinking") pupil.position.y = 0.06;
    });

const targetBrowOpacity = browsVisible ? 0.9 : 0;
      brows.children.forEach((brow) => {
        const material = (brow as import("three").Mesh).material as import("three").MeshBasicMaterial;
        material.opacity += (targetBrowOpacity - material.opacity) * Math.min(1, delta * 8);
      });

    mouth.rotation.z += (smileTarget - mouth.rotation.z) * Math.min(1, delta * 5);

    ring.rotation.z += delta * (fastRing ? 1.55 : 0.3) * (state === "sleep" ? 0 : 1);

    const beaconPulse = state === "location" || state === "searching" || state === "event"
      ? 1.6 + Math.sin(t * 5) * 0.7
      : 0.9;
    beaconMat.emissiveIntensity += (beaconPulse - beaconMat.emissiveIntensity) * Math.min(1, delta * 6);

    particles.rotation.y += delta * 0.05;
  };

  return {
    root,
    update,
    dispose: () => {
      root.traverse((child) => {
        const mesh = child as import("three").Mesh;
        if (mesh.geometry) mesh.geometry.dispose();
        const material = mesh.material as import("three").Material | import("three").Material[] | undefined;
        if (Array.isArray(material)) material.forEach((m) => m.dispose());
        else if (material) material.dispose();
      });
    }
  };
}