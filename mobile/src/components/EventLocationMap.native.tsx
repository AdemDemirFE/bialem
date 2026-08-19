import { StyleSheet } from "react-native";
import MapView, {
  Marker,
  type MapPressEvent,
  type MarkerDragStartEndEvent
} from "react-native-maps";

export type MapCoordinate = {
  latitude: number;
  longitude: number;
};

type EventLocationMapProps = {
  coordinate: MapCoordinate;
  session: number;
  onChange: (coordinate: MapCoordinate) => void;
};

export function EventLocationMap({ coordinate, session, onChange }: EventLocationMapProps) {
  return (
    <MapView
      key={session}
      style={StyleSheet.absoluteFill}
      initialRegion={{
        ...coordinate,
        latitudeDelta: 0.025,
        longitudeDelta: 0.025
      }}
      showsUserLocation
      showsMyLocationButton
      onPress={(event: MapPressEvent) => onChange(event.nativeEvent.coordinate)}
    >
      <Marker
        coordinate={coordinate}
        draggable
        onDragEnd={(event: MarkerDragStartEndEvent) => onChange(event.nativeEvent.coordinate)}
      />
    </MapView>
  );
}
