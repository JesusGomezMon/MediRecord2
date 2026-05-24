import fs from "fs";
import path from "path";
import Database from "better-sqlite3";
import {
  Appointment,
  AppointmentInput,
  Intake,
  IntakeInput,
  Medication,
  MedicationInput,
  Reminder,
  ReminderInput,
  DashboardStats,
} from "./types";

const dbPath = process.env.DB_PATH ?? path.join(__dirname, "..", "data", "medirecord.db");
const isMemory = dbPath === ":memory:";
if (!isMemory) {
  fs.mkdirSync(path.dirname(dbPath), { recursive: true });
}

const db = new Database(dbPath);

function migrate() {
  db.prepare(
    `CREATE TABLE IF NOT EXISTS medications (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      description TEXT DEFAULT '',
      source TEXT DEFAULT 'manual',
      created_at TEXT DEFAULT (datetime('now'))
    );`,
  ).run();

  db.prepare(
    `CREATE TABLE IF NOT EXISTS reminders (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      medication_id INTEGER,
      label TEXT NOT NULL,
      time TEXT NOT NULL,
      recurrence TEXT DEFAULT 'Diario',
      created_at TEXT DEFAULT (datetime('now')),
      FOREIGN KEY (medication_id) REFERENCES medications(id) ON DELETE SET NULL
    );`,
  ).run();

  db.prepare(
    `CREATE TABLE IF NOT EXISTS appointments (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title TEXT NOT NULL,
      date TEXT NOT NULL,
      location TEXT DEFAULT '',
      notes TEXT DEFAULT '',
      created_at TEXT DEFAULT (datetime('now'))
    );`,
  ).run();

  db.prepare(
    `CREATE TABLE IF NOT EXISTS intakes (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      medication_id INTEGER NOT NULL,
      taken_at TEXT DEFAULT (datetime('now')),
      quantity TEXT DEFAULT '1',
      created_at TEXT DEFAULT (datetime('now')),
      FOREIGN KEY (medication_id) REFERENCES medications(id) ON DELETE CASCADE
    );`,
  ).run();
}

function seedIfEmpty() {
  const medsCount = db.prepare("SELECT COUNT(*) as count FROM medications").get() as {
    count: number;
  };
  if (medsCount.count > 0) return;

  const insertMed = db.prepare(
    "INSERT INTO medications (name, description, source) VALUES (@name, @description, @source)",
  );
  const insertReminder = db.prepare(
    "INSERT INTO reminders (medication_id, label, time, recurrence) VALUES (@medicationId, @label, @time, @recurrence)",
  );
  const insertAppointment = db.prepare(
    "INSERT INTO appointments (title, date, location, notes) VALUES (@title, @date, @location, @notes)",
  );

  const medIds: number[] = [];
  medIds.push(
    insertMed.run({
      name: "Paracetamol 500mg",
      description: "Analgésico y antipirético. Tomar después de comidas.",
      source: "seed",
    }).lastInsertRowid as number,
  );
  medIds.push(
    insertMed.run({
      name: "Atorvastatina 20mg",
      description: "Para colesterol. Tomar en la noche.",
      source: "seed",
    }).lastInsertRowid as number,
  );

  insertReminder.run({
    medicationId: medIds[0],
    label: "Mañana 08:00",
    time: "08:00",
    recurrence: "Diario",
  });
  insertReminder.run({
    medicationId: medIds[1],
    label: "Noche 21:00",
    time: "21:00",
    recurrence: "Diario",
  });

  insertAppointment.run({
    title: "Consulta médica",
    date: new Date(Date.now() + 86400000).toISOString(),
    location: "Clínica Central",
    notes: "Llevar resultados de laboratorio",
  });
}

migrate();
seedIfEmpty();

