import type { Metadata } from "next";
import { ResetPasswordBridge } from "./reset-password-bridge";

export const metadata: Metadata = {
  title: "Şifreyi yenile | Bialem",
  description: "Bialem hesabının şifresini güvenli biçimde yenile."
};

export default function ResetPasswordPage() {
  return <ResetPasswordBridge />;
}
