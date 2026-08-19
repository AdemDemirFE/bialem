import { createRoot } from "react-dom/client";
import { AppRegistry } from "react-native";
import { App } from "./App";
import { ErrorBoundary } from "./ErrorBoundary";
import "./components/AppAlert.css";

AppRegistry.registerComponent("bialem", () => App);
const rootTag = document.getElementById("root");
if (rootTag) {
  createRoot(rootTag).render(
    <ErrorBoundary>
      <App />
    </ErrorBoundary>
  );
}
