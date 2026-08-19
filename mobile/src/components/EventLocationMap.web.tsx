import { StyleSheet, Text, View } from "react-native";
import { colors } from "../theme/colors";

export type MapCoordinate = {
  latitude: number;
  longitude: number;
};

type EventLocationMapProps = {
  coordinate: MapCoordinate;
  session: number;
  onChange: (coordinate: MapCoordinate) => void;
};

export function EventLocationMap({ coordinate }: EventLocationMapProps) {
  return (
    <View style={styles.placeholder}>
      <Text style={styles.title}>Harita seçimi mobil uygulamada kullanılabilir.</Text>
      <Text style={styles.text}>
        {coordinate.latitude.toFixed(5)}, {coordinate.longitude.toFixed(5)}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  placeholder: {
    ...StyleSheet.absoluteFillObject,
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    padding: 24,
    backgroundColor: colors.surfaceStrong
  },
  title: {
    color: colors.ink,
    fontSize: 16,
    fontWeight: "900",
    textAlign: "center"
  },
  text: {
    color: colors.muted,
    fontSize: 13,
    textAlign: "center"
  }
});
