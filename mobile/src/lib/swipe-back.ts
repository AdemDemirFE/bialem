import { useEffect } from "react";
import { router } from "./router";

const EDGE_WIDTH = 28;
const TRIGGER_DISTANCE = 90;
const MAX_DURATION = 450;

const TAB_PATHS = new Set(["/", "/feed", "/communities", "/assistant", "/notifications", "/profile", "/people"]);

function isSwipeBackEnabled(path: string) {
  const normalized = path.split("?")[0];
  if (TAB_PATHS.has(normalized)) return false;
  return true;
}

export function useSwipeBack(enabled = true) {
  useEffect(() => {
    if (!enabled || typeof document === "undefined") return;

    let startX = 0;
    let startY = 0;
    let startTime = 0;
    let active = false;

    const onStart = (e: TouchEvent) => {
      if (!isSwipeBackEnabled(window.location.pathname)) return;
      const touch = e.touches[0];
      if (touch.clientX > EDGE_WIDTH) return;
      startX = touch.clientX;
      startY = touch.clientY;
      startTime = Date.now();
      active = true;
    };

    const onMove = (e: TouchEvent) => {
      if (!active) return;
      const touch = e.touches[0];
      const dx = touch.clientX - startX;
      const dy = touch.clientY - startY;
      if (dx > 0 && Math.abs(dx) > Math.abs(dy) && dx > TRIGGER_DISTANCE * 0.4) {
        e.preventDefault();
      }
    };

    const onEnd = (e: TouchEvent) => {
      if (!active) return;
      active = false;
      const touch = e.changedTouches[0];
      const dx = touch.clientX - startX;
      const dy = touch.clientY - startY;
      const duration = Date.now() - startTime;
      if (dx > TRIGGER_DISTANCE && Math.abs(dx) > Math.abs(dy) && duration < MAX_DURATION) {
        router.back();
      }
    };

    const onCancel = () => {
      active = false;
    };

    document.addEventListener("touchstart", onStart, { passive: true });
    document.addEventListener("touchmove", onMove, { passive: false });
    document.addEventListener("touchend", onEnd, { passive: true });
    document.addEventListener("touchcancel", onCancel, { passive: true });

    return () => {
      document.removeEventListener("touchstart", onStart);
      document.removeEventListener("touchmove", onMove);
      document.removeEventListener("touchend", onEnd);
      document.removeEventListener("touchcancel", onCancel);
    };
  }, [enabled]);
}
