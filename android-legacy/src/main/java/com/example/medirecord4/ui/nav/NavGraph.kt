package com.example.medirecord4.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medirecord4.ui.AppViewModel
import com.example.medirecord4.ui.screens.AppointmentScreen
import com.example.medirecord4.ui.screens.DashboardScreen
import com.example.medirecord4.ui.screens.HistoryScreen
import com.example.medirecord4.ui.screens.MedicationScreen
import com.example.medirecord4.ui.screens.PharmacyScreen
import com.example.medirecord4.ui.screens.ReminderScreen
import com.example.medirecord4.ui.screens.SearchScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val SEARCH = "search"
    const val MED = "medication"
    const val REMINDER = "reminder"
    const val APPOINTMENT = "appointment"
    const val HISTORY = "history"
    const val PHARMACY = "pharmacy"
}

@Composable
fun AppNavHost(navController: NavHostController, vm: AppViewModel = viewModel()) {
    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                nav = navController,
                state = vm.dashboardStats.collectAsState(),
                reminders = vm.reminders.collectAsState(),
                onMarkTaken = vm::addIntakeDone,
                onToggleSensors = vm::toggleSensors
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                state = vm.searchState.collectAsState(),
                onSearch = vm::searchMedicineWeb,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MED) {
            MedicationScreen(
                meds = vm.medications.collectAsState(),
                onSave = vm::addMedication,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.REMINDER) {
            ReminderScreen(
                meds = vm.medications.collectAsState(),
                onSave = vm::addReminder,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.APPOINTMENT) {
            AppointmentScreen(
                appointments = vm.appointments.collectAsState(),
                onSave = vm::addAppointment,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                intakes = vm.intakes.collectAsState(),
                statsProvider = { vm.refreshStats() },
                stats = vm.searchState.collectAsState().value.lastStats,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PHARMACY) {
            PharmacyScreen(onBack = { navController.popBackStack() })
        }
    }
}
