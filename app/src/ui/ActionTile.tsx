import React from "react";
import { Text, TouchableOpacity, StyleSheet } from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { palette, radii, spacing, typography } from "../theme/theme";

type Props = {
  title: string;
  subtitle?: string;
  onPress?: () => void;
  colors?: string[];
};

export const ActionTile: React.FC<Props> = ({
  title,
  subtitle,
  onPress,
  colors = [palette.primary, palette.secondary],
}) => (
  <TouchableOpacity activeOpacity={0.9} style={{ flex: 1 }} onPress={onPress}>
    <LinearGradient colors={colors} style={styles.tile}>
      <Text style={[typography.subtitle, styles.title]}>{title}</Text>
      {subtitle ? <Text style={[typography.small, styles.subtitle]}>{subtitle}</Text> : null}
    </LinearGradient>
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  tile: {
    borderRadius: radii.lg,
    padding: spacing[4],
    minHeight: 120,
    justifyContent: "space-between",
  },
  title: {
    color: "#fff",
  },
  subtitle: {
    color: "rgba(255,255,255,0.9)",
  },
});
