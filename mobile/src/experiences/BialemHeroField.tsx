import { useRef } from "react";
import { StyleSheet, View } from "react-native";
import { useTheme } from "../theme/theme";
import { useBialemExperience, type EngineContext, type EngineScene } from "./useBialemExperience";

/**
 * Ambient 3D background layer (particles + drifting discovery rings).
 * Designed as a decorative overlay for hero/onboarding surfaces.
 * Transparent, non-interactive and fully optional — normal UI renders
 * on top and the layer degrades to nothing when WebGL is unavailable.
 */
export function BialemHeroField({ active = true, intensity = 1 }: { active?: boolean; intensity?: number }) {
  const { resolvedTheme } = useTheme();
  const containerRef = useRef(null);
  const { ready } = useBialemExperience({
    containerRef,
    theme: resolvedTheme,
    deps: [resolvedTheme, active, intensity],
    build: (context) => buildHeroField(context, intensity)
  });

  if (!active) return null;

  return <View ref={containerRef} aria-hidden style={styles.container} />;
}

function buildHeroField(context: EngineContext, intensity: number): EngineScene {
  const { THREE, palette, tier } = context;
  const root = new THREE.Group();

  const particleCount = tier === "high" ? 420 : tier === "medium" ? 220 : 90;
  const seeded = mulberry32(9981);
  const positions = new Float32Array(particleCount * 3);
  for (let i = 0; i < particleCount; i++) {
    const spread = 9;
    positions[i * 3] = (seeded() - 0.5) * spread * 2.2;
    positions[i * 3 + 1] = (seeded() - 0.5) * spread;
    positions[i * 3 + 2] = (seeded() - 0.5) * spread;
  }
  const geometry = new THREE.BufferGeometry();
  geometry.setAttribute("position", new THREE.BufferAttribute(positions, 3));
  const material = new THREE.PointsMaterial({
    color: palette.ringA,
    size: tier === "low" ? 0.05 : 0.07,
    transparent: true,
    opacity: 0.38 * intensity,
    depthWrite: false,
    sizeAttenuation: true
  });
  const points = new THREE.Points(geometry, material);
  root.add(points);

  const ringGroup = new THREE.Group();
  const ringMaterials = [
    new THREE.MeshBasicMaterial({ color: palette.ringA, transparent: true, opacity: 0.28 * intensity }),
    new THREE.MeshBasicMaterial({ color: palette.ringB, transparent: true, opacity: 0.2 * intensity })
  ];
  for (let i = 0; i < 2; i++) {
    const ring = new THREE.Mesh(new THREE.TorusGeometry(3.4 + i * 0.9, 0.012 + i * 0.004, 6, 72), ringMaterials[i]);
    ring.rotation.x = Math.PI / 2 + (i === 0 ? 0.5 : -0.55);
    ring.rotation.z = i * 1.3;
    ringGroup.add(ring);
  }
  ringGroup.position.set(0, 0, -2);
  root.add(ringGroup);

  if (tier === "high" || tier === "medium") {
    const coreMat = new THREE.MeshBasicMaterial({
      color: palette.accent,
      transparent: true,
      opacity: 0.08 * intensity
    });
    const core = new THREE.Mesh(new THREE.SphereGeometry(1.8, 24, 20), coreMat);
    core.position.set(0, 0, -3.5);
    root.add(core);
  }

  let pointerX = 0;
  let pointerY = 0;
  let localElapsed = 0;
  const update = (delta: number) => {
    localElapsed += delta;
    points.rotation.y += delta * 0.012;
    points.position.y = Math.sin(localElapsed * 0.3) * 0.3;
    ringGroup.rotation.z += delta * 0.05;
    ringGroup.rotation.x = Math.sin(localElapsed * 0.1) * 0.06 + 0.5;
    root.rotation.y += (pointerX * 0.05 - root.rotation.y) * Math.min(1, delta * 1.5);
    root.rotation.x += (pointerY * 0.04 - root.rotation.x) * Math.min(1, delta * 1.5);
  };

  if (tier !== "low" && typeof window !== "undefined") {
    const onMove = (event: PointerEvent) => {
      pointerX = (event.clientX / Math.max(1, window.innerWidth)) - 0.5;
      pointerY = (event.clientY / Math.max(1, window.innerHeight)) - 0.5;
    };
    window.addEventListener("pointermove", onMove, { passive: true });
    // If any pointermove listener is added, ensure it is removed on dispose.
    return {
      root,
      update,
      dispose: () => {
        window.removeEventListener("pointermove", onMove);
        disposeScene(root);
      }
    };
  }

  return {
    root,
    update,
    dispose: () => disposeScene(root)
  };
}

function mulberry32(seed: number) {
  return () => {
    seed |= 0;
    seed = (seed + 0x6d2b79f5) | 0;
    let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function disposeScene(root: import("three").Object3D) {
  root.traverse((child) => {
    const mesh = child as import("three").Mesh;
    if (mesh.geometry) mesh.geometry.dispose();
    const material = mesh.material as import("three").Material | import("three").Material[] | undefined;
    if (Array.isArray(material)) material.forEach((m) => m.dispose());
    else if (material) material.dispose();
  });
}

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    overflow: "hidden",
    pointerEvents: "none"
  }
});