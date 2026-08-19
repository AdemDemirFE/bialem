import { Component, type ReactNode } from "react";

type Props = { children: ReactNode };
type State = { error: Error | null };

export class ErrorBoundary extends Component<Props, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: Error) {
    return { error };
  }

  render() {
    if (!this.state.error) return this.props.children;

    return (
      <div style={{ padding: 24, fontFamily: "sans-serif", color: "#0a1833", background: "#f4f6fb", minHeight: "100%" }}>
        <h1 style={{ fontSize: 22 }}>Uygulama yüklenemedi</h1>
        <pre style={{ whiteSpace: "pre-wrap", color: "#c94568" }}>{this.state.error.message}</pre>
      </div>
    );
  }
}
