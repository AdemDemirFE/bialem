import type { ExperienceTier } from "./capabilities";
import { prefersReducedMotion } from "./capabilities";
import type { ExperiencePalette } from "./palette";

type Three = typeof import("three");

export type EngineScene = {
  root: import("three").Object3D;
  update?: (delta: number, elapsed: number) => void;
  dispose?: () => void;
};

export type EngineContext = {
  THREE: Three;
  scene: import("three").Scene;
  camera: import("three").PerspectiveCamera;
  palette: ExperiencePalette;
  tier: ExperienceTier;
  aspect: number;
  renderer: import("three").WebGLRenderer;
};

function capDpr(tier: ExperienceTier, deviceDpr: number): number {
  const max = tier === "high" ? 2 : tier === "medium" ? 1.5 : 1;
  return Math.min(deviceDpr || 1, max);
}

export class BialemExperience {
  private readonly THREE: Three;
  private renderer: import("three").WebGLRenderer;
  private scene: import("three").Scene;
  private camera: import("three").PerspectiveCamera;
  private clock: import("three").Clock;
  private sceneEntry: EngineScene;
  private readonly tier: ExperienceTier;
  private reducedMotion: boolean;
  private rafId = 0;
  private running = false;
  private container: HTMLElement;
  private resolver?: ResizeObserver;
  private onVisibility: () => void;
  private palette: ExperiencePalette;
  private disposed = false;

  constructor(container: HTMLElement, THREE: Three, builder: (context: EngineContext) => EngineScene, palette: ExperiencePalette, tier: ExperienceTier) {
    this.container = container;
    this.THREE = THREE;
    this.palette = palette;
    this.tier = tier;
    this.reducedMotion = prefersReducedMotion();

    this.renderer = new THREE.WebGLRenderer({
      alpha: true,
      antialias: tier === "high",
      powerPreference: "high-performance",
      stencil: false,
      depth: true
    });
    this.renderer.setPixelRatio(capDpr(tier, typeof window !== "undefined" ? window.devicePixelRatio : 1));
    this.renderer.domElement.style.display = "block";
    this.renderer.domElement.style.pointerEvents = "none";

    const width = container.clientWidth || 1;
    const height = container.clientHeight || 1;
    this.renderer.setSize(width, height);

    this.scene = new THREE.Scene();
    this.camera = new THREE.PerspectiveCamera(42, width / height, 0.1, 100);
    this.camera.position.set(0, 0, 8);
    this.clock = new THREE.Clock();

    this.sceneEntry = builder(this.makeContext());
    this.scene.add(this.sceneEntry.root);
    container.appendChild(this.renderer.domElement);

    this.resize = this.resize.bind(this);
    if (typeof ResizeObserver !== "undefined") {
      this.resolver = new ResizeObserver(this.resize);
      this.resolver.observe(container);
    }

    this.onVisibility = () => {
      if (typeof document === "undefined") return;
      if (document.hidden) this.pause();
      else this.resume();
    };
    if (typeof document !== "undefined") document.addEventListener("visibilitychange", this.onVisibility);
  }

  private makeContext(): EngineContext {
    return {
      THREE: this.THREE,
      scene: this.scene,
      camera: this.camera,
      renderer: this.renderer,
      palette: this.palette,
      tier: this.tier,
      aspect: (this.container.clientWidth || 1) / (this.container.clientHeight || 1)
    };
  }

  /** Replaces the whole scene entry (used for mascot state + theme changes). */
  rebuild(builder: (context: EngineContext) => EngineScene) {
    if (this.disposed) return;
    this.scene.remove(this.sceneEntry.root);
    this.sceneEntry.dispose?.();
    this.sceneEntry = builder(this.makeContext());
    this.scene.add(this.sceneEntry.root);
  }

  start() {
    if (this.running || this.disposed) return;
    this.running = true;
    if (this.reducedMotion) {
      this.renderer.render(this.scene, this.camera);
      this.running = false;
      this.rafId = 0;
      return;
    }
    this.clock.start();
    this.tick();
  }

  pause() {
    this.running = false;
    if (this.rafId) {
      cancelAnimationFrame(this.rafId);
      this.rafId = 0;
    }
  }

  resume() {
    if (this.disposed) return;
    this.clock.getDelta();
    this.start();
  }

  private tick = () => {
    if (!this.running || this.disposed) return;
    if (!this.reducedMotion) {
      const delta = Math.min(this.clock.getDelta(), 0.05);
      const elapsed = this.clock.elapsedTime;
      this.sceneEntry.update?.(delta, elapsed);
    }
    this.renderer.render(this.scene, this.camera);
    this.rafId = requestAnimationFrame(this.tick);
  };

  resize = () => {
    if (this.disposed) return;
    const width = this.container.clientWidth || 1;
    const height = this.container.clientHeight || 1;
    this.camera.aspect = width / height;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(width, height);
    if (this.reducedMotion) this.renderer.render(this.scene, this.camera);
  };

  setReducedMotion(reduced: boolean) {
    this.reducedMotion = reduced;
    if (reduced) this.clock.stop();
    else this.clock.start();
  }

  dispose() {
    if (this.disposed) return;
    this.disposed = true;
    this.pause();
    if (typeof document !== "undefined") document.removeEventListener("visibilitychange", this.onVisibility);
    this.resolver?.disconnect();
    try {
      this.sceneEntry.dispose?.();
      this.scene.traverse((child) => {
        const mesh = child as import("three").Mesh;
        if (mesh.geometry) mesh.geometry.dispose();
        const material = (mesh.material as import("three").Material | import("three").Material[] | undefined);
        if (Array.isArray(material)) material.forEach((m) => m.dispose());
        else if (material) material.dispose();
      });
    } finally {
      // Must leave the DOM so repeated mount/remount cycles do not leak contexts.
      this.renderer.domElement.remove();
      this.renderer.dispose();
    }
  }
}