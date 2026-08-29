export const CART_REFRESH_EVENT = "bialem-cart-updated";

export function notifyCartUpdated() {
  if (typeof window !== "undefined") {
    window.dispatchEvent(new CustomEvent(CART_REFRESH_EVENT));
  }
}
