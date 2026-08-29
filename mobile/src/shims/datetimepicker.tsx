import { useState } from "react";
import { Platform, Pressable, Text, View } from "react-native";

export type DateTimePickerEvent = { type: string };

export default function DateTimePicker({
  value,
  onChange,
  mode,
  display,
  is24Hour,
  minimumDate
}: {
  value: Date;
  onChange?: (event: DateTimePickerEvent, date?: Date) => void;
  mode?: string;
  display?: string;
  is24Hour?: boolean;
  minimumDate?: Date;
}) {
  const [open, setOpen] = useState(true);
  if (Platform.OS !== "web" || !open) return null;
  return (
    <View>
      <input
        type={mode === "time" ? "time" : "datetime-local"}
        defaultValue={value.toISOString().slice(0, 16)}
        onChange={(event) => onChange?.({ type: "set" }, event.target.value ? new Date(event.target.value) : undefined)}
      />
      <Pressable onPress={() => setOpen(false)}>
        <Text>Kapat</Text>
      </Pressable>
    </View>
  );
}