export const dbRepo = {
  listMedications(): Medication[] {
    const stmt = db.prepare(
      "SELECT id, name, description, source, created_at as createdAt FROM medications ORDER BY created_at DESC",
    );
    return stmt.all() as Medication[];
  },
  createMedication(input: MedicationInput): Medication {
    const stmt = db.prepare(
      "INSERT INTO medications (name, description, source) VALUES (@name, @description, @source)",
    );
    const result = stmt.run(input);
    const row = db
      .prepare(
        "SELECT id, name, description, source, created_at as createdAt FROM medications WHERE id = ?",
      )
      .get(result.lastInsertRowid) as Medication;
    return row;
  },
  updateMedication(id: number, input: MedicationInput): Medication {
    db.prepare(
      "UPDATE medications SET name=@name, description=@description, source=@source WHERE id=@id",
    ).run({ id, ...input });
    return db
      .prepare(
        "SELECT id, name, description, source, created_at as createdAt FROM medications WHERE id = ?",
      )
      .get(id) as Medication;
  },
  deleteMedication(id: number) {
    db.prepare("DELETE FROM medications WHERE id = ?").run(id);
  },

  listReminders(): Reminder[] {
    return db
      .prepare(
        `SELECT id, medication_id as medicationId, label, time, recurrence,
                created_at as createdAt
         FROM reminders ORDER BY time ASC`,
      )
      .all() as Reminder[];
  },
  createReminder(input: ReminderInput): Reminder {
    const stmt = db.prepare(
      "INSERT INTO reminders (medication_id, label, time, recurrence) VALUES (@medicationId, @label, @time, @recurrence)",
    );
    const result = stmt.run(input);
    return db
      .prepare(
        `SELECT id, medication_id as medicationId, label, time, recurrence,
                created_at as createdAt
         FROM reminders WHERE id = ?`,
      )
      .get(result.lastInsertRowid) as Reminder;
  },
  updateReminder(id: number, input: ReminderInput): Reminder {
    db.prepare(
      "UPDATE reminders SET medication_id=@medicationId, label=@label, time=@time, recurrence=@recurrence WHERE id=@id",
    ).run({ id, ...input });
    return db
      .prepare(
        `SELECT id, medication_id as medicationId, label, time, recurrence,
                created_at as createdAt
         FROM reminders WHERE id = ?`,
      )
      .get(id) as Reminder;
  },
  deleteReminder(id: number) {
    db.prepare("DELETE FROM reminders WHERE id = ?").run(id);
  },

  listAppointments(): Appointment[] {
    return db
      .prepare(
        `SELECT id, title, date, location, notes, created_at as createdAt
         FROM appointments ORDER BY date ASC`,
      )
      .all() as Appointment[];
  },
  createAppointment(input: AppointmentInput): Appointment {
    const stmt = db.prepare(
      "INSERT INTO appointments (title, date, location, notes) VALUES (@title, @date, @location, @notes)",
    );
    const result = stmt.run(input);
    return db
      .prepare(
        "SELECT id, title, date, location, notes, created_at as createdAt FROM appointments WHERE id = ?",
      )
      .get(result.lastInsertRowid) as Appointment;
  },
  updateAppointment(id: number, input: AppointmentInput): Appointment {
    db.prepare(
      "UPDATE appointments SET title=@title, date=@date, location=@location, notes=@notes WHERE id=@id",
    ).run({ id, ...input });
    return db
      .prepare(
        "SELECT id, title, date, location, notes, created_at as createdAt FROM appointments WHERE id = ?",
      )
      .get(id) as Appointment;
  },
  deleteAppointment(id: number) {
    db.prepare("DELETE FROM appointments WHERE id = ?").run(id);
  },

  listIntakes(): Intake[] {
    return db
      .prepare(
        `SELECT id, medication_id as medicationId, taken_at as takenAt, quantity, created_at as createdAt
         FROM intakes ORDER BY taken_at DESC`,
      )
      .all() as Intake[];
  },
  createIntake(input: IntakeInput): Intake {
    const data = { ...input, takenAt: input.takenAt ?? new Date().toISOString() };
    const stmt = db.prepare(
      "INSERT INTO intakes (medication_id, taken_at, quantity) VALUES (@medicationId, @takenAt, @quantity)",
    );
    const result = stmt.run(data);
    return db
      .prepare(
        `SELECT id, medication_id as medicationId, taken_at as takenAt, quantity, created_at as createdAt
         FROM intakes WHERE id = ?`,
      )
      .get(result.lastInsertRowid) as Intake;
  },
  deleteIntake(id: number) {
    db.prepare("DELETE FROM intakes WHERE id = ?").run(id);
  },

  stats(): DashboardStats {
    const meds = db.prepare("SELECT COUNT(*) as c FROM medications").get() as { c: number };
    const reminders = db.prepare("SELECT COUNT(*) as c FROM reminders").get() as { c: number };
    const appointments = db.prepare("SELECT COUNT(*) as c FROM appointments").get() as { c: number };
    const today = new Date().toISOString().slice(0, 10);
    const intakesToday = db
      .prepare(
        "SELECT COUNT(*) as c FROM intakes WHERE substr(taken_at, 1, 10) = @today OR substr(created_at,1,10) = @today",
      )
      .get({ today }) as { c: number };
    return {
      meds: meds.c,
      reminders: reminders.c,
      appointments: appointments.c,
      intakesToday: intakesToday.c,
    };
  },
};
