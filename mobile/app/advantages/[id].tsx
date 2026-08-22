import { Ionicons } from "@expo/vector-icons";
import { Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Linking, Modal, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import QRCode from "react-native-qrcode-svg";
import { api } from "../../src/lib/api";
import { colors } from "../../src/theme/colors";

type Offer = {
  id: string;
  title: string;
  description: string | null;
  discount_percent: number;
  minimum_spend: number | null;
  maximum_discount: number | null;
  valid_until: string | null;
  per_user_limit: number | null;
  terms: string | null;
};

type Venue = {
  id: string;
  name: string;
  description: string | null;
  category: string;
  logo_url: string | null;
  cover_image_url: string | null;
  address: string;
  city: string;
  latitude: number | null;
  longitude: number | null;
  phone: string | null;
  partner_offers: Offer[];
};

type Redemption = {
  redemption_id: string;
  redemption_token: string;
  redemption_code: string;
  expires_at: string;
};

export default function AdvantageDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const insets = useSafeAreaInsets();
  const [venue, setVenue] = useState<Venue | null>(null);
  const [loading, setLoading] = useState(true);
  const [issuing, setIssuing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [redemption, setRedemption] = useState<Redemption | null>(null);
  const [secondsLeft, setSecondsLeft] = useState(0);
  const [coverFailed, setCoverFailed] = useState(false);

  useEffect(() => {
    void api
      .from("partner_venues")
      .select("id, name, description, category, logo_url, cover_image_url, address, city, latitude, longitude, phone, partner_offers!partner_offers_venue_id_fkey(id, title, description, discount_percent, minimum_spend, maximum_discount, valid_until, per_user_limit, terms)")
      .eq("id", id)
      .single()
      .then((result) => {
        if (result.error) setError("Avantaj ayrıntıları yüklenemedi.");
        else setVenue(result.data as unknown as Venue);
        setLoading(false);
      });
  }, [id]);

  useEffect(() => {
    if (!redemption) return;
    const updateCountdown = () => {
      const next = Math.max(0, Math.ceil((new Date(redemption.expires_at).getTime() - Date.now()) / 1000));
      setSecondsLeft(next);
      if (next === 0) setRedemption(null);
    };
    updateCountdown();
    const timer = setInterval(updateCountdown, 1000);
    return () => clearInterval(timer);
  }, [redemption]);

  const offer = venue?.partner_offers?.[0];

  const issueCode = async () => {
    if (!offer) return;
    setIssuing(true);
    setError(null);
    const result = await api.rpc("issue_partner_offer_redemption", { target_offer_id: offer.id });
    if (result.error) setError(result.error.message);
    else setRedemption((result.data?.[0] ?? null) as Redemption | null);
    setIssuing(false);
  };

  const openMap = async () => {
    if (!venue) return;
    const query = venue.latitude && venue.longitude
      ? `${venue.latitude},${venue.longitude}`
      : `${venue.name} ${venue.address}`;
    await Linking.openURL(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(query)}`);
  };

  if (loading) return <View style={styles.center}><ActivityIndicator size="large" color={colors.accent} /></View>;
  if (!venue) return <View style={styles.center}><Text style={styles.error}>{error ?? "Kurum bulunamadı."}</Text></View>;

  const qrValue = redemption ? `bialem://advantage/redeem?token=${redemption.redemption_token}` : "";

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: venue.name }} />
      <ScrollView style={styles.screen} contentContainerStyle={styles.page}>
        {venue.cover_image_url && !coverFailed ? <Image source={{ uri: venue.cover_image_url }} style={styles.cover} onError={() => setCoverFailed(true)} /> : (
          <View style={styles.coverFallback}><Ionicons name="storefront" size={58} color="#fff" /></View>
        )}

        <View style={styles.identity}>
          {venue.logo_url ? <Image source={{ uri: venue.logo_url }} style={styles.logo} /> : null}
          <View style={styles.identityCopy}>
            <Text style={styles.kicker}>BİALEM ANLAŞMALI KURUMU</Text>
            <Text style={styles.name}>{venue.name}</Text>
          </View>
          {offer ? <Text style={styles.discount}>%{Number(offer.discount_percent).toLocaleString("tr-TR")}</Text> : null}
        </View>

        {venue.description ? <Text style={styles.description}>{venue.description}</Text> : null}

        <Pressable onPress={() => void openMap()} style={styles.locationCard}>
          <View style={styles.locationIcon}><Ionicons name="location" size={22} color={colors.ink} /></View>
          <View style={styles.locationCopy}>
            <Text style={styles.locationTitle}>{venue.city}</Text>
            <Text style={styles.locationText}>{venue.address}</Text>
          </View>
          <Ionicons name="open-outline" size={20} color={colors.accent} />
        </Pressable>

        {offer ? (
          <View style={styles.offerCard}>
            <Text style={styles.kicker}>SANA ÖZEL AVANTAJ</Text>
            <Text style={styles.offerTitle}>{offer.title}</Text>
            {offer.description ? <Text style={styles.description}>{offer.description}</Text> : null}
            <View style={styles.ruleGrid}>
              <Rule icon="wallet-outline" label="Alt limit" value={offer.minimum_spend ? `${offer.minimum_spend} ₺` : "Yok"} />
              <Rule icon="pricetag-outline" label="Üst indirim" value={offer.maximum_discount ? `${offer.maximum_discount} ₺` : "Yok"} />
              <Rule icon="repeat-outline" label="Kullanım" value={offer.per_user_limit ? `${offer.per_user_limit} kez` : "Sınırsız"} />
              <Rule icon="calendar-outline" label="Son tarih" value={offer.valid_until ? new Date(offer.valid_until).toLocaleDateString("tr-TR") : "Süresiz"} />
            </View>
            {offer.terms ? <Text style={styles.terms}>{offer.terms}</Text> : null}
            <View style={styles.memberQrIntro}>
              <Ionicons name="phone-portrait-outline" size={22} color={colors.accent} />
              <View style={styles.memberQrCopy}>
                <Text style={styles.memberQrTitle}>Üye QR kodun burada</Text>
                <Text style={styles.memberQrText}>Kasaya geldiğinde aşağıdaki düğmeye dokun ve oluşan kodu görevliye göster.</Text>
              </View>
            </View>
            <Pressable disabled={issuing} onPress={() => void issueCode()} style={[styles.useButton, issuing && styles.disabled]}>
              {issuing ? <ActivityIndicator color={colors.ink} /> : <Ionicons name="qr-code" size={23} color={colors.ink} />}
              <Text style={styles.useButtonText}>QR kodumu oluştur</Text>
            </Pressable>
            <Text style={styles.securityNote}>Kod kasada oluşturulmalı ve 60 saniye içinde kullanılmalıdır.</Text>
          </View>
        ) : (
          <View style={styles.offerCard}><Text style={styles.offerTitle}>Bu kurumun yeni kampanyası yakında.</Text></View>
        )}

        {error ? <Text style={styles.error}>{error}</Text> : null}
      </ScrollView>

      <Modal visible={Boolean(redemption)} transparent animationType="slide" onRequestClose={() => setRedemption(null)}>
        <View style={styles.modalBackdrop}>
          <View style={[styles.modalCard, { paddingBottom: Math.max(34, insets.bottom + 20) }]}>
            <View style={styles.timerRow}>
              <Text style={styles.modalKicker}>KASADA GÖSTER</Text>
              <View style={styles.timer}><Text style={styles.timerText}>{secondsLeft} sn</Text></View>
            </View>
            <Text style={styles.modalTitle}>{venue.name}</Text>
            <View style={styles.qrFrame}>
              {redemption ? <QRCode value={qrValue} size={210} color="#071b44" backgroundColor="#ffffff" /> : null}
            </View>
            <Text style={styles.code}>{redemption?.redemption_code}</Text>
            <Text style={styles.modalHelp}>İşletme görevlisi QR kodu okutabilir veya bu kodu girebilir.</Text>
            <Pressable onPress={() => setRedemption(null)} style={styles.closeButton}>
              <Text style={styles.closeButtonText}>Kapat</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </>
  );
}

