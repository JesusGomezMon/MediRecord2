import React, { useEffect } from "react";
import { View, Text, StyleSheet, FlatList, RefreshControl } from "react-native";
import { useNavigation } from "@react-navigation/native";
import { Screen } from "../ui/Screen";
import { Card } from "../ui/Card";
import { StatCard } from "../ui/StatCard";
import { ActionTile } from "../ui/ActionTile";
import { PrimaryButton } from "../ui/PrimaryButton";
import { palette, spacing, typography } from "../theme/theme";
import { useStore } from "../state/useStore";

type Tile = {
  title: string;
  subtitle: string;
  route: string;
  colors?: string[];
};

export const DashboardScreen: React.FC = () => {
  const navigation = useNavigation<any>();
  const { stats, meds, reminders, appointments, intakes, syncAll, loading } = useStore();

  useEffect(() => {
    syncAll();
  }, [syncAll]);

  const tiles: Tile[] = [
    {
      title: "Medicamentos",
      subtitle: `${meds.length} activos`,
      route: "Meds",
      colors: ["#5B8CFF", "#7C3AED"],
    },
    {
      title: "Recordatorios",
      subtitle: `${reminders.length} diarios`,
      route: "Recordatorios",
      colors: ["#22D3EE", "#3B82F6"],
    },
    {
      title: "Citas",
      subtitle: `${appointments.length} próximas`,
      route: "Citas",
      colors: ["#F97316", "#FB7185"],
    },
    {
      title: "Historial",
      subtitle: `${intakes.length} tomas`,
      route: "Historial",
      colors: ["#22C55E", "#16A34A"],
    },
    {
      title: "Buscar",
      subtitle: "Info confiable",
      route: "Buscar",
      colors: ["#0EA5E9", "#2563EB"],
    },
    {
      title: "Protección",
      subtitle: "Consejos rápidos",
      route: "Historial",
      colors: ["#8B5CF6", "#7C3AED"],
    },
  ];

  return (
    <Screen>
      <View style={styles.headerRow}>
        <View>
          <Text style={[typography.small, styles.muted]}>Tu salud al día</Text>
          <Text style={[typography.h1, styles.title]}>MediRecord</Text>
        </View>
        <PrimaryButton label="Actualizar" variant="secondary" onPress={syncAll} disabled={loading} />
      </View>

      <Card elevated>
        <Text style={[typography.subtitle, styles.label]}>Resumen</Text>
        <View style={styles.statsRow}>
          <StatCard label="Medicinas" value={stats?.meds ?? meds.length} />
          <StatCard label="Recordatorios" value={stats?.reminders ?? reminders.length} />
        </View>
        <View style={styles.statsRow}>
          <StatCard label="Citas" value={stats?.appointments ?? appointments.length} />
          <StatCard
            label="Tomas hoy"
            value={stats?.intakesToday ?? 0}
            color={palette.success}
          />
        </View>
      </Card>

      <Text style={[typography.subtitle, styles.label]}>Panel rápido</Text>
      <FlatList
        data={tiles}
        numColumns={2}
        keyExtractor={(item) => item.title}
        columnWrapperStyle={{ gap: spacing[3] }}
        renderItem={({ item }) => (
          <View style={{ flex: 1, marginBottom: spacing[3] }}>
            <ActionTile
              title={item.title}
              subtitle={item.subtitle}
              colors={item.colors}
              onPress={() => navigation.navigate(item.route)}
            />
          </View>
        )}
        refreshControl={<RefreshControl refreshing={loading} onRefresh={syncAll} />}
        showsVerticalScrollIndicator={false}
        scrollEnabled={false}
      />
    </Screen>
  );
};

const styles = StyleSheet.create({
  headerRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  muted: {
    color: palette.muted,
  },
  title: {
    color: palette.text,
  },
  label: {
    color: palette.muted,
    marginBottom: 8,
  },
  statsRow: {
    flexDirection: "row",
    gap: spacing[3],
    marginBottom: spacing[3],
  },
});
