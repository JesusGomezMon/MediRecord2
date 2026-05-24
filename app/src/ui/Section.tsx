import React from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { palette, spacing, typography } from "../theme/theme";

type Props = {
  title: string;
  actionLabel?: string;
  onActionPress?: () => void;
};

export const Section: React.FC<Props> = ({ title, actionLabel, onActionPress, children }) => {
  return (
    <View style={{ marginBottom: spacing[5] }}>
      <View style={styles.row}>
        <Text style={[typography.h2, styles.title]}>{title}</Text>
        {actionLabel ? (
          <TouchableOpacity onPress={onActionPress}>
            <Text style={[typography.small, styles.action]}>{actionLabel}</Text>
          </TouchableOpacity>
        ) : null}
      </View>
      {children}
    </View>
  );
};

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: spacing[2],
  },
  title: {
    color: palette.text,
  },
  action: {
    color: palette.primary,
  },
});
