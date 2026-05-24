import React from "react";
import { View, StyleSheet, ViewProps } from "react-native";
import { palette, radii } from "../theme/theme";

type Props = ViewProps & {
  elevated?: boolean;
};

export const Card: React.FC<Props> = ({ elevated = false, style, children, ...rest }) => {
  return (
    <View style={[styles.base, elevated && styles.elevated, style]} {...rest}>
      {children}
    </View>
  );
};

const styles = StyleSheet.create({
  base: {
    backgroundColor: palette.surface,
    borderRadius: radii.md,
    borderWidth: 1,
    borderColor: palette.border,
    padding: 16,
  },
  elevated: {
    shadowColor: palette.shadow,
    shadowOffset: { width: 0, height: 12 },
    shadowOpacity: 1,
    shadowRadius: 24,
    elevation: 5,
  },
});
