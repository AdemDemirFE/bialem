import { Ionicons } from "@expo/vector-icons";
import { Image, Modal, Pressable, StyleSheet, View } from "react-native";
import { colors } from "../theme/colors";

type Props = {
  visible: boolean;
  uri: string | null;
  onClose: () => void;
  onEdit?: () => void;
};

export function ImageViewerModal({ visible, uri, onClose, onEdit }: Props) {
  return (
    <Modal visible={visible} transparent animationType="fade" onRequestClose={onClose}>
      <View style={styles.backdrop}>
        <Pressable style={styles.closeArea} onPress={onClose} />
        <View style={styles.imageWrap}>
          {uri ? <Image source={{ uri }} style={styles.image} resizeMode="contain" /> : null}
        </View>
        <View style={styles.toolbar}>
          {onEdit ? (
            <Pressable style={styles.editButton} onPress={() => {
              onClose();
              onEdit();
            }}>
              <Ionicons name="create-outline" size={20} color={colors.actionText} />
              <Ionicons name="camera" size={20} color={colors.actionText} />
            </Pressable>
          ) : null}
          <Pressable style={styles.closeButton} onPress={onClose}>
            <Ionicons name="close" size={26} color={colors.ink} />
          </Pressable>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.92)",
    justifyContent: "center",
    alignItems: "center"
  },
  closeArea: {
    ...StyleSheet.absoluteFillObject
  },
  imageWrap: {
    width: "100%",
    height: "80%",
    zIndex: 1
  },
  image: {
    width: "100%",
    height: "100%"
  },
  toolbar: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingHorizontal: 18,
    paddingTop: 48,
    paddingBottom: 12,
    zIndex: 2
  },
  editButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 999,
    backgroundColor: colors.action
  },
  closeButton: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.surface
  }
});
