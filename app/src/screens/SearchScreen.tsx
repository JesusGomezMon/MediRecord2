import React, { useState } from "react";
import { View, Text, StyleSheet, Linking } from "react-native";
import { PrimaryButton } from "../ui/PrimaryButton";
import { Screen } from "../ui/Screen";
import { Card } from "../ui/Card";
import { TextField } from "../ui/TextField";
import { palette, spacing, typography } from "../theme/theme";
import { useStore } from "../state/useStore";

export const SearchScreen: React.FC = () => {
  const { searchResult, search, clearSearch, addMedication } = useStore();
  const [query, setQuery] = useState("Paracetamol");
  const [saving, setSaving] = useState(false);

  const onSearch = async () => {
    await search(query);
  };

  const onSave = async () => {
    if (!searchResult) return;
    setSaving(true);
    await addMedication({
      name: searchResult.title,
      description: searchResult.extract,
    });
    setSaving(false);
  };

  return (
    <Screen>
      <Card elevated>
        <Text style={[typography.h2, styles.title]}>Buscar medicamento</Text>
        <TextField
          label="Nombre"
          value={query}
          onChangeText={setQuery}
          placeholder="Ej. Ibuprofeno"
        />
        <View style={styles.row}>
          <PrimaryButton label="Buscar" onPress={onSearch} />
          <PrimaryButton label="Limpiar" variant="ghost" onPress={clearSearch} />
        </View>
      </Card>

      {searchResult ? (
        <Card elevated>
          <Text style={[typography.subtitle, styles.resultTitle]}>{searchResult.title}</Text>
          <Text style={styles.desc}>{searchResult.extract}</Text>
          <View style={styles.row}>
            <PrimaryButton label="Guardar en meds" onPress={onSave} disabled={saving} />
            <PrimaryButton
              label="Abrir wiki"
              variant="secondary"
              onPress={() => Linking.openURL(searchResult.url)}
            />
          </View>
          <Text style={styles.meta}>Fuente: Wikipedia ({searchResult.lang})</Text>
        </Card>
      ) : (
        <Text style={styles.muted}>Busca para ver un resumen confiable.</Text>
      )}
    </Screen>
  );
};

const styles = StyleSheet.create({
  title: { color: palette.text, marginBottom: spacing[2] },
  row: {
    flexDirection: "row",
    gap: spacing[3],
    marginTop: spacing[2],
    alignItems: "center",
  },
  resultTitle: { color: palette.text, marginBottom: spacing[2] },
  desc: { color: palette.text, marginBottom: spacing[2], lineHeight: 20 },
  meta: { color: palette.muted, marginTop: spacing[1] },
  muted: { color: palette.muted },
});
