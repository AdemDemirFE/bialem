let navigateRef: ((to: string, opts?: { replace?: boolean }) => void) | null = null;
let backRef: (() => void) | null = null;

export function bindRouter(
  navigate: (to: string, opts?: { replace?: boolean }) => void,
  back: () => void
) {
  navigateRef = navigate;
  backRef = back;
}

export const router = {
  push(to: string) {
    navigateRef?.(to);
  },
  replace(to: string) {
    navigateRef?.(to, { replace: true });
  },
  back() {
    backRef?.();
  }
};
