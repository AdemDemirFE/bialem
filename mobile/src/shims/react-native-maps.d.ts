declare module "react-native-maps" {
  import type { ComponentType, ReactNode } from "react";

  export type MapPressEvent = { nativeEvent: { coordinate: { latitude: number; longitude: number } } };
  export type MarkerDragStartEndEvent = { nativeEvent: { coordinate: { latitude: number; longitude: number } } };

  export interface MapViewProps {
    style?: any;
    initialRegion?: { latitude: number; longitude: number; latitudeDelta: number; longitudeDelta: number };
    region?: { latitude: number; longitude: number; latitudeDelta: number; longitudeDelta: number };
    showsUserLocation?: boolean;
    showsMyLocationButton?: boolean;
    onPress?: (event: MapPressEvent) => void;
    children?: ReactNode;
  }
  export const MapView: ComponentType<MapViewProps>;
  export default MapView;
  export const Marker: ComponentType<{ coordinate: { latitude: number; longitude: number }; title?: string; description?: string; draggable?: boolean; onDragEnd?: (event: MarkerDragStartEndEvent) => void }>;
}
