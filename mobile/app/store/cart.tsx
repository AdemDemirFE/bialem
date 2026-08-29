import { Stack } from "expo-router";
import { CartScreenContent } from "../../src/components/CartScreenContent";

export default function CartScreen() {
  return (
    <>
      <Stack.Screen options={{ title: "Sepetim" }} />
      <CartScreenContent />
    </>
  );
}
