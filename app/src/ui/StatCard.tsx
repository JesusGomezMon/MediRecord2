import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { palette, radii, spacing, typography } from "../theme/theme";

type Props = {
  label: string;
  value: string | number;
  color?: string;
};

export const StatCard: React.FC<Props> = ({ label, value, color = palette.primary }) => (
  <View style={[styles.card, { borderColor: color }]}>
    <Text style={[typography.small, styles.label]}>{label}</Text>
    <Text style={[typography.h1, styles.value]}>{value}</Text>
  </View>
);

const styles = StyleSheet.create({
  card: {
    flex: 1,
    padding: spacing[3],
    borderRadius: radii.md,
    borderWidth: 1,
    borderColor: palette.border,
    backgroundColor: "#fff",
  },
  label: {
    color: palette.muted,
    marginBottom: 4,
  },
  value: {
    color: palette.text,
  },
});
