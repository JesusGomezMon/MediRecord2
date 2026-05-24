import { z } from "zod";

export const medicationSchema = z.object({
  name: z.string().min(1, "El nombre es obligatorio"),
  description: z.string().default(""),
  source: z.string().default("manual"),
});

export type MedicationInput = z.infer<typeof medicationSchema>;
export type Medication = MedicationInput & {
  id: number;
  createdAt: string;
};

export const reminderSchema = z.object({
  medicationId: z.number().int().positive().optional(),
  label: z.string().min(1, "El título es obligatorio"),
  time: z.string().min(1, "La hora es obligatoria"), // HH:mm
  recurrence: z.string().default("Diario"),
});

export type ReminderInput = z.infer<typeof reminderSchema>;
export type Reminder = ReminderInput & {
  id: number;
  createdAt: string;
};

export const appointmentSchema = z.object({
  title: z.string().min(1, "El título es obligatorio"),
  date: z.string().min(1, "La fecha es obligatoria"), // ISO string
  location: z.string().default(""),
  notes: z.string().default(""),
});

export type AppointmentInput = z.infer<typeof appointmentSchema>;
export type Appointment = AppointmentInput & {
  id: number;
  createdAt: string;
};

export const intakeSchema = z.object({
  medicationId: z.number().int().positive(),
  takenAt: z.string().optional(), // ISO string
  quantity: z.string().default("1"),
});

export type IntakeInput = z.infer<typeof intakeSchema>;
export type Intake = IntakeInput & {
  id: number;
  createdAt: string;
};

export type DashboardStats = {
  meds: number;
  reminders: number;
  appointments: number;
  intakesToday: number;
};
