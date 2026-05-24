import { create } from "zustand";
import { Api } from "../api/service";
import {
  Appointment,
  DashboardStats,
  Intake,
  Medication,
  Reminder,
  WikiResult,
} from "../api/types";

type UiState = {
  loading: boolean;
  error?: string;
};

type Store = UiState & {
  stats: DashboardStats | null;
  meds: Medication[];
  reminders: Reminder[];
  appointments: Appointment[];
  intakes: Intake[];
  searchResult: WikiResult | null;
  syncAll: () => Promise<void>;
  addMedication: (payload: { name: string; description?: string }) => Promise<void>;
  addReminder: (payload: { medicationId?: number; label: string; time: string }) => Promise<void>;
  addAppointment: (payload: { title: string; date: string; location?: string; notes?: string }) => Promise<void>;
  addIntake: (payload: { medicationId: number; quantity?: string }) => Promise<void>;
  removeMedication: (id: number) => Promise<void>;
  removeReminder: (id: number) => Promise<void>;
  removeAppointment: (id: number) => Promise<void>;
  search: (query: string) => Promise<void>;
  clearSearch: () => void;
};

export const useStore = create<Store>((set, get) => ({
  loading: false,
  stats: null,
  meds: [],
  reminders: [],
  appointments: [],
  intakes: [],
  searchResult: null,

  async syncAll() {
    set({ loading: true, error: undefined });
    try {
      const [stats, meds, reminders, appointments, intakes] = await Promise.all([
        Api.getStats(),
        Api.listMedications(),
        Api.listReminders(),
        Api.listAppointments(),
        Api.listIntakes(),
      ]);
      set({ stats, meds, reminders, appointments, intakes, loading: false });
    } catch (error: any) {
      set({ error: "No se pudo sincronizar", loading: false });
    }
  },

  async addMedication(payload) {
    await Api.createMedication({ ...payload, source: "app" });
    await get().syncAll();
  },

  async addReminder(payload) {
    await Api.createReminder(payload);
    await get().syncAll();
  },

  async addAppointment(payload) {
    await Api.createAppointment(payload);
    await get().syncAll();
  },

  async addIntake(payload) {
    await Api.createIntake(payload);
    await get().syncAll();
  },

  async removeMedication(id: number) {
    await Api.deleteMedication(id);
    await get().syncAll();
  },

  async removeReminder(id: number) {
    await Api.deleteReminder(id);
    await get().syncAll();
  },

  async removeAppointment(id: number) {
    await Api.deleteAppointment(id);
    await get().syncAll();
  },

  async search(query: string) {
    if (!query.trim()) return;
    try {
      const result = await Api.search(query);
      set({ searchResult: result, error: undefined });
    } catch (e) {
      set({ error: "Sin resultados", searchResult: null });
    }
  },

  clearSearch() {
    set({ searchResult: null });
  },
}));
