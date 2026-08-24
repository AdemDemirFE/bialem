import { createRoot } from "react-dom/client";
import { AppRegistry } from "react-native";
import { App } from "./App";
import { ErrorBoundary } from "./ErrorBoundary";
import "sweetalert2/dist/sweetalert2.min.css";
import "./components/AppAlert.css";
import "./theme/global.css";

AppRegistry.registerComponent("bialem", () => App);
const rootTag = document.getElementById("root");
if (rootTag) {
  createRoot(rootTag).render(
    <ErrorBoundary>
      <App />
    </ErrorBoundary>
  );
}
