import { api } from "./client";
import {
  Appointment,
  DashboardStats,
  Intake,
  Medication,
  Reminder,
  WikiResult,
} from "./types";

export const Api = {
  async getStats(): Promise<DashboardStats> {
    const res = await api.get<{ stats: DashboardStats }>("/health");
    return res.data.stats;
  },
  async listMedications(): Promise<Medication[]> {
    const res = await api.get<Medication[]>("/medications");
    return res.data;
  },
  async createMedication(body: Pick<Medication, "name" | "description" | "source">) {
    const res = await api.post<Medication>("/medications", body);
    return res.data;
  },
  async deleteMedication(id: number) {
    await api.delete(`/medications/${id}`);
  },
  async listReminders(): Promise<Reminder[]> {
    const res = await api.get<Reminder[]>("/reminders");
    return res.data;
  },
  async createReminder(body: {
    medicationId?: number;
    label: string;
    time: string;
    recurrence?: string;
  }) {
    const res = await api.post<Reminder>("/reminders", body);
    return res.data;
  },
  async deleteReminder(id: number) {
    await api.delete(`/reminders/${id}`);
  },
  async listAppointments(): Promise<Appointment[]> {
    const res = await api.get<Appointment[]>("/appointments");
    return res.data;
  },
  async createAppointment(body: {
    title: string;
    date: string;
    location?: string;
    notes?: string;
  }) {
    const res = await api.post<Appointment>("/appointments", body);
    return res.data;
  },
  async deleteAppointment(id: number) {
    await api.delete(`/appointments/${id}`);
  },
  async listIntakes(): Promise<Intake[]> {
    const res = await api.get<Intake[]>("/intakes");
    return res.data;
  },
  async createIntake(body: { medicationId: number; quantity?: string }) {
    const res = await api.post<Intake>("/intakes", body);
    return res.data;
  },
  async search(query: string): Promise<WikiResult | null> {
    const res = await api.get<WikiResult>("/search", { params: { q: query } });
    return res.data;
  },
};
