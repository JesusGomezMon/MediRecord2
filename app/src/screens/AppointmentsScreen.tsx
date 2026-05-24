import React, { useState } from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { Screen } from "../ui/Screen";
import { Card } from "../ui/Card";
import { TextField } from "../ui/TextField";
import { PrimaryButton } from "../ui/PrimaryButton";
import { palette, spacing, typography } from "../theme/theme";
import { useStore } from "../state/useStore";

export const AppointmentsScreen: React.FC = () => {
  const { appointments, addAppointment, removeAppointment } = useStore();
  const [title, setTitle] = useState("Consulta médica");
  const [date, setDate] = useState(new Date().toISOString());
  const [location, setLocation] = useState("Clínica");
  const [notes, setNotes] = useState("");

  const onAdd = async () => {
    if (!title.trim() || !date.trim()) return;
    await addAppointment({ title, date, location, notes });
  };

  return (
    <Screen>
      <Card elevated>
        <Text style={[typography.h2, styles.title]}>Agendar cita</Text>
        <TextField label="Título" value={title} onChangeText={setTitle} />
        <TextField
          label="Fecha (ISO)"
          value={date}
          onChangeText={setDate}
          placeholder="2026-05-24T15:00:00Z"
        />
        <TextField label="Lugar" value={location} onChangeText={setLocation} />
        <TextField
          label="Notas"
          value={notes}
          onChangeText={setNotes}
          placeholder="P.ej. llevar laboratorios"
        />
        <PrimaryButton label="Guardar" onPress={onAdd} />
      </Card>

      <Text style={[typography.subtitle, styles.subtitle]}>Próximas</Text>
      {appointments.map((item) => (
        <Card key={item.id} style={{ marginBottom: spacing[3] }}>
          <View style={styles.row}>
            <View style={{ flex: 1 }}>
              <Text style={[typography.h2, styles.name]}>{item.title}</Text>
              <Text style={styles.meta}>
                {new Date(item.date).toLocaleString()} • {item.location}
              </Text>
              {item.notes ? <Text style={styles.meta}>{item.notes}</Text> : null}
            </View>
            <TouchableOpacity onPress={() => removeAppointment(item.id)}>
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
});
