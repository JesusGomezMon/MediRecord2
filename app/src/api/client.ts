import axios from "axios";
import { Platform } from "react-native";

const localHost = Platform.OS === "android" ? "http://10.0.2.2:4000" : "http://localhost:4000";
const baseURL = process.env.EXPO_PUBLIC_API_URL || localHost;

export const api = axios.create({
  baseURL,
  timeout: 8000,
});
