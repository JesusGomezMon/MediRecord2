package com.example.medirecord4.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medirecord4.data.MediRepository
import com.example.medirecord4.data.StatItem
import com.example.medirecord4.data.local.AppointmentEntity
import com.example.medirecord4.data.local.IntakeEntity
import com.example.medirecord4.data.local.MedicationEntity
import com.example.medirecord4.data.local.ReminderEntity
import com.example.medirecord4.data.network.WikiClient
import com.example.medirecord4.data.network.WikiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MediRepository()
    private val wiki = WikiClient()

    val medications: StateFlow<List<MedicationEntity>> =
        repo.medications().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val reminders: StateFlow<List<ReminderEntity>> =
        repo.reminders().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val intakes: StateFlow<List<IntakeEntity>> =
        repo.intakes().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val appointments: StateFlow<List<AppointmentEntity>> =
        repo.appointments().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _sensorsEnabled = MutableStateFlow(false)
    val sensorsEnabled: StateFlow<Boolean> = _sensorsEnabled

    val dashboardStats: StateFlow<DashboardState> =
        combine(medications, reminders, intakes) { meds, rems, ints ->
            DashboardState(meds, rems, ints, _sensorsEnabled.value)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, DashboardState())

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState

    fun addMedication(
        name: String,
        dose: String,
        instructions: String,
        type: String,
        permanent: Boolean,
        durationDays: Int,
        stockActual: Int,
        stockTotal: Int
    ) {
        viewModelScope.launch {
            repo.addMedication(name, dose, instructions, type, permanent, durationDays, stockActual, stockTotal)
        }
    }

    fun addReminder(medicationId: String, time: String, days: String, startDate: String) {
        viewModelScope.launch { repo.addReminder(medicationId, time, days, startDate) }
    }

    fun addIntakeDone(reminderId: String?, medicationId: String) {
        viewModelScope.launch { repo.addIntakeDone(reminderId, medicationId) }
    }

    fun addAppointment(doctor: String, specialty: String, dateTime: String, location: String, notes: String) {
        viewModelScope.launch { repo.addAppointment(doctor, specialty, dateTime, location, notes) }
    }

    fun toggleSensors() {
        _sensorsEnabled.value = !_sensorsEnabled.value
    }

    fun refreshStats() {
        viewModelScope.launch {
            val stats = repo.stats()
            _searchState.value = _searchState.value.copy(lastStats = stats)
        }
    }

    fun searchMedicineWeb(name: String) {
        if (name.isBlank()) {
            _searchState.value = SearchState(error = "Escribe un nombre de medicamento")
            return
        }
        _searchState.value = SearchState(isLoading = true)
        viewModelScope.launch {
            val result = wiki.fetch(name)
            if (result != null) {
                _searchState.value = SearchState(result = result, lastStats = _searchState.value.lastStats)
            } else {
                _searchState.value = SearchState(error = "No encontré un resumen para \"$name\". Prueba con el nombre genérico.", lastStats = _searchState.value.lastStats)
            }
        }
    }
}

data class DashboardState(
    val medications: List<MedicationEntity> = emptyList(),
    val reminders: List<ReminderEntity> = emptyList(),
    val intakes: List<IntakeEntity> = emptyList(),
    val sensorsEnabled: Boolean = false
)

data class SearchState(
    val isLoading: Boolean = false,
    val result: WikiResult? = null,
    val error: String? = null,
    val lastStats: List<StatItem> = emptyList()
)