function Rule({ icon, label, value }: { icon: keyof typeof Ionicons.glyphMap; label: string; value: string }) {
  return (
    <View style={styles.rule}>
      <Ionicons name={icon} size={18} color={colors.accent} />
      <Text style={styles.ruleLabel}>{label}</Text>
      <Text style={styles.ruleValue}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.page },
  page: { gap: 14, padding: 16, paddingBottom: 36 },
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24, backgroundColor: colors.page },
  cover: { width: "100%", height: 210, borderRadius: 20 },
  coverFallback: { height: 200, alignItems: "center", justifyContent: "center", borderRadius: 30, backgroundColor: colors.accent },
  identity: { flexDirection: "row", alignItems: "center", gap: 12 },
  logo: { width: 58, height: 58, borderRadius: 18, backgroundColor: colors.surface },
  identityCopy: { flex: 1, gap: 3 },
  kicker: { color: colors.accent, fontSize: 9, fontWeight: "900", letterSpacing: 1.3 },
  name: { color: colors.ink, fontSize: 27, lineHeight: 31, fontWeight: "900" },
  discount: { overflow: "hidden", paddingHorizontal: 12, paddingVertical: 10, borderRadius: 16, color: colors.ink, backgroundColor: colors.action, fontSize: 21, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 14, lineHeight: 22 },
  locationCard: { flexDirection: "row", alignItems: "center", gap: 12, padding: 15, borderRadius: 21, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.surface },
  locationIcon: { width: 44, height: 44, alignItems: "center", justifyContent: "center", borderRadius: 15, backgroundColor: colors.action },
  locationCopy: { flex: 1, gap: 3 },
  locationTitle: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  locationText: { color: colors.muted, fontSize: 12, lineHeight: 18 },
  offerCard: { gap: 14, padding: 21, borderRadius: 29, borderWidth: 1, borderColor: colors.warning, backgroundColor: colors.surface },
  offerTitle: { color: colors.ink, fontSize: 25, lineHeight: 30, fontWeight: "900" },
  ruleGrid: { flexDirection: "row", flexWrap: "wrap", gap: 9 },
  rule: { width: "48%", flexGrow: 1, gap: 4, padding: 12, borderRadius: 16, backgroundColor: colors.surfaceStrong },
  ruleLabel: { color: colors.muted, fontSize: 9, fontWeight: "800" },
  ruleValue: { color: colors.ink, fontSize: 13, fontWeight: "900" },
  terms: { padding: 12, borderRadius: 14, color: colors.muted, backgroundColor: colors.page, fontSize: 11, lineHeight: 17 },
  memberQrIntro: { flexDirection: "row", alignItems: "center", gap: 11, padding: 13, borderRadius: 17, backgroundColor: colors.accentSoft },
  memberQrCopy: { flex: 1, gap: 3 },
  memberQrTitle: { color: colors.ink, fontSize: 13, fontWeight: "900" },
  memberQrText: { color: colors.muted, fontSize: 11, lineHeight: 16 },
  useButton: { flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 9, padding: 16, borderRadius: 18, backgroundColor: colors.action },
  useButtonText: { color: colors.ink, fontSize: 15, fontWeight: "900" },
  disabled: { opacity: 0.55 },
  securityNote: { color: colors.muted, textAlign: "center", fontSize: 10, lineHeight: 15 },
  error: { color: colors.danger, textAlign: "center", fontWeight: "800" },
  modalBackdrop: { flex: 1, justifyContent: "flex-end", backgroundColor: "rgba(7,27,68,.7)" },
  modalCard: { gap: 12, padding: 18, paddingBottom: 28, borderTopLeftRadius: 24, borderTopRightRadius: 24, backgroundColor: colors.surface },
  timerRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  modalKicker: { color: colors.accent, fontSize: 11, fontWeight: "900", letterSpacing: 1.5 },
  timer: { paddingHorizontal: 12, paddingVertical: 7, borderRadius: 999, backgroundColor: colors.action },
  timerText: { color: colors.ink, fontWeight: "900" },
  modalTitle: { color: colors.ink, textAlign: "center", fontSize: 23, fontWeight: "900" },
  qrFrame: { alignSelf: "center", padding: 16, borderRadius: 24, backgroundColor: "#fff" },
  code: { color: colors.ink, textAlign: "center", fontSize: 28, fontWeight: "900", letterSpacing: 5 },
  modalHelp: { color: colors.muted, textAlign: "center", fontSize: 12, lineHeight: 18 },
  closeButton: { padding: 14, borderRadius: 16, backgroundColor: colors.brandInk },
  closeButtonText: { color: "#fff", textAlign: "center", fontWeight: "900" }
});
