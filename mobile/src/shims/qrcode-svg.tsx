import { Image } from "react-native";

type QRCodeProps = {
  value: string;
  size?: number;
  color?: string;
  backgroundColor?: string;
};

function hex(value: string) {
  return String(value).replace("#", "").replace(/^0x/, "") || "000000";
}

export default function QRCode({ value, size = 128, color = "#000000", backgroundColor = "#ffffff" }: QRCodeProps) {
  const src = `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&color=${hex(color)}&bgcolor=${hex(backgroundColor)}&data=${encodeURIComponent(value)}`;
  return <Image accessibilityLabel="QR kod" source={{ uri: src }} style={{ width: size, height: size, backgroundColor }} />;
}
