import React from "react";
import { Text, TouchableOpacity, StyleSheet, GestureResponderEvent } from "react-native";
import { palette, radii, typography } from "../theme/theme";

type Props = {
  label: string;
  onPress?: (event: GestureResponderEvent) => void;
  variant?: "primary" | "secondary" | "ghost";
  disabled?: boolean;
};

export const PrimaryButton: React.FC<Props> = ({
  label,
  onPress,
  variant = "primary",
  disabled = false,
}) => {
  const stylesVariant =
    variant === "secondary"
      ? buttonStyles.secondary
      : variant === "ghost"
        ? buttonStyles.ghost
        : buttonStyles.primary;

  return (
    <TouchableOpacity
      style={[buttonStyles.base, stylesVariant, disabled && buttonStyles.disabled]}
      onPress={onPress}
      activeOpacity={0.85}
      disabled={disabled}
    >
      <Text
        style={[
          typography.subtitle,
          buttonStyles.label,
          variant === "ghost" && { color: palette.text },
        ]}
      >
        {label}
      </Text>
    </TouchableOpacity>
  );
};

const buttonStyles = StyleSheet.create({
  base: {
    borderRadius: radii.md,
    paddingVertical: 14,
    paddingHorizontal: 16,
    alignItems: "center",
    justifyContent: "center",
  },
  label: {
    color: "#fff",
  },
  primary: {
    backgroundColor: palette.primary,
  },
  secondary: {
    backgroundColor: palette.secondary,
  },
  ghost: {
    backgroundColor: "transparent",
    borderWidth: 1,
    borderColor: palette.border,
  },
  disabled: {
    opacity: 0.5,
  },
});
