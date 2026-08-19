import { Ionicons } from "@expo/vector-icons";
import { Link, useLocalSearchParams } from "expo-router";
import { useEffect, useState } from "react";
import { ActivityIndicator, Image, Linking, Pressable, ScrollView, Share, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import type { CityRadarEvent } from "../../src/components/CityDiscovery";
import { addEventToCalendar } from "../../src/lib/calendar";
import { api } from "../../src/lib/api";
import { normalizeImageUrl } from "../../src/lib/media-url";
import { colors } from "../../src/theme/colors";

type TicketOffer = {
  offer_id: string;
  seller_name: string;
  purchase_url: string;
  currency: string | null;
  min_price: number | null;
  max_price: number | null;
  price_label: string | null;
  availability: string;
  fees_included: boolean | null;
  is_official: boolean;
  last_checked_at: string;
  is_cheapest: boolean;
};

export default function CityEventDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const insets = useSafeAreaInsets();
  const [event, setEvent] = useState<CityRadarEvent | null>(null);
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [ticketOffers, setTicketOffers] = useState<TicketOffer[]>([]);

  const loadEvent = async () => {
    const [{ data, error: radarError }, { data: offerData, error: offerError }] = await Promise.all([
      api.rpc("get_city_radar", { target_city: null }),
      api.rpc("get_city_event_ticket_offers", { target_event_id: id })
    ]);
    if (radarError) setError(radarError.message);
    else setEvent(((data ?? []) as CityRadarEvent[]).find((item) => item.event_id === id) ?? null);
    if (offerError) setError(offerError.message);
    else setTicketOffers((offerData ?? []) as TicketOffer[]);
    setLoading(false);
  };

  useEffect(() => {
    void loadEvent();
  }, [id]);

  const toggleInterest = async (lookingForCompany: boolean) => {
    if (!event) return;
    setWorking(true);
    setError(null);
    setNotice(null);
    const { error: interestError } = await api.rpc("set_city_event_interest", {
      target_event_id: event.event_id,
      target_looking_for_company: lookingForCompany
    });
    if (interestError) setError(interestError.message);
    else {
      setNotice(lookingForCompany ? "Birlikte Git listesine eklendin." : "Etkinliği ilgi listene ekledin.");
      await loadEvent();
    }
    setWorking(false);
  };

  const clearInterest = async () => {
    if (!event) return;
    setWorking(true);
    const { error: clearError } = await api.rpc("clear_city_event_interest", { target_event_id: event.event_id });
    if (clearError) setError(clearError.message);
    else {
      setNotice("Etkinlik listenden çıkarıldı.");
      await loadEvent();
    }
    setWorking(false);
  };

  const addToCalendar = async () => {
    if (!event) return;
    try {
      await addEventToCalendar({
        id: event.event_id,
        title: event.title,
        description: event.description,
        starts_at: event.starts_at,
        ends_at: event.ends_at,
        location_name: event.venue_name,
        address_text: event.address_text,
        public_url: event.source_url || event.ticket_url
      });
      setNotice("Etkinlik takvime eklenmek üzere hazırlandı.");
    } catch (calendarError) {
      setError(calendarError instanceof Error ? calendarError.message : "Takvim açılamadı.");
    }
  };

  const shareEvent = async () => {
    if (!event) return;
    const url = event.source_url || event.ticket_url || "";
    await Share.share({ title: event.title, message: `${event.title}\n${formatDate(event.starts_at)} · ${event.venue_name || event.city}${url ? `\n${url}` : ""}` });
  };

  if (loading) {
    return <View style={[styles.center, { paddingTop: insets.top, paddingBottom: insets.bottom }]}><ActivityIndicator color={colors.accent} /><Text style={styles.muted}>Şehir etkinliği açılıyor...</Text></View>;
  }

  if (!event) {
    return (
      <View style={[styles.center, { paddingTop: insets.top, paddingBottom: insets.bottom }]}>
        <Text style={styles.title}>Etkinlik bulunamadı</Text>
        <Text style={styles.muted}>{error || "Etkinlik kaldırılmış veya süresi geçmiş olabilir."}</Text>
        <Link href="/(tabs)/feed" asChild><Pressable style={styles.primaryButton}><Text style={styles.primaryText}>Keşfet'e dön</Text></Pressable></Link>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={[styles.page, { paddingTop: insets.top + 16, paddingBottom: insets.bottom + 28 }]}>
      <Link href="/(tabs)/feed" asChild>
        <Pressable style={styles.backButton}><Ionicons name="arrow-back" size={18} color={colors.ink} /><Text style={styles.backText}>Şehir Radarı</Text></Pressable>
      </Link>

      <View style={styles.hero}>
        <View style={styles.heroFallback}><Ionicons name="images" size={54} color="rgba(255,255,255,0.82)" /></View>
        {event.cover_image_url ? (
          <Image source={{ uri: normalizeImageUrl(event.cover_image_url) }} style={styles.heroImage} resizeMode="cover" fadeDuration={180} />
        ) : null}
        <View style={styles.heroShade} />
        <View style={styles.heroContent}>
          <View style={styles.categoryBadge}><Text style={styles.categoryText}>{event.category}</Text></View>
          <Text style={styles.heroTitle}>{event.title}</Text>
          <Text style={styles.heroMeta}>{event.city} · {formatDate(event.starts_at)}</Text>
        </View>
      </View>

      <View style={styles.trustCard}>
        <Ionicons name="shield-checkmark" size={25} color={colors.success} />
        <View style={styles.flex}>
          <Text style={styles.trustTitle}>Küratörlü şehir etkinliği</Text>
          <Text style={styles.muted}>Bilgi kaynağı: {event.source_name}. Bilet ve program değişikliklerini resmî sayfadan doğrula.</Text>
        </View>
      </View>

      <View style={styles.panel}>
        <Text style={styles.panelTitle}>Etkinlik bilgileri</Text>
        <Info icon="calendar" label="Tarih" value={formatDate(event.starts_at)} />
        <Info icon="location" label="Mekân" value={event.venue_name || event.city} />
        <Info icon="wallet" label="Ücret" value={event.price_label || "Belirtilmedi"} />
        {event.description ? <Text style={styles.description}>{event.description}</Text> : null}
      </View>

      <View style={styles.ticketPanel}>
        <View style={styles.ticketHeadingRow}>
          <View style={styles.flex}>
            <Text style={styles.ticketKicker}>RESMÎ BİLET SATIŞI</Text>
            <Text style={styles.panelTitle}>Bilet seçeneği</Text>
          </View>
          <View style={styles.ticketIcon}><Ionicons name="ticket" size={24} color={colors.ink} /></View>
        </View>
        {ticketOffers.length > 0 ? (
          <View style={styles.offerStack}>
            {ticketOffers.map((offer) => (
              <View key={offer.offer_id} style={[styles.offerCard, offer.is_cheapest && styles.cheapestOffer]}>
                <View style={styles.offerTopRow}>
                  <View style={styles.flex}>
                    <View style={styles.sellerRow}>
                      <Text style={styles.sellerName}>{offer.seller_name}</Text>
                      {offer.is_official ? <Ionicons name="shield-checkmark" size={16} color={colors.success} /> : null}
                    </View>
                    <Text style={styles.offerPrice}>{formatOfferPrice(offer)}</Text>
                  </View>
                  {offer.is_cheapest ? <Text style={styles.cheapestBadge}>EN UCUZ</Text> : null}
                </View>
                <Text style={styles.offerMeta}>{offer.fees_included === true ? "Hizmet bedeli dahil" : "Son tutarı satıcı sayfasında doğrula"}</Text>
                <Pressable style={styles.offerButton} onPress={() => void Linking.openURL(offer.purchase_url)}>
                  <Text style={styles.offerButtonText}>{offer.seller_name}'te incele</Text>
                  <Ionicons name="open-outline" size={18} color={colors.onBrand} />
                </Pressable>
              </View>
            ))}
          </View>
        ) : (
          <Text style={styles.muted}>Bu etkinlik için doğrulanmış bir satış bağlantısı henüz bulunmuyor.</Text>
        )}
        <Text style={styles.priceDisclaimer}>Fiyatlar ve kontenjanlar değişebilir. Bialem bilet satmaz; satın alma işlemi resmî satıcının sayfasında tamamlanır.</Text>
      </View>

      <View style={styles.togetherCard}>
        <View style={styles.togetherIcon}><Ionicons name="people" size={29} color={colors.ink} /></View>
        <Text style={styles.togetherKicker}>BİRLİKTE GİT</Text>
        <Text style={styles.togetherTitle}>Bu plana yalnız gitmek zorunda değilsin.</Text>
        <Text style={styles.muted}>{event.companion_count} Bialem üyesi bu etkinlik için eşlikçi arıyor.</Text>
        {notice ? <Text style={styles.notice}>{notice}</Text> : null}
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <Pressable disabled={working} style={[styles.primaryButton, event.is_looking_for_company && styles.activeButton]} onPress={() => void toggleInterest(!event.is_looking_for_company)}>
          <Text style={styles.primaryText}>{event.is_looking_for_company ? "Birlikte Git açık" : "Birlikte gitmek istiyorum"}</Text>
        </Pressable>
        {!event.is_looking_for_company ? (
          <Pressable disabled={working} style={styles.secondaryButton} onPress={() => void toggleInterest(false)}><Text style={styles.secondaryText}>Sadece ilgileniyorum</Text></Pressable>
        ) : null}
        {event.is_interested ? <Pressable disabled={working} onPress={() => void clearInterest()}><Text style={styles.removeText}>Listemden çıkar</Text></Pressable> : null}
      </View>

      <View style={styles.actionGrid}>
        <Pressable style={styles.actionButton} onPress={() => void addToCalendar()}><Ionicons name="calendar-outline" size={23} color={colors.accent} /><Text style={styles.actionText}>Takvime ekle</Text></Pressable>
        <Pressable style={styles.actionButton} onPress={() => void shareEvent()}><Ionicons name="share-social-outline" size={23} color={colors.accent} /><Text style={styles.actionText}>Paylaş</Text></Pressable>
      </View>

    </ScrollView>
  );
}

function Info({ icon, label, value }: { icon: keyof typeof Ionicons.glyphMap; label: string; value: string }) {
  return <View style={styles.infoRow}><View style={styles.infoIcon}><Ionicons name={icon} size={19} color={colors.accent} /></View><View style={styles.flex}><Text style={styles.infoLabel}>{label}</Text><Text style={styles.infoValue}>{value}</Text></View></View>;
}

function formatDate(value: string) {
  return new Date(value).toLocaleString("tr-TR", { day: "2-digit", month: "long", weekday: "short", hour: "2-digit", minute: "2-digit" });
}

function formatOfferPrice(offer: TicketOffer) {
  if (offer.min_price !== null) {
    const currency = offer.currency || "TRY";
    const minimum = formatMoney(offer.min_price, currency);
    if (offer.max_price !== null && offer.max_price !== offer.min_price) {
      return `${minimum} - ${formatMoney(offer.max_price, currency)}`;
    }
    return `${minimum}'den başlayan`;
  }
  return offer.price_label || "Güncel fiyatı satıcıda gör";
}

function formatMoney(value: number, currency: string) {
  try {
    return new Intl.NumberFormat("tr-TR", { style: "currency", currency, maximumFractionDigits: 2 }).format(value);
  } catch {
    return `${value.toLocaleString("tr-TR")} ${currency}`;
  }
}

const styles = StyleSheet.create({
  page: { flexGrow: 1, gap: 16, paddingHorizontal: 20, backgroundColor: colors.page },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 12, padding: 24, backgroundColor: colors.page },
  backButton: { alignSelf: "flex-start", flexDirection: "row", alignItems: "center", gap: 7, paddingHorizontal: 13, paddingVertical: 9, borderRadius: 999, backgroundColor: colors.surface },
  backText: { color: colors.ink, fontWeight: "900" },
  hero: { minHeight: 310, overflow: "hidden", justifyContent: "flex-end", borderRadius: 32, backgroundColor: colors.accent },
  heroFallback: { ...StyleSheet.absoluteFillObject, alignItems: "center", justifyContent: "center" },
  heroImage: { ...StyleSheet.absoluteFillObject, width: "100%", height: "100%" },
  heroShade: { ...StyleSheet.absoluteFillObject, backgroundColor: "rgba(8,26,68,0.48)" },
  heroContent: { gap: 10, padding: 23 },
  categoryBadge: { alignSelf: "flex-start", paddingHorizontal: 11, paddingVertical: 7, borderRadius: 999, backgroundColor: colors.action },
  categoryText: { color: colors.ink, fontSize: 11, fontWeight: "900", textTransform: "uppercase" },
  heroTitle: { color: colors.onBrand, fontSize: 34, lineHeight: 39, fontWeight: "900" },
  heroMeta: { color: "#e9eeff", fontSize: 14, fontWeight: "800" },
  trustCard: { flexDirection: "row", alignItems: "flex-start", gap: 12, padding: 16, borderRadius: 22, backgroundColor: colors.surfaceStrong, borderWidth: 1, borderColor: colors.aqua },
  trustTitle: { color: colors.ink, fontSize: 14, fontWeight: "900", marginBottom: 4 },
  flex: { flex: 1 },
  panel: { gap: 13, padding: 20, borderRadius: 28, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  panelTitle: { color: colors.ink, fontSize: 22, fontWeight: "900" },
  ticketPanel: { gap: 15, padding: 20, borderRadius: 28, backgroundColor: colors.surfaceStrong, borderWidth: 1, borderColor: colors.action },
  ticketHeadingRow: { flexDirection: "row", alignItems: "center", gap: 12 },
  ticketKicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.5, marginBottom: 4 },
  ticketIcon: { width: 48, height: 48, borderRadius: 17, alignItems: "center", justifyContent: "center", backgroundColor: colors.warning },
  offerStack: { gap: 10 },
  offerCard: { gap: 10, padding: 15, borderRadius: 20, backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  cheapestOffer: { borderWidth: 2, borderColor: colors.success },
  offerTopRow: { flexDirection: "row", alignItems: "flex-start", gap: 10 },
  sellerRow: { flexDirection: "row", alignItems: "center", gap: 6 },
  sellerName: { color: colors.ink, fontSize: 17, fontWeight: "900" },
  offerPrice: { color: colors.accent, fontSize: 15, fontWeight: "900", marginTop: 5 },
  cheapestBadge: { overflow: "hidden", paddingHorizontal: 9, paddingVertical: 6, borderRadius: 999, color: colors.onBrand, backgroundColor: colors.success, fontSize: 9, fontWeight: "900" },
  offerMeta: { color: colors.muted, fontSize: 11, fontWeight: "700" },
  offerButton: { flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 7, padding: 13, borderRadius: 999, backgroundColor: colors.accent },
  offerButtonText: { color: colors.onBrand, fontSize: 13, fontWeight: "900" },
  priceDisclaimer: { color: colors.muted, fontSize: 11, lineHeight: 17 },
  infoRow: { flexDirection: "row", alignItems: "center", gap: 11 },
  infoIcon: { width: 42, height: 42, borderRadius: 14, alignItems: "center", justifyContent: "center", backgroundColor: colors.accentSoft },
  infoLabel: { color: colors.muted, fontSize: 11, fontWeight: "800" },
  infoValue: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  description: { color: colors.muted, fontSize: 15, lineHeight: 23, paddingTop: 3 },
  togetherCard: { gap: 10, padding: 21, borderRadius: 30, backgroundColor: colors.surface, borderWidth: 2, borderColor: colors.warning },
  togetherIcon: { width: 56, height: 56, borderRadius: 20, alignItems: "center", justifyContent: "center", backgroundColor: colors.warning },
  togetherKicker: { color: colors.accent, fontSize: 10, fontWeight: "900", letterSpacing: 1.5 },
  togetherTitle: { color: colors.ink, fontSize: 23, lineHeight: 29, fontWeight: "900" },
  muted: { color: colors.muted, fontSize: 13, lineHeight: 20 },
  primaryButton: { marginTop: 4, alignItems: "center", padding: 15, borderRadius: 999, backgroundColor: colors.action },
  activeButton: { backgroundColor: colors.accent },
  primaryText: { color: colors.ink, fontSize: 14, fontWeight: "900" },
  secondaryButton: { alignItems: "center", padding: 13, borderRadius: 999, backgroundColor: colors.accentSoft },
  secondaryText: { color: colors.accent, fontSize: 13, fontWeight: "900" },
  removeText: { color: colors.danger, textAlign: "center", fontSize: 12, fontWeight: "800" },
  notice: { color: colors.success, fontSize: 12, fontWeight: "800" },
  error: { color: colors.danger, fontSize: 12, fontWeight: "800" },
  title: { color: colors.ink, fontSize: 25, fontWeight: "900" },
  actionGrid: { flexDirection: "row", gap: 10 },
  actionButton: { flex: 1, alignItems: "center", gap: 7, padding: 16, borderRadius: 20, backgroundColor: colors.surface },
  actionText: { color: colors.ink, fontSize: 12, fontWeight: "900" },
});
