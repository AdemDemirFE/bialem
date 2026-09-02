import { useEffect, useRef, useState } from "react";
import { getExperienceTier, isExperienceEnabled, type ExperienceTier } from "./capabilities";
import { BialemExperience, type EngineContext, type EngineScene } from "./engine";
import { getExperiencePalette, type ExperiencePalette } from "./palette";

type Build = (context: EngineContext) => EngineScene;

type Options = {
  /** Ref attached to the container DOM node (react-native-web View). */
  containerRef: { current: unknown };
  /** Scene builder — invoked on mount and whenever `deps` change. */
  build: Build;
  /** Values that require a scene rebuild (theme, mascot state, ...). */
  deps?: readonly unknown[];
  theme?: "light" | "dark";
};

export type { Build, EngineContext, EngineScene };

export function useBialemExperience({ containerRef, build, deps = [], theme = "light" }: Options) {
  const engineRef = useRef<BialemExperience | null>(null);
  const [ready, setReady] = useState(false);
  const tier = getExperienceTier();

  useEffect(() => {
    if (!isExperienceEnabled()) return;

    const container = containerRef.current as HTMLElement | null;
    if (!container) return;

    let cancelled = false;
    let engine: BialemExperience | null = null;

    void import("three")
      .then((THREE) => {
        if (cancelled) return;
        const palette = getExperiencePalette(theme);
        engine = new BialemExperience(container, THREE, build, palette, tier);
        engine.start();
        engineRef.current = engine;
        setReady(true);
      })
      .catch((error) => {
        if (!cancelled) console.warn("[Bialem 3D] experience failed to start", error);
      });

    return () => {
      cancelled = true;
      engineRef.current = null;
      engine?.dispose();
      setReady(false);
    };
  }, []);

  useEffect(() => {
    if (!ready) return;
    engineRef.current?.rebuild((context: EngineContext) => {
      const palette: ExperiencePalette = getExperiencePalette(theme);
      return build({ ...context, palette });
    });
  }, deps);

  return { ready, tier, enabled: tier !== "off" };
}

export type { ExperienceTier };