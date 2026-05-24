import React from "react";
import { TextInput, StyleSheet, TextInputProps, View, Text } from "react-native";
import { palette, radii, spacing, typography } from "../theme/theme";

type Props = TextInputProps & {
  label?: string;
  error?: string;
};

export const TextField: React.FC<Props> = ({ label, error, style, ...rest }) => {
  return (
    <View style={{ marginBottom: spacing[4] }}>
      {label ? <Text style={[typography.small, styles.label]}>{label}</Text> : null}
      <TextInput
        style={[styles.input, style, error && styles.inputError]}
        placeholderTextColor={palette.muted}
        {...rest}
      />
      {error ? <Text style={styles.error}>{error}</Text> : null}
    </View>
  );
};

const styles = StyleSheet.create({
  label: {
    color: palette.muted,
    marginBottom: 6,
  },
  input: {
    borderWidth: 1,
    borderColor: palette.border,
    backgroundColor: "#fff",
    borderRadius: radii.md,
    paddingHorizontal: spacing[3],
    paddingVertical: 12,
    color: palette.text,
    fontSize: 15,
  },
  inputError: {
    borderColor: palette.danger,
  },
  error: {
    color: palette.danger,
    marginTop: 4,
    ...typography.small,
  },
});
