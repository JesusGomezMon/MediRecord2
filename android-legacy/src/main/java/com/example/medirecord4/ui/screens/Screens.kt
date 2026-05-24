package com.example.medirecord4.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.medirecord4.data.StatItem
import com.example.medirecord4.data.local.AppointmentEntity
import com.example.medirecord4.data.local.IntakeEntity
import com.example.medirecord4.data.local.MedicationEntity
import com.example.medirecord4.data.local.ReminderEntity
import com.example.medirecord4.data.network.WikiResult
import com.example.medirecord4.ui.DashboardState
import com.example.medirecord4.ui.SearchState
import com.example.medirecord4.ui.nav.Routes
import com.example.medirecord4.ui.theme.Background
import com.example.medirecord4.ui.theme.Border
import com.example.medirecord4.ui.theme.Primary
import com.example.medirecord4.ui.theme.Surface
import com.example.medirecord4.ui.theme.TextPrimary
import com.example.medirecord4.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    nav: NavHostController,
    state: State<DashboardState>,
    reminders: State<List<ReminderEntity>>,
    onMarkTaken: (String?, String) -> Unit,
    onToggleSensors: () -> Unit
) {
    val data = state.value
    Scaffold(
        topBar = {
            SimpleTopBar(title = "MediRecord", subtitle = "Panel principal")
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrimaryButtons(nav, data.sensorsEnabled, onToggleSensors)
            SectionCard(title = "Medicamentos de hoy") {
                if (reminders.value.isEmpty()) {
                    Text("No hay recordatorios activos.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    reminders.value.forEach {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            val med = data.medications.find { m -> m.id == it.medicationId }
                            Text(med?.name ?: "Medicamento", fontWeight = FontWeight.Bold)
                            Text("Hora: ${it.time}", color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onMarkTaken(it.id, it.medicationId) },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Marcar como tomado") }
                        }
                    }
                }
            }
            SectionCard(title = "Medicamentos") {
                if (data.medications.isEmpty()) {
                    Text("Aún no hay medicamentos.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    data.medications.forEach {
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(it.name, fontWeight = FontWeight.Bold)
                            Text(it.dose, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryButtons(nav: NavHostController, sensorsEnabled: Boolean, onToggleSensors: () -> Unit) {
    SectionCard(title = "Acciones rápidas") {
        val actions = listOf(
            "Agregar medicamento" to { nav.navigate(Routes.MED) },
            "Agregar recordatorio" to { nav.navigate(Routes.REMINDER) },
            "Agregar cita" to { nav.navigate(Routes.APPOINTMENT) },
            "Consultar medicamento (web)" to { nav.navigate(Routes.SEARCH) },
            "Farmacias cercanas" to { nav.navigate(Routes.PHARMACY) },
            "Historial y estadísticas" to { nav.navigate(Routes.HISTORY) },
            (if (sensorsEnabled) "Desactivar protección" else "Activar protección") to onToggleSensors
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(actions) { (label, handler) ->
                Button(
                    onClick = handler,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(label, textAlign = TextAlign.Center) }
            }
        }
    }
}

@Composable
fun SearchScreen(
    state: State<SearchState>,
    onSearch: (String) -> Unit,
    onBack: () -> Unit
) {
    val st = state.value
    val query = remember { mutableStateOf("") }
    Scaffold(
        topBar = { SimpleTopBar(title = "Buscar medicamento", onBack = onBack) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Nombre del medicamento") {
                OutlinedTextField(
                    value = query.value,
                    onValueChange = { query.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. paracetamol") }
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { onSearch(query.value) },
                    enabled = !st.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (st.isLoading) "Buscando…" else "Buscar")
                }
            }
            SectionCard(title = "Resultado") {
                when {
                    st.isLoading -> Text("Buscando en la web…", color = TextSecondary)
                    st.error != null -> Text(st.error, color = TextSecondary)
                    st.result != null -> ResultBlock(st.result)
                    else -> Text("Ingresa un nombre y presiona buscar.", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ResultBlock(result: WikiResult) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(result.title, style = MaterialTheme.typography.headlineMedium)
        Text(result.description, color = TextSecondary)
        Text(result.summary, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
        if (result.url.isNotBlank()) {
            Text("Fuente: ${result.url}", color = Primary, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            "Aviso: Esta información es orientativa. Consulte siempre a su médico.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun MedicationScreen(
    meds: State<List<MedicationEntity>>,
    onSave: (String, String, String, String, Boolean, Int, Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val name = remember { mutableStateOf("") }
    val dose = remember { mutableStateOf("") }
    val instructions = remember { mutableStateOf("") }
    val permanent = remember { mutableStateOf(true) }
    val duration = remember { mutableStateOf("0") }
    val stock = remember { mutableStateOf("0") }
    val total = remember { mutableStateOf("0") }

    Scaffold(
        topBar = { SimpleTopBar(title = "Agregar medicamento", onBack = onBack) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Datos") {
                OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dose.value, onValueChange = { dose.value = it }, label = { Text("Dosis") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instructions.value, onValueChange = { instructions.value = it }, label = { Text("Indicaciones") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = duration.value,
                        onValueChange = { duration.value = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Duración días (si temporal)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stock.value,
                        onValueChange = { stock.value = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Stock actual") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = total.value,
                    onValueChange = { total.value = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Stock total") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { permanent.value = true }, modifier = Modifier.weight(1f), enabled = !permanent.value) { Text("Permanente") }
                    Button(onClick = { permanent.value = false }, modifier = Modifier.weight(1f), enabled = permanent.value) { Text("Temporal") }
                }
                Button(
                    onClick = {
                        onSave(
                            name.value,
                            dose.value,
                            instructions.value,
                            "tableta",
                            permanent.value,
                            duration.value.toIntOrNull() ?: 0,
                            stock.value.toIntOrNull() ?: 0,
                            total.value.toIntOrNull() ?: 0
                        )
                        name.value = ""; dose.value = ""; instructions.value = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar") }
            }
            SectionCard(title = "Mis medicamentos") {
                meds.value.forEach {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(it.name, fontWeight = FontWeight.Bold)
                        Text(it.dose, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderScreen(
    meds: State<List<MedicationEntity>>,
    onSave: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val medId = remember { mutableStateOf(meds.value.firstOrNull()?.id ?: "") }
    val time = remember { mutableStateOf("08:00") }
    val days = remember { mutableStateOf("LUN,MAR,MIE,JUE,VIE,SAB,DOM") }
    val start = remember { mutableStateOf("2024-06-01") }
    Scaffold(
        topBar = { SimpleTopBar(title = "Agregar recordatorio", onBack = onBack) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Recordatorio") {
                OutlinedTextField(value = medId.value, onValueChange = { medId.value = it }, label = { Text("ID medicamento (elige de la lista)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = time.value, onValueChange = { time.value = it }, label = { Text("Hora (HH:mm)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = days.value, onValueChange = { days.value = it }, label = { Text("Días (ej. LUN,MAR,MIÉ)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = start.value, onValueChange = { start.value = it }, label = { Text("Fecha inicio YYYY-MM-DD") }, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = { onSave(medId.value, time.value, days.value, start.value) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medId.value.isNotBlank()
                ) { Text("Guardar") }
            }
            SectionCard(title = "IDs disponibles") {
                meds.value.forEach {
                    Text("${it.id} • ${it.name}", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun AppointmentScreen(
    appointments: State<List<AppointmentEntity>>,
    onSave: (String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val doctor = remember { mutableStateOf("") }
    val specialty = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("2024-06-10 10:00") }
    val location = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    Scaffold(
        topBar = { SimpleTopBar(title = "Agregar cita", onBack = onBack) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Cita") {
                OutlinedTextField(value = doctor.value, onValueChange = { doctor.value = it }, label = { Text("Doctor/a") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = specialty.value, onValueChange = { specialty.value = it }, label = { Text("Especialidad") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date.value, onValueChange = { date.value = it }, label = { Text("Fecha y hora") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location.value, onValueChange = { location.value = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes.value, onValueChange = { notes.value = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        onSave(doctor.value, specialty.value, date.value, location.value, notes.value)
                        doctor.value = ""; specialty.value = ""; notes.value = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Guardar") }
            }
            SectionCard(title = "Próximas citas") {
                appointments.value.forEach {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(it.doctorName, fontWeight = FontWeight.Bold)
                        Text("${it.specialty} · ${it.dateTime}", color = TextSecondary)
                        if (it.location.isNotBlank()) Text(it.location, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    intakes: State<List<IntakeEntity>>,
    statsProvider: () -> Unit,
    stats: List<StatItem>,
    onBack: () -> Unit
) {
    statsProvider()
    Scaffold(
        topBar = { SimpleTopBar(title = "Historial", onBack = onBack) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Últimas tomas") {
                if (intakes.value.isEmpty()) {
                    Text("Sin registros todavía", color = TextSecondary)
                } else {
                    intakes.value.take(20).forEach {
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text("${it.state.uppercase()} · ${it.scheduledAt}", fontWeight = FontWeight.Bold)
                            Text("Medicamento: ${it.medicationId}", color = TextSecondary)
                        }
                    }
                }
            }
            SectionCard(title = "Cumplimiento") {
                if (stats.isEmpty()) {
                    Text("Aún no hay datos de cumplimiento", color = TextSecondary)
                } else {
                    stats.forEach {
                        Text("${it.name} (${it.dose}) - ${"%.0f".format(it.percent)}%", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacyScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    Scaffold(
        topBar = { SimpleTopBar(title = "Farmacias cercanas", onBack = onBack) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Abrir en mapas") {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=farmacias"))
                        ctx.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Buscar farmacias cerca de mí") }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/farmacias"))
                        ctx.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Abrir búsqueda en Maps") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.headlineMedium)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                Button(onClick = onBack, shape = RoundedCornerShape(12.dp)) { Text("Atrás") }
            }
        },
        scrollBehavior = null
    )
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Surface, RoundedCornerShape(18.dp))
            .padding(18.dp)
            .animateContentSize()
            .border(0.5.dp, Border, RoundedCornerShape(18.dp))
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
