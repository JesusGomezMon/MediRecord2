import React from "react";
import { NavigationContainer, DefaultTheme } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { Ionicons } from "@expo/vector-icons";
import { palette } from "../theme/theme";
import { DashboardScreen } from "../screens/DashboardScreen";
import { MedicationsScreen } from "../screens/MedicationsScreen";
import { RemindersScreen } from "../screens/RemindersScreen";
import { AppointmentsScreen } from "../screens/AppointmentsScreen";
import { SearchScreen } from "../screens/SearchScreen";
import { HistoryScreen } from "../screens/HistoryScreen";

const Tab = createBottomTabNavigator();

const navTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    background: palette.background,
    primary: palette.primary,
    text: palette.text,
  },
};

export const AppNavigator = () => (
  <NavigationContainer theme={navTheme}>
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarActiveTintColor: palette.primary,
        tabBarInactiveTintColor: palette.muted,
        tabBarLabelStyle: { fontSize: 12, fontWeight: "600" },
        tabBarStyle: { paddingBottom: 6, paddingTop: 4, height: 60 },
        tabBarIcon: ({ color, size }) => {
          const icons: Record<string, string> = {
            Inicio: "grid-outline",
            Meds: "medkit-outline",
            Recordatorios: "alarm-outline",
            Citas: "calendar-outline",
            Buscar: "search-outline",
            Historial: "time-outline",
          };
          const name = icons[route.name] ?? "ellipse-outline";
          return <Ionicons name={name as any} size={size} color={color} />;
        },
      })}
    >
      <Tab.Screen name="Inicio" component={DashboardScreen} />
      <Tab.Screen name="Meds" component={MedicationsScreen} options={{ title: "Medicinas" }} />
      <Tab.Screen name="Recordatorios" component={RemindersScreen} />
      <Tab.Screen name="Citas" component={AppointmentsScreen} />
      <Tab.Screen name="Buscar" component={SearchScreen} />
      <Tab.Screen name="Historial" component={HistoryScreen} />
    </Tab.Navigator>
  </NavigationContainer>
);
