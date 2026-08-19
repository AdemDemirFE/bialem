import DateTimePicker, { type DateTimePickerEvent } from "@react-native-community/datetimepicker";
import { Ionicons } from "@expo/vector-icons";
import * as Location from "expo-location";
import { Stack, useLocalSearchParams } from "expo-router";
import { useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  Linking,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { SafeAreaView, useSafeAreaInsets } from "react-native-safe-area-context";
import {
  EventLocationMap,
  type MapCoordinate
} from "../src/components/EventLocationMap";
import { ImagePickerField } from "../src/components/ImagePickerField";
import { useAuth } from "../src/lib/auth";
import {
  removeUploadedImage,
  uploadEventCover,
  type PickedImage
} from "../src/lib/storage";
import { api } from "../src/lib/api";
import { colors } from "../src/theme/colors";

type CommunityOption = {
  id: string;
  name: string;
  slug: string;
  creation_mode: "direct" | "proposal";
};

type DateTarget = "start" | "end";
type PickerMode = "date" | "time";
const DEFAULT_MAP_COORDINATE: MapCoordinate = {
  latitude: 39.9334,
  longitude: 32.8597
};

function nextAvailableStart() {
  const date = new Date(Date.now() + 60 * 60 * 1000);
  date.setMinutes(0, 0, 0);
  return date;
}

function formatSelectedDate(value: Date | null) {
  if (!value) return "Tarih ve saat seçin";
  return value.toLocaleString("tr-TR", {
    weekday: "short",
    day: "2-digit",
    month: "long",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
}

function formatMapAddress(address: Location.LocationGeocodedAddress) {
  return Array.from(new Set([
    address.name,
    address.street,
    address.district,
    address.city,
    address.region
  ].filter((part): part is string => Boolean(part?.trim())))).join(", ");
}

export default function OrganizerRequestScreen() {
  const { groupId: initialGroupId } = useLocalSearchParams<{ groupId?: string }>();
  const { user, profile } = useAuth();
  const insets = useSafeAreaInsets();
  const [communities, setCommunities] = useState<CommunityOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [communityId, setCommunityId] = useState("");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [coverImage, setCoverImage] = useState<PickedImage | null>(null);
  const [startsAt, setStartsAt] = useState<Date | null>(null);
  const [endsAt, setEndsAt] = useState<Date | null>(null);
  const [dateTarget, setDateTarget] = useState<DateTarget | null>(null);
  const [pickerMode, setPickerMode] = useState<PickerMode>("date");
  const [locationName, setLocationName] = useState("");
  const [addressText, setAddressText] = useState("");
  const [selectedCoordinate, setSelectedCoordinate] = useState<MapCoordinate | null>(null);
  const [draftCoordinate, setDraftCoordinate] = useState<MapCoordinate>(DEFAULT_MAP_COORDINATE);
  const [draftAddress, setDraftAddress] = useState("");
  const [mapVisible, setMapVisible] = useState(false);
  const [mapLoading, setMapLoading] = useState(false);
  const [mapError, setMapError] = useState<string | null>(null);
  const [mapSession, setMapSession] = useState(0);
  const [mapSearch, setMapSearch] = useState("");
  const [mapSearching, setMapSearching] = useState(false);
  const [capacity, setCapacity] = useState("");

  useEffect(() => {
    let active = true;

    const loadCommunities = async () => {
      setLoading(true);
      if (!user) {
        setCommunities([]);
        setLoading(false);
        return;
      }

      const groupsResult = await api.rpc("get_my_event_creation_groups");

      if (!active) {
        return;
      }

      if (groupsResult.error) {
        setError(groupsResult.error.message || "Gruplar yüklenemedi.");
      } else {
        const nextCommunities = (groupsResult.data ?? []) as CommunityOption[];
        setCommunities(nextCommunities);
        const requestedGroup = nextCommunities.find((group) => group.id === initialGroupId);
        if (requestedGroup) {
          setCommunityId(requestedGroup.id);
        } else if (nextCommunities[0]) {
          setCommunityId(nextCommunities[0].id);
        }
      }

      setLoading(false);
    };

    void loadCommunities();

    return () => {
      active = false;
    };
  }, [user?.id, initialGroupId]);

  const selectedCommunity = useMemo(
    () => communities.find((community) => community.id === communityId) ?? null,
    [communities, communityId]
  );
  const createsDirectly = selectedCommunity?.creation_mode === "direct";

  const openDatePicker = (target: DateTarget) => {
    setDateTarget(target);
    setPickerMode("date");
  };

  const setTargetDate = (target: DateTarget, value: Date) => {
    if (target === "start") {
      setStartsAt(value);
      if (endsAt && endsAt <= value) setEndsAt(new Date(value.getTime() + 2 * 60 * 60 * 1000));
    } else {
      setEndsAt(value);
    }
  };

  const handleDateChange = (event: DateTimePickerEvent, selected?: Date) => {
    if (!dateTarget) return;
    if (event.type === "dismissed" || !selected) {
      setDateTarget(null);
      return;
    }

    if (Platform.OS === "ios") {
      setTargetDate(dateTarget, selected);
      setDateTarget(null);
      return;
    }

    const current = dateTarget === "start"
      ? startsAt ?? nextAvailableStart()
      : endsAt ?? new Date((startsAt ?? nextAvailableStart()).getTime() + 2 * 60 * 60 * 1000);
    const combined = new Date(current);

    if (pickerMode === "date") {
      combined.setFullYear(selected.getFullYear(), selected.getMonth(), selected.getDate());
      setTargetDate(dateTarget, combined);
      setPickerMode("time");
      return;
    }

    combined.setHours(selected.getHours(), selected.getMinutes(), 0, 0);
    setTargetDate(dateTarget, combined);
    setDateTarget(null);
  };

  const openLocationInMaps = async () => {
    const query = [locationName.trim(), addressText.trim()].filter(Boolean).join(", ");
    if (!query) {
      setError("Google Maps'te aramak için mekân adı veya adres yazın.");
      return;
    }

    setError(null);
    try {
      await Linking.openURL(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(query)}`);
    } catch {
      setError("Google Maps açılamadı. Internet bağlantınızı kontrol edip tekrar deneyin.");
    }
  };

  const resolveAddress = async (coordinate: MapCoordinate) => {
    try {
      const addresses = await Location.reverseGeocodeAsync(coordinate);
      const resolved = addresses[0] ? formatMapAddress(addresses[0]) : "";
      setDraftAddress(resolved);
      setMapError(resolved ? null : "Bu nokta için adres bulunamadı. Konumu yine de kaydedebilirsiniz.");
    } catch {
      setDraftAddress("");
      setMapError("Adres bilgisi alınamadı. Konumu yine de kaydedebilirsiniz.");
    }
  };

  const selectDraftCoordinate = (coordinate: MapCoordinate) => {
    setDraftCoordinate(coordinate);
    setMapLoading(true);
    void resolveAddress(coordinate).finally(() => setMapLoading(false));
  };

  const searchMapLocation = async () => {
    const query = mapSearch.trim();
    if (!query || mapSearching) return;

    setMapSearching(true);
    setMapError(null);
    try {
      const results = await Location.geocodeAsync(query);
      const match = results[0];
      if (!match) {
        setMapError("Aradığınız mekân veya adres bulunamadı.");
        return;
      }

      const coordinate = { latitude: match.latitude, longitude: match.longitude };
      setDraftCoordinate(coordinate);
      setMapSession((value) => value + 1);
      await resolveAddress(coordinate);
    } catch {
      setMapError("Mekân araması tamamlanamadı. Bağlantınızı kontrol edip tekrar deneyin.");
    } finally {
      setMapSearching(false);
    }
  };

  const openMapPicker = async () => {
    const initialCoordinate = selectedCoordinate ?? DEFAULT_MAP_COORDINATE;
    setDraftCoordinate(initialCoordinate);
    setDraftAddress(selectedCoordinate ? addressText : "");
    setMapSearch(locationName || addressText || "");
    setMapError(null);
    setMapSession((value) => value + 1);
    setMapVisible(true);

    if (selectedCoordinate) return;

    setMapLoading(true);
    try {
      const permission = await Location.requestForegroundPermissionsAsync();
      if (permission.status !== "granted") {
        setMapError("Konum izni verilmedi. Haritadan konumu elle seçebilirsiniz.");
        return;
      }

      const current = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.Balanced
      });
      const coordinate = {
        latitude: current.coords.latitude,
        longitude: current.coords.longitude
      };
      setDraftCoordinate(coordinate);
      setMapSession((value) => value + 1);
      await resolveAddress(coordinate);
    } catch {
      setMapError("Mevcut konum alınamadı. Haritadan konumu elle seçebilirsiniz.");
    } finally {
      setMapLoading(false);
    }
  };

  const confirmMapLocation = () => {
    setSelectedCoordinate(draftCoordinate);
    if (draftAddress) {
      setAddressText(draftAddress);
      if (!locationName.trim()) {
        setLocationName(draftAddress.split(",")[0]?.trim() || "Haritadan seçilen konum");
      }
    }
    setMapVisible(false);
    setError(null);
  };

  const handleSubmit = async () => {
    if (!user || !profile) {
      setError("Etkinlik oluşturmak için aktif oturum gerekir.");
      return;
    }

    if (!communityId || !title.trim() || !startsAt) {
      setError("Grup, başlık ve başlangıç tarihi zorunludur.");
      return;
    }

    if (startsAt.getTime() <= Date.now()) {
      setError("Başlangıç tarihi gelecekte olmalıdır.");
      return;
    }

    if (endsAt && endsAt <= startsAt) {
      setError("Bitiş tarihi başlangıç tarihinden sonra olmalıdır.");
      return;
    }

    setSubmitting(true);
    setError(null);
    setNotice(null);

    let uploadedCover: Awaited<ReturnType<typeof uploadEventCover>> | null = null;
    try {
      if (coverImage) {
        uploadedCover = await uploadEventCover({ userId: user.id, image: coverImage });
      }
    } catch (uploadError) {
      setError(uploadError instanceof Error ? uploadError.message : "Kapak görseli yüklenemedi.");
      setSubmitting(false);
      return;
    }

    const { data, error } = await api.rpc("create_group_event", {
      target_community_id: communityId,
      target_title: title.trim(),
      target_description: description.trim() || null,
      target_starts_at: startsAt.toISOString(),
      target_ends_at: endsAt?.toISOString() ?? null,
      target_location_name: locationName.trim() || null,
      target_address_text: addressText.trim() || null,
      target_latitude: selectedCoordinate?.latitude ?? null,
      target_longitude: selectedCoordinate?.longitude ?? null,
      target_capacity: capacity.trim() ? Number(capacity) : null,
      target_cover_image_url: uploadedCover?.storagePath ?? null
    });

    if (error) {
      if (uploadedCover) {
        await removeUploadedImage("event-covers", uploadedCover.bucketPath).catch(() => undefined);
      }
      setError(error.message);
      setSubmitting(false);
      return;
    }

    const result = (data ?? [])[0] as { event_status?: string; creation_mode?: "direct" | "proposal" } | undefined;
    setNotice(
      result?.creation_mode === "proposal"
        ? "Etkinlik öneriniz grup moderatörünün onayına gönderildi."
        : result?.event_status === "published"
          ? "Etkinlik oluşturuldu ve yayınlandı."
          : "Etkinlik oluşturuldu. Yeni partner güven kontrolünden sonra yayınlanacak."
    );
    setTitle("");
    setDescription("");
    setCoverImage(null);
    setStartsAt(null);
    setEndsAt(null);
    setLocationName("");
    setAddressText("");
    setSelectedCoordinate(null);
    setCapacity("");
    setSubmitting(false);
  };

  return (
    <>
      <Stack.Screen options={{ headerShown: true, title: createsDirectly ? "Etkinlik Oluştur" : "Etkinlik Öner" }} />
      <ScrollView contentContainerStyle={styles.page}>
        <View style={styles.panel}>
        <Text style={styles.kicker}>{createsDirectly ? "ETKİNLİK OLUŞTUR" : "ETKİNLİK ÖNER"}</Text>
        <Text style={styles.title}>
          {createsDirectly ? "Grubun için doğrudan yeni bir etkinlik oluştur." : "Grubun için yeni bir etkinlik önerisi gönder."}
        </Text>
        <Text style={styles.text}>
          {createsDirectly
            ? "Moderatör olarak grup onayına ihtiyaç duymadan etkinlik oluşturabilirsin. Yeni partner topluluklarda yalnızca Bialem güven kontrolü uygulanır."
            : "Öneri önce kaynak grup moderatörüne gider. Onaylandıktan sonra ortak Keşfet havuzunda yayına çıkar."}
        </Text>
      </View>

      <View style={styles.panel}>
        <Text style={styles.sectionTitle}>{createsDirectly ? "Etkinlik formu" : "Öneri formu"}</Text>
        {error ? <Text style={styles.errorText}>{error}</Text> : null}
        {notice ? <Text style={styles.noticeText}>{notice}</Text> : null}

        {loading ? (
          <View style={styles.loadingRow}>
            <ActivityIndicator color={colors.accent} />
            <Text style={styles.loadingText}>Topluluklar yükleniyor...</Text>
          </View>
        ) : communities.length === 0 ? (
          <View style={styles.emptyBox}>
            <Text style={styles.emptyTitle}>Katıldığın bir grup yok</Text>
            <Text style={styles.emptyText}>
              Etkinlik önerebilmek için Topluluklar sekmesinden önce bir ana topluluğa, sonra bir gruba katıl.
            </Text>
          </View>
        ) : (
          <>
            <Text style={styles.fieldLabel}>Grup seçin</Text>
            <View style={styles.choiceList}>
              {communities.map((community) => (
                <Pressable
                  key={community.id}
                  style={[styles.choiceChip, community.id === communityId && styles.choiceChipActive]}
                  onPress={() => setCommunityId(community.id)}
                >
                  <Text style={[styles.choiceText, community.id === communityId && styles.choiceTextActive]}>
                    {community.name}
                  </Text>
                </Pressable>
              ))}
            </View>

            <Field label="Etkinlik başlığı" value={title} onChangeText={setTitle} placeholder="Örnek: Açık hava fotoğraf yürüyüşü" />
            <Field
              label="Açıklama"
              value={description}
              onChangeText={setDescription}
              placeholder="Etkinliğin amacı, akışı, kimler için uygun olduğu"
              multiline
            />
            <ImagePickerField
              image={coverImage}
              onChange={setCoverImage}
              onError={setError}
              disabled={submitting}
              label="Etkinlik kapak görseli"
            />
            <DateField label="Başlangıç tarihi ve saati" value={startsAt} required onPress={() => openDatePicker("start")} />
            <DateField
              label="Bitiş tarihi ve saati"
              value={endsAt}
              onPress={() => openDatePicker("end")}
              onClear={endsAt ? () => setEndsAt(null) : undefined}
            />
            <Field label="Mekân adı" value={locationName} onChangeText={setLocationName} placeholder="Moda Sahil" />
            <Field label="Adres" value={addressText} onChangeText={setAddressText} placeholder="Çankaya / Ankara" />
            <View style={styles.mapsBox}>
              <View style={styles.mapsCopy}>
                <View style={styles.mapsIcon}><Ionicons name="location" size={20} color={colors.onBrand} /></View>
                <View style={styles.mapsTextWrap}>
                  <Text style={styles.mapsTitle}>
                    {selectedCoordinate ? "Harita konumu seçildi" : "Mekânı haritadan seçin"}
                  </Text>
                  <Text style={styles.mapsHint}>
                    {selectedCoordinate
                      ? `${selectedCoordinate.latitude.toFixed(5)}, ${selectedCoordinate.longitude.toFixed(5)}`
                      : "Haritaya dokunun veya işaretçiyi sürükleyin."}
                  </Text>
                </View>
              </View>
              <Pressable style={styles.mapsButton} onPress={() => void openMapPicker()}>
                <Ionicons name="map-outline" size={19} color={colors.actionText} />
                <Text style={styles.mapsButtonText}>
                  {selectedCoordinate ? "Harita konumunu değiştir" : "Haritadan seç"}
                </Text>
              </Pressable>
              <Pressable style={styles.mapsSecondaryButton} onPress={() => void openLocationInMaps()}>
                <Ionicons name="search-outline" size={18} color={colors.ink} />
                <Text style={styles.mapsSecondaryButtonText}>Yazılan mekânı Google Maps'te ara</Text>
              </Pressable>
            </View>
            <Field label="Katılım limiti" value={capacity} onChangeText={setCapacity} placeholder="30" keyboardType="numeric" />

            {dateTarget ? (
              <DateTimePicker
                key={`${dateTarget}-${Platform.OS === "ios" ? "datetime" : pickerMode}`}
                value={dateTarget === "start"
                  ? startsAt ?? nextAvailableStart()
                  : endsAt ?? new Date((startsAt ?? nextAvailableStart()).getTime() + 2 * 60 * 60 * 1000)}
                mode={Platform.OS === "ios" ? "datetime" : pickerMode}
                display={Platform.OS === "ios" ? "compact" : "default"}
                is24Hour
                minimumDate={pickerMode === "date" ? (dateTarget === "end" && startsAt ? startsAt : new Date()) : undefined}
                onChange={handleDateChange}
              />
            ) : null}

            {selectedCommunity ? (
              <Text style={styles.helperText}>
                {createsDirectly ? (
                  <>Etkinlik <Text style={styles.helperStrong}>{selectedCommunity.name}</Text> grubunda doğrudan oluşturulacak.</>
                ) : (
                  <>Öneri <Text style={styles.helperStrong}>{selectedCommunity.name}</Text> grup moderatörüne gönderilecek.</>
                )}
              </Text>
            ) : null}

            <Pressable style={[styles.primaryButton, submitting && styles.disabledButton]} onPress={() => void handleSubmit()}>
              <Text style={styles.primaryButtonText}>
                {submitting ? "Kaydediliyor..." : createsDirectly ? "Etkinliği oluştur" : "Öneriyi gönder"}
              </Text>
            </Pressable>
          </>
        )}
        </View>
      </ScrollView>
      <Modal visible={mapVisible} animationType="slide" onRequestClose={() => setMapVisible(false)}>
        <SafeAreaView style={styles.mapModal} edges={["top"]}>
          <View style={styles.mapHeader}>
            <Pressable accessibilityLabel="Haritayi kapat" style={styles.mapHeaderButton} onPress={() => setMapVisible(false)}>
              <Ionicons name="close" size={24} color={colors.ink} />
            </Pressable>
            <View style={styles.mapHeaderCopy}>
              <Text style={styles.mapHeaderTitle}>Mekânı seçin</Text>
            <Text style={styles.mapHeaderHint}>Haritaya dokunun veya işaretçiyi sürükleyin.</Text>
            </View>
            <View style={styles.mapHeaderButton} />
          </View>

          <View style={styles.mapSearchBar}>
            <Ionicons name="search" size={20} color={colors.muted} />
            <TextInput
              value={mapSearch}
              onChangeText={setMapSearch}
              placeholder="Mekân veya adres ara"
              placeholderTextColor={colors.muted}
              returnKeyType="search"
              style={styles.mapSearchInput}
              onSubmitEditing={() => void searchMapLocation()}
            />
            <Pressable
              style={[styles.mapSearchButton, (!mapSearch.trim() || mapSearching) && styles.disabledButton]}
              disabled={!mapSearch.trim() || mapSearching}
              onPress={() => void searchMapLocation()}
            >
              {mapSearching ? <ActivityIndicator color={colors.actionText} /> : <Text style={styles.mapSearchButtonText}>Ara</Text>}
            </Pressable>
          </View>

          <View style={styles.mapCanvas}>
            <EventLocationMap
              coordinate={draftCoordinate}
              session={mapSession}
              onChange={selectDraftCoordinate}
            />
            {mapLoading ? (
              <View style={styles.mapLoading}>
                <ActivityIndicator color={colors.accent} />
                <Text style={styles.mapLoadingText}>Konum bilgisi alınıyor...</Text>
              </View>
            ) : null}
          </View>

          <View
            style={[
              styles.mapFooter,
              { paddingBottom: Math.max(22, insets.bottom + 12) }
            ]}
          >
            <View style={styles.mapAddressBox}>
              <Ionicons name="location" size={22} color={colors.accent} />
              <View style={styles.mapAddressCopy}>
                <Text style={styles.mapAddressTitle}>Seçilen konum</Text>
                <Text style={styles.mapAddressText}>
                  {draftAddress || `${draftCoordinate.latitude.toFixed(6)}, ${draftCoordinate.longitude.toFixed(6)}`}
                </Text>
              </View>
            </View>
            {mapError ? <Text style={styles.mapErrorText}>{mapError}</Text> : null}
            <Pressable style={styles.mapConfirmButton} onPress={confirmMapLocation}>
              <Ionicons name="checkmark-circle" size={20} color={colors.actionText} />
              <Text style={styles.mapsButtonText}>Bu konumu kullan</Text>
            </Pressable>
          </View>
        </SafeAreaView>
      </Modal>
    </>
  );
}

function DateField({
  label,
  value,
  required = false,
  onPress,
  onClear
}: {
  label: string;
  value: Date | null;
  required?: boolean;
  onPress: () => void;
  onClear?: () => void;
}) {
  return (
    <View style={styles.fieldGroup}>
      <Text style={styles.fieldLabel}>{label}{required ? " *" : ""}</Text>
      <View style={styles.dateRow}>
        <Pressable style={[styles.dateButton, value && styles.dateButtonSelected]} onPress={onPress}>
          <Ionicons name="calendar-outline" size={21} color={value ? colors.accent : colors.muted} />
          <View style={styles.dateCopy}>
            <Text style={[styles.dateValue, !value && styles.datePlaceholder]}>{formatSelectedDate(value)}</Text>
            <Text style={styles.dateHint}>Değiştirmek için dokunun</Text>
          </View>
          <Ionicons name="chevron-forward" size={18} color={colors.muted} />
        </Pressable>
        {onClear ? (
          <Pressable accessibilityLabel="Bitiş tarihini temizle" style={styles.clearDateButton} onPress={onClear}>
            <Ionicons name="close" size={20} color={colors.danger} />
          </Pressable>
        ) : null}
      </View>
    </View>
  );
}

type FieldProps = {
  label: string;
  value: string;
  onChangeText: (value: string) => void;
  placeholder: string;
  multiline?: boolean;
  keyboardType?: "default" | "numeric";
};

function Field({ label, multiline = false, ...props }: FieldProps) {
  return (
    <View style={styles.fieldGroup}>
      <Text style={styles.fieldLabel}>{label}</Text>
      <TextInput
        {...props}
        style={[styles.input, multiline && styles.textArea]}
        multiline={multiline}
        placeholderTextColor="#7d877d"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  page: {
    flexGrow: 1,
    minHeight: "100%",
    backgroundColor: colors.page,
    padding: 24,
    gap: 20
  },
  panel: {
    backgroundColor: colors.surface,
    borderRadius: 24,
    padding: 20,
    gap: 12,
    borderWidth: 1,
    borderColor: colors.border
  },
  kicker: {
    color: colors.accent,
    fontSize: 14,
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: 1.2
  },
  title: {
    color: colors.ink,
    fontSize: 30,
    lineHeight: 36,
    fontWeight: "800"
  },
  text: {
    color: colors.muted,
    fontSize: 16,
    lineHeight: 24
  },
  sectionTitle: {
    color: colors.ink,
    fontSize: 22,
    fontWeight: "800"
  },
  errorText: {
    color: colors.danger,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600"
  },
  noticeText: {
    color: colors.accent,
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "600",
    backgroundColor: colors.surfaceStrong,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 10
  },
  loadingRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10
  },
  loadingText: {
    color: colors.muted,
    fontSize: 15
  },
  emptyBox: {
    backgroundColor: colors.surfaceStrong,
    borderRadius: 18,
    padding: 16,
    gap: 8
  },
  emptyTitle: {
    color: colors.ink,
    fontSize: 18,
    fontWeight: "800"
  },
  emptyText: {
    color: colors.muted,
    fontSize: 15,
    lineHeight: 22
  },
  choiceList: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10
  },
  choiceChip: {
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    paddingHorizontal: 14,
    paddingVertical: 10
  },
  choiceChipActive: {
    backgroundColor: colors.action,
    borderColor: colors.action
  },
  choiceText: {
    color: colors.ink,
    fontWeight: "700"
  },
  choiceTextActive: {
    color: colors.actionText
  },
  fieldGroup: {
    gap: 8
  },
  fieldLabel: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "700"
  },
  input: {
    minHeight: 52,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    paddingHorizontal: 14,
    paddingVertical: 12,
    color: colors.ink,
    fontSize: 16
  },
  dateRow: {
    flexDirection: "row",
    alignItems: "stretch",
    gap: 8
  },
  dateButton: {
    flex: 1,
    minHeight: 64,
    flexDirection: "row",
    alignItems: "center",
    gap: 11,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surfaceStrong,
    paddingHorizontal: 14,
    paddingVertical: 10
  },
  dateButtonSelected: {
    borderColor: colors.accent
  },
  dateCopy: {
    flex: 1,
    gap: 3
  },
  dateValue: {
    color: colors.ink,
    fontSize: 14,
    lineHeight: 19,
    fontWeight: "800"
  },
  datePlaceholder: {
    color: colors.muted,
    fontWeight: "600"
  },
  dateHint: {
    color: colors.muted,
    fontSize: 11
  },
  clearDateButton: {
    width: 48,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  mapsBox: {
    gap: 12,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.aqua,
    backgroundColor: colors.surfaceStrong,
    padding: 14
  },
  mapsCopy: {
    flexDirection: "row",
    alignItems: "center",
    gap: 11
  },
  mapsIcon: {
    width: 40,
    height: 40,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 14,
    backgroundColor: colors.accent
  },
  mapsTextWrap: {
    flex: 1,
    gap: 3
  },
  mapsTitle: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "900"
  },
  mapsHint: {
    color: colors.muted,
    fontSize: 12,
    lineHeight: 17
  },
  mapsButton: {
    minHeight: 46,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    borderRadius: 999,
    backgroundColor: colors.action
  },
  mapsButtonText: {
    color: colors.actionText,
    fontSize: 14,
    fontWeight: "900"
  },
  mapsSecondaryButton: {
    minHeight: 42,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    borderRadius: 999,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  mapsSecondaryButtonText: {
    color: colors.ink,
    fontSize: 13,
    fontWeight: "800"
  },
  mapModal: {
    flex: 1,
    backgroundColor: colors.surface
  },
  mapHeader: {
    minHeight: 76,
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  mapHeaderButton: {
    width: 44,
    height: 44,
    alignItems: "center",
    justifyContent: "center"
  },
  mapHeaderCopy: {
    flex: 1,
    alignItems: "center",
    gap: 2
  },
  mapHeaderTitle: {
    color: colors.ink,
    fontSize: 18,
    fontWeight: "900"
  },
  mapHeaderHint: {
    color: colors.muted,
    fontSize: 12,
    textAlign: "center"
  },
  mapSearchBar: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
    backgroundColor: colors.surface
  },
  mapSearchInput: {
    flex: 1,
    minHeight: 46,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.page,
    color: colors.ink,
    paddingHorizontal: 14
  },
  mapSearchButton: {
    minWidth: 64,
    minHeight: 46,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 16,
    backgroundColor: colors.action
  },
  mapSearchButtonText: {
    color: colors.actionText,
    fontWeight: "900"
  },
  mapCanvas: {
    flex: 1,
    backgroundColor: colors.surfaceStrong
  },
  mapLoading: {
    position: "absolute",
    top: 16,
    alignSelf: "center",
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    borderRadius: 999,
    backgroundColor: colors.surface,
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderWidth: 1,
    borderColor: colors.border
  },
  mapLoadingText: {
    color: colors.ink,
    fontSize: 12,
    fontWeight: "700"
  },
  mapFooter: {
    gap: 10,
    padding: 16,
    paddingBottom: 22,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    backgroundColor: colors.surface
  },
  mapAddressBox: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 10,
    borderRadius: 16,
    backgroundColor: colors.surfaceStrong,
    padding: 14
  },
  mapAddressCopy: {
    flex: 1,
    gap: 3
  },
  mapAddressTitle: {
    color: colors.ink,
    fontSize: 13,
    fontWeight: "900"
  },
  mapAddressText: {
    color: colors.muted,
    fontSize: 13,
    lineHeight: 19
  },
  mapErrorText: {
    color: colors.danger,
    fontSize: 12,
    lineHeight: 17
  },
  mapConfirmButton: {
    minHeight: 50,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    borderRadius: 999,
    backgroundColor: colors.action
  },
  textArea: {
    minHeight: 120,
    textAlignVertical: "top"
  },
  helperText: {
    color: colors.muted,
    fontSize: 14,
    lineHeight: 20
  },
  helperStrong: {
    color: colors.ink,
    fontWeight: "700"
  },
  primaryButton: {
    marginTop: 6,
    backgroundColor: colors.action,
    borderRadius: 999,
    paddingVertical: 15,
    paddingHorizontal: 18
  },
  disabledButton: {
    opacity: 0.7
  },
  primaryButtonText: {
    color: colors.actionText,
    textAlign: "center",
    fontSize: 16,
    fontWeight: "800"
  }
});
