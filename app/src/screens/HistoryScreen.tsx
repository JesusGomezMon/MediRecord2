import React, { useState } from "react";
import { View, Text, StyleSheet } from "react-native";
import { Screen } from "../ui/Screen";
import { Card } from "../ui/Card";
import { TextField } from "../ui/TextField";
import { PrimaryButton } from "../ui/PrimaryButton";
import { palette, spacing, typography } from "../theme/theme";
import { useStore } from "../state/useStore";

export const HistoryScreen: React.FC = () => {
  const { intakes, meds, addIntake } = useStore();
  const [medicationId, setMedicationId] = useState<number | undefined>(
    meds[0] ? meds[0].id : undefined,
  );
  const [quantity, setQuantity] = useState("1");

  const onAdd = async () => {
    if (!medicationId) return;
    await addIntake({ medicationId, quantity });
  };

  return (
    <Screen>
      <Card elevated>
        <Text style={[typography.h2, styles.title]}>Marcar toma</Text>
        <TextField
          label="ID de medicina"
          value={medicationId ? String(medicationId) : ""}
          onChangeText={(val) => setMedicationId(val ? Number(val) : undefined)}
          keyboardType="numeric"
          placeholder={meds[0] ? `Ej. ${meds[0].id}` : "ID"}
        />
        <TextField
          label="Cantidad"
          value={quantity}
          onChangeText={setQuantity}
          keyboardType="numeric"
        />
        <PrimaryButton label="Registrar" onPress={onAdd} />
        {meds.length > 0 ? (
          <Text style={styles.helper}>Ej: {meds.slice(0, 2).map((m) => `${m.name} (${m.id})`).join(" • ")}</Text>
        ) : null}
      </Card>

      <Text style={[typography.subtitle, styles.subtitle]}>Historial de tomas</Text>
      {intakes.map((item) => (
        <Card key={item.id} style={{ marginBottom: spacing[3] }}>
          <Text style={[typography.subtitle, styles.name]}>Med #{item.medicationId}</Text>
          <Text style={styles.meta}>
            {new Date(item.takenAt || item.createdAt).toLocaleString()} • Cant: {item.quantity}
          </Text>
        </Card>
      ))}
    </Screen>
  );
};

const styles = StyleSheet.create({
  title: { color: palette.text, marginBottom: spacing[2] },
  subtitle: { color: palette.text },
  name: { color: palette.text },
  meta: { color: palette.muted, marginTop: 6 },
  helper: { marginTop: spacing[2], color: palette.muted },
});
