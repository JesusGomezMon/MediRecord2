import React, { useState } from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { Screen } from "../ui/Screen";
import { Card } from "../ui/Card";
import { TextField } from "../ui/TextField";
import { PrimaryButton } from "../ui/PrimaryButton";
import { palette, spacing, typography } from "../theme/theme";
import { useStore } from "../state/useStore";

export const RemindersScreen: React.FC = () => {
  const { reminders, meds, addReminder, removeReminder } = useStore();
  const [label, setLabel] = useState("Mañana 08:00");
  const [time, setTime] = useState("08:00");
  const [medicationId, setMedicationId] = useState<number | undefined>(undefined);

  const onAdd = async () => {
    if (!label.trim() || !time.trim()) return;
    await addReminder({ label, time, medicationId });
    setLabel("Mañana 08:00");
  };

  return (
    <Screen>
      <Card elevated>
        <Text style={[typography.h2, styles.title]}>Nuevo recordatorio</Text>
        <TextField label="Título" value={label} onChangeText={setLabel} />
        <TextField label="Hora" value={time} onChangeText={setTime} placeholder="HH:mm" />
        <TextField
          label="ID de medicina (opcional)"
          value={medicationId ? String(medicationId) : ""}
          onChangeText={(val) => setMedicationId(val ? Number(val) : undefined)}
          keyboardType="numeric"
          placeholder="Relaciona con una medicina"
        />
        <PrimaryButton label="Guardar" onPress={onAdd} />
        {meds.length > 0 ? (
          <Text style={styles.helper}>
            Sugerencia: {meds[0].name} (id {meds[0].id}){" "}
            {meds[1] ? `• ${meds[1].name} (id ${meds[1].id})` : ""}
          </Text>
        ) : null}
      </Card>

      <Text style={[typography.subtitle, styles.subtitle]}>Programados</Text>
      {reminders.map((item) => (
        <Card key={item.id} style={{ marginBottom: spacing[3] }}>
          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Text style={[typography.h2, styles.name]}>{item.label}</Text>
              <Text style={styles.meta}>
                Hora: {item.time} {item.medicationId ? `• Med ${item.medicationId}` : ""}
              </Text>
            </View>
            <TouchableOpacity onPress={() => removeReminder(item.id)}>
              <Ionicons name="trash-outline" size={22} color={palette.danger} />
            </TouchableOpacity>
          </View>
        </Card>
      ))}
    </Screen>
  );
};

const styles = StyleSheet.create({
  title: { color: palette.text, marginBottom: spacing[2] },
  subtitle: { color: palette.text },
  row: { flexDirection: "row", gap: spacing[3], alignItems: "center" },
  name: { color: palette.text },
  meta: { color: palette.muted, marginTop: 6 },
  helper: { marginTop: spacing[2], color: palette.muted },
});
