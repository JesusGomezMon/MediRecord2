package com.example.medirecord4.data

import com.example.medirecord4.data.local.AppointmentEntity
import com.example.medirecord4.data.local.IntakeEntity
import com.example.medirecord4.data.local.InMemoryStore
import com.example.medirecord4.data.local.MedicationEntity
import com.example.medirecord4.data.local.ReminderEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MediRepository(
    private val store: InMemoryStore = InMemoryStore.instance,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    private val userId = "usr-001"

    fun medications(): Flow<List<MedicationEntity>> = store.medications
    fun reminders(): Flow<List<ReminderEntity>> = store.reminders
    fun intakes(): Flow<List<IntakeEntity>> = store.intakes
    fun appointments(): Flow<List<AppointmentEntity>> = store.appointments

    suspend fun stats(): List<StatItem> = withContext(io) {
        store.intakes.value.groupBy { it.medicationId }.map { (medId, list) ->
            val med = store.medications.value.find { it.id == medId }
            val total = list.size
            val done = list.count { it.state == "tomado" }
            val percent = if (total > 0) done * 100.0 / total else 0.0
            StatItem(med?.name ?: medId, med?.dose ?: "", percent)
        }
    }

    suspend fun addMedication(
        name: String,
        dose: String,
        instructions: String,
        type: String,
        permanent: Boolean,
        durationDays: Int,
        stockActual: Int,
        stockTotal: Int
    ) = withContext(io) {
        val med = MedicationEntity(
            id = store.newId("med"),
            userId = userId,
            name = name,
            dose = dose,
            instructions = instructions,
            type = type,
            isPermanent = permanent,
            durationDays = durationDays,
            stockActual = stockActual,
            stockTotal = stockTotal
        )
        store.medications.update { it + med }
    }

    suspend fun addReminder(
        medicationId: String,
        time: String,
        days: String,
        startDate: String
    ) = withContext(io) {
        val reminder = ReminderEntity(
            id = store.newId("rec"),
            medicationId = medicationId,
            userId = userId,
            time = time,
            days = days,
            startDate = startDate
        )
        store.reminders.update { it + reminder }
    }

    suspend fun addIntakeDone(reminderId: String?, medicationId: String) = withContext(io) {
        val now = nowString()
        val intake = IntakeEntity(
            id = store.newId("hist"),
            medicationId = medicationId,
            reminderId = reminderId,
            userId = userId,
            scheduledAt = now,
            takenAt = now,
            state = "tomado"
        )
        store.intakes.update { listOf(intake) + it }
    }

    suspend fun addAppointment(
        doctor: String,
        specialty: String,
        dateTime: String,
        location: String,
        notes: String
    ) = withContext(io) {
        val appointment = AppointmentEntity(
            id = store.newId("cita"),
            userId = userId,
            doctorName = doctor,
            specialty = specialty,
            dateTime = dateTime,
            location = location,
            notes = notes
        )
        store.appointments.update { it + appointment }
    }

    private fun nowString(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
}

data class StatItem(
    val name: String,
    val dose: String,
    val percent: Double
)
