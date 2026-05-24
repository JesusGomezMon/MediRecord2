import React, { useState } from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { Screen } from "../ui/Screen";
import { Card } from "../ui/Card";
import { TextField } from "../ui/TextField";
import { PrimaryButton } from "../ui/PrimaryButton";
import { palette, spacing, typography } from "../theme/theme";
import { useStore } from "../state/useStore";

export const MedicationsScreen: React.FC = () => {
  const { meds, addMedication, removeMedication } = useStore();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  const onAdd = async () => {
    if (!name.trim()) return;
    await addMedication({ name, description });
    setName("");
    setDescription("");
  };

  return (
    <Screen>
      <Card elevated>
        <Text style={[typography.h2, styles.title]}>Agregar medicamento</Text>
        <TextField label="Nombre" value={name} onChangeText={setName} placeholder="Ej. Paracetamol" />
        <TextField
          label="Descripción"
          value={description}
          onChangeText={setDescription}
          placeholder="Posología, notas..."
          multiline
          style={{ minHeight: 80, textAlignVertical: "top" }}
        />
        <PrimaryButton label="Guardar" onPress={onAdd} />
      </Card>

      <Text style={[typography.subtitle, styles.subtitle]}>Tus medicamentos</Text>
      {meds.map((med) => (
        <Card key={med.id} style={{ marginBottom: spacing[3] }}>
          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Text style={[typography.h2, styles.name]}>{med.name}</Text>
              <Text style={[typography.body, styles.desc]} numberOfLines={2}>
                {med.description}
              </Text>
            </View>
            <TouchableOpacity onPress={() => removeMedication(med.id)}>
              <Ionicons name="trash-outline" size={22} color={palette.danger} />
            </TouchableOpacity>
          </View>
          <Text style={styles.meta}>Agregado: {new Date(med.createdAt).toLocaleDateString()}</Text>
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
  desc: { color: palette.muted, marginTop: 6 },
  meta: { color: palette.muted, marginTop: 8, fontSize: 12 },
});
