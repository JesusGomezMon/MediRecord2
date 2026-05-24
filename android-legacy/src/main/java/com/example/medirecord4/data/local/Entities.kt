package com.example.medirecord4.data.local

data class MedicationEntity(
    val id: String,
    val userId: String,
    val name: String,
    val dose: String,
    val instructions: String = "",
    val type: String = "tableta",
    val isPermanent: Boolean = true,
    val durationDays: Int = 0,
    val stockActual: Int = 0,
    val stockTotal: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class ReminderEntity(
    val id: String,
    val medicationId: String,
    val userId: String,
    val time: String,
    val days: String,
    val startDate: String,
    val active: Boolean = true
)

data class IntakeEntity(
    val id: String,
    val medicationId: String,
    val reminderId: String?,
    val userId: String,
    val scheduledAt: String,
    val takenAt: String? = null,
    val state: String = "pendiente" // pendiente, tomado, omitido, retrasado
)

data class AppointmentEntity(
    val id: String,
    val userId: String,
    val doctorName: String,
    val specialty: String = "",
    val dateTime: String,
    val location: String = "",
    val notes: String = "",
    val status: String = "pendiente" // pendiente, asistio, no_asistio
)
