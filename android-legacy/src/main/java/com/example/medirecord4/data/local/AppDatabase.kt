package com.example.medirecord4.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryStore {
    val medications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val reminders = MutableStateFlow<List<ReminderEntity>>(emptyList())
    val intakes = MutableStateFlow<List<IntakeEntity>>(emptyList())
    val appointments = MutableStateFlow<List<AppointmentEntity>>(emptyList())

    init {
        seed()
    }

    private fun seed() {
        val userId = "usr-001"
        medications.value = listOf(
            MedicationEntity(
                id = "med-001", userId = userId,
                name = "Metformina", dose = "500mg",
                instructions = "Tomar con alimentos",
                type = "tableta", isPermanent = true,
                stockActual = 28, stockTotal = 30
            ),
            MedicationEntity(
                id = "med-002", userId = userId,
                name = "Losartán", dose = "50mg",
                instructions = "Tomar en ayunas",
                type = "tableta", isPermanent = true,
                stockActual = 15, stockTotal = 30
            )
        )
        reminders.value = listOf(
            ReminderEntity(
                id = "rec-001", medicationId = "med-001", userId = userId,
                time = "08:00", days = "LUN,MAR,MIE,JUE,VIE,SAB,DOM", startDate = "2024-01-01"
            ),
            ReminderEntity(
                id = "rec-002", medicationId = "med-001", userId = userId,
                time = "20:00", days = "LUN,MAR,MIE,JUE,VIE,SAB,DOM", startDate = "2024-01-01"
            )
        )
        intakes.value = listOf(
            IntakeEntity(
                id = "hist-001", medicationId = "med-001", reminderId = "rec-001",
                userId = userId, scheduledAt = "2024-05-23 08:00", takenAt = "2024-05-23 08:05", state = "tomado"
            )
        )
        appointments.value = listOf(
            AppointmentEntity(
                id = "cita-001", userId = userId,
                doctorName = "Dra. Laura Sánchez",
                specialty = "Medicina interna",
                dateTime = "2024-06-10 10:00",
                location = "Clínica Central, Piso 2",
                notes = "Traer últimos estudios"
            )
        )
    }

    fun newId(prefix: String): String = "$prefix-${UUID.randomUUID()}"

    companion object {
        val instance by lazy { InMemoryStore() }
    }
}
