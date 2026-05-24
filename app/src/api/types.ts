export type Medication = {
  id: number;
  name: string;
  description?: string;
  source?: string;
  createdAt: string;
};

export type Reminder = {
  id: number;
  medicationId?: number;
  label: string;
  time: string;
  recurrence: string;
  createdAt: string;
};

export type Appointment = {
  id: number;
  title: string;
  date: string;
  location?: string;
  notes?: string;
  createdAt: string;
};

export type Intake = {
  id: number;
  medicationId: number;
  takenAt: string;
  quantity: string;
  createdAt: string;
};

export type DashboardStats = {
  meds: number;
  reminders: number;
  appointments: number;
  intakesToday: number;
};

export type WikiResult = {
  title: string;
  extract: string;
  url: string;
  lang: string;
};
