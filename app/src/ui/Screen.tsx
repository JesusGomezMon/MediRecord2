import React from "react";
import { SafeAreaView } from "react-native-safe-area-context";
import { ScrollView, StyleSheet, ViewProps } from "react-native";
import { palette, spacing } from "../theme/theme";

type Props = ViewProps & {
  scrollable?: boolean;
};

export const Screen: React.FC<Props> = ({ scrollable = true, style, children }) => {
  const content = (
    <SafeAreaView style={[styles.safe, style]}>
      {scrollable ? (
        <ScrollView contentContainerStyle={styles.content}>{children}</ScrollView>
      ) : (
        children
      )}
    </SafeAreaView>
  );
  return content;
};

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: palette.background,
  },
  content: {
    padding: spacing[4],
    gap: spacing[4],
  },
});
