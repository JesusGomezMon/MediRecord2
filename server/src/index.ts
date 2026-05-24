import express from "express";
import cors from "cors";
import { z } from "zod";
import { dbRepo } from "./db";
import {
  appointmentSchema,
  intakeSchema,
  medicationSchema,
  reminderSchema,
} from "./types";
import { wikiSearch } from "./wiki";

const app = express();
app.use(cors());
app.use(express.json());

const idParam = z.object({ id: z.string().regex(/^\d+$/).transform(Number) });

app.get("/health", (_req, res) => {
  res.json({ ok: true, stats: dbRepo.stats() });
});

// Medications
app.get("/medications", (_req, res) => {
  res.json(dbRepo.listMedications());
});

app.post("/medications", (req, res) => {
  const parsed = medicationSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const med = dbRepo.createMedication(parsed.data);
  res.status(201).json(med);
});

app.put("/medications/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  const parsed = medicationSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const med = dbRepo.updateMedication(id.data.id, parsed.data);
  res.json(med);
});

app.delete("/medications/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  dbRepo.deleteMedication(id.data.id);
  res.status(204).send();
});

// Reminders
app.get("/reminders", (_req, res) => {
  res.json(dbRepo.listReminders());
});

app.post("/reminders", (req, res) => {
  const parsed = reminderSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const reminder = dbRepo.createReminder(parsed.data);
  res.status(201).json(reminder);
});

app.put("/reminders/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  const parsed = reminderSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const reminder = dbRepo.updateReminder(id.data.id, parsed.data);
  res.json(reminder);
});

app.delete("/reminders/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  dbRepo.deleteReminder(id.data.id);
  res.status(204).send();
});

// Appointments
app.get("/appointments", (_req, res) => {
  res.json(dbRepo.listAppointments());
});

app.post("/appointments", (req, res) => {
  const parsed = appointmentSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const appointment = dbRepo.createAppointment(parsed.data);
  res.status(201).json(appointment);
});

app.put("/appointments/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  const parsed = appointmentSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const appointment = dbRepo.updateAppointment(id.data.id, parsed.data);
  res.json(appointment);
});

app.delete("/appointments/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  dbRepo.deleteAppointment(id.data.id);
  res.status(204).send();
});

// Intakes (historial)
app.get("/intakes", (_req, res) => {
  res.json(dbRepo.listIntakes());
});

app.post("/intakes", (req, res) => {
  const parsed = intakeSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json(parsed.error.flatten());
  const intake = dbRepo.createIntake(parsed.data);
  res.status(201).json(intake);
});

app.delete("/intakes/:id", (req, res) => {
  const id = idParam.safeParse(req.params);
  if (!id.success) return res.status(400).json({ message: "ID inválido" });
  dbRepo.deleteIntake(id.data.id);
  res.status(204).send();
});

// Search (Wikipedia summary proxy)
app.get("/search", async (req, res) => {
  const query = (req.query.q ?? req.query.query ?? "") as string;
  if (!query.trim()) return res.status(400).json({ message: "Falta el parámetro q" });
  const result = await wikiSearch(query);
  if (!result) return res.status(404).json({ message: "Sin resultados" });
  res.json(result);
});

const port = process.env.PORT ?? 4000;

if (require.main === module) {
  app.listen(port, () => {
    console.log(`API ready on http://localhost:${port}`);
  });
}

export default app;
