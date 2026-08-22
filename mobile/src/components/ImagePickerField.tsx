import { Ionicons } from "@expo/vector-icons";
import { Image, Pressable, StyleSheet, Text, View } from "react-native";
import {
  pickImageFromLibrary,
  requestMediaLibraryPermission,
  type PickedImage
} from "../lib/storage";
import { colors } from "../theme/colors";

type ImagePickerFieldProps = {
  image: PickedImage | null;
  onChange: (image: PickedImage | null) => void;
  onError: (message: string) => void;
  disabled?: boolean;
  label?: string;
};

export function ImagePickerField({
  image,
  onChange,
  onError,
  disabled = false,
  label = "Kapak görseli"
}: ImagePickerFieldProps) {
  async function chooseImage() {
    try {
      const permissionGranted = await requestMediaLibraryPermission();
      if (!permissionGranted) {
        onError("Kapak görseli seçmek için fotoğraf arşivi izni vermelisin.");
        return;
      }

      const selected = await pickImageFromLibrary();
      if (selected) onChange(selected);
    } catch {
      onError("Görsel seçilemedi. Lütfen tekrar deneyin.");
    }
  }

  return (
    <View style={styles.field}>
      <Text style={styles.label}>{label}</Text>
      {image ? (
        <View style={styles.preview}>
          <Image source={{ uri: image.uri }} style={styles.image} resizeMode="cover" />
          <View style={styles.previewFooter}>
            <Text numberOfLines={1} style={styles.fileName}>{image.fileName}</Text>
            <Pressable
              accessibilityLabel="Seçilen kapak görselini kaldır"
              disabled={disabled}
              style={styles.removeButton}
              onPress={() => onChange(null)}
            >
              <Ionicons name="trash-outline" size={18} color={colors.danger} />
            </Pressable>
          </View>
        </View>
      ) : null}
      <Pressable
        disabled={disabled}
        style={[styles.pickButton, disabled && styles.disabled]}
        onPress={() => void chooseImage()}
      >
        <Ionicons name="images-outline" size={20} color={colors.ink} />
        <Text style={styles.pickButtonText}>{image ? "Başka görsel seç" : "Galeriden görsel seç"}</Text>
      </Pressable>
      <Text style={styles.hint}>JPEG, PNG veya WebP · En fazla 5 MB</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  field: {
    gap: 8
  },
  label: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "800"
  },
  preview: {
    overflow: "hidden",
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 18,
    backgroundColor: colors.surfaceStrong
  },
  image: {
    width: "100%",
    height: 170
  },
  previewFooter: {
    minHeight: 46,
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingLeft: 13
  },
  fileName: {
    flex: 1,
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700"
  },
  removeButton: {
    width: 46,
    height: 46,
    alignItems: "center",
    justifyContent: "center"
  },
  pickButton: {
    minHeight: 44,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    backgroundColor: colors.surfaceStrong
  },
  pickButtonText: {
    color: colors.ink,
    fontSize: 14,
    fontWeight: "900"
  },
  hint: {
    color: colors.muted,
    fontSize: 12
  },
  disabled: {
    opacity: 0.6
  }
});
