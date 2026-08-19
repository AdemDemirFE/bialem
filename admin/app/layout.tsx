import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  metadataBase: new URL("https://bialem.app"),
  title: {
    default: "Bi'Alem | Şehrinle yeniden tanış",
    template: "%s | Bi'Alem",
  },
  description:
    "Şehrindeki etkinlikleri keşfet, güvenli topluluklara katıl ve aynı planı paylaşan insanlarla gerçek hayatta buluş.",
  applicationName: "Bi'Alem",
  keywords: ["etkinlik", "topluluk", "Ankara", "sosyal keşif", "birlikte git"],
  openGraph: {
    title: "Bi'Alem | Şehrinle yeniden tanış",
    description:
      "Şehrindeki etkinlikleri keşfet, güvenli topluluklara katıl ve gerçek hayatta buluş.",
    type: "website",
    locale: "tr_TR",
    siteName: "Bi'Alem",
    images: [{ url: "/brand/onboarding-worlds.png", width: 1824, height: 864 }],
  },
  twitter: {
    card: "summary_large_image",
    title: "Bi'Alem | Şehrinle yeniden tanış",
    description: "Birlikte keşfet. Birlikte katıl.",
    images: ["/brand/onboarding-worlds.png"],
  },
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="tr">
      <body>{children}</body>
    </html>
  );
}
