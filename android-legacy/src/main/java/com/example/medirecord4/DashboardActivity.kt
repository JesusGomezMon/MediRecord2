package com.example.medirecord4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var notificationHelper: NotificationHelper
    private val usuarioActual = "usr-001" // Usuario de ejemplo
    private var isSensorServiceRunning = false
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.title = getString(R.string.dashboard_title)

        // Inicializar base de datos y helpers
        dbHelper = DatabaseHelper(this)
        notificationHelper = NotificationHelper(this)

        // Solicitar permisos necesarios
        requestNecessaryPermissions()

        // Configurar botones
        setupButtons()

        // Mostrar información de la base de datos
        mostrarInformacionDB()

        // Mostrar medicamentos para marcar como tomados
        mostrarMedicamentosParaTomar()
        applyEntranceAnimation()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnAgregarMedicamento).setOnClickListener {
            val intent = Intent(this, AgregarMedicamentoActivity::class.java)
            intent.putExtra("USUARIO_ID", usuarioActual)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAgregarRecordatorio).setOnClickListener {
            val intent = Intent(this, AgregarRecordatorioActivity::class.java)
            intent.putExtra("USUARIO_ID", usuarioActual)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAgregarCita).setOnClickListener {
            val intent = Intent(this, AgregarCitaActivity::class.java)
            intent.putExtra("USUARIO_ID", usuarioActual)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnVerHistorial).setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            intent.putExtra("USUARIO_ID", usuarioActual)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnBuscarOnline).setOnClickListener {
            val intent = Intent(this, BuscarMedicamentoOnlineActivity::class.java)
            startActivity(intent)
        }
        
        // Nuevo botón: Ver farmacias cercanas
        findViewById<Button>(R.id.btnVerFarmacias).setOnClickListener {
            // Usar versión externa (no requiere API Key)
            val intent = Intent(this, FarmaciasMapaExternoActivity::class.java)
            startActivity(intent)
        }
        
        // Nuevo botón: Activar/Desactivar servicio de sensores
        findViewById<Button>(R.id.btnActivarSensores).setOnClickListener {
            toggleSensorService()
        }
    }
    
    /**
     * Solicita todos los permisos necesarios para las funcionalidades
     */
    private fun requestNecessaryPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        // Permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        
        // Permisos de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Permisos de reconocimiento de actividad
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    /**
     * Activa o desactiva el servicio de monitoreo de sensores
     */
    private fun toggleSensorService() {
        val button = findViewById<Button>(R.id.btnActivarSensores)
        
        if (isSensorServiceRunning) {
            // Detener servicio
            val intent = Intent(this, SensorMonitorService::class.java)
            stopService(intent)
            isSensorServiceRunning = false
            button.text = getString(R.string.btn_enable_protection)
            button.setBackgroundResource(R.drawable.bg_button_secondary)
            button.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            Toast.makeText(this, getString(R.string.protection_disabled), Toast.LENGTH_SHORT).show()
        } else {
            // Iniciar servicio
            val intent = Intent(this, SensorMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isSensorServiceRunning = true
            button.text = getString(R.string.btn_disable_protection)
            button.setBackgroundResource(R.drawable.bg_button_danger)
            button.setTextColor(ContextCompat.getColor(this, R.color.surface_color))
            Toast.makeText(this, 
                getString(R.string.protection_enabled), 
                Toast.LENGTH_LONG).show()
        }

        button.animate()
            .scaleX(0.98f)
            .scaleY(0.98f)
            .setDuration(90)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i])
                }
            }
            
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.permissions_denied_some),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, getString(R.string.permissions_granted), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarMedicamentosParaTomar() {
        val container = findViewById<LinearLayout>(R.id.containerMedicamentos)
        container.removeAllViews()

        val recordatorios = dbHelper.obtenerRecordatoriosHoy()

        if (recordatorios.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = getString(R.string.empty_today_meds)
                textSize = 17f
                setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.text_secondary))
                setPadding(dp(20), dp(20), dp(20), dp(20))
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_card)
            }
            container.addView(tvVacio)
        } else {
            recordatorios.forEachIndexed { index, rec ->
                // Crear un LinearLayout VERTICAL para cada medicamento
                val itemLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(20), dp(18), dp(20), dp(18))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setBackgroundResource(R.drawable.bg_card)
                }

                val tvNombre = TextView(this).apply {
                    text = getString(R.string.med_name_format, rec["medicamento"] ?: "")
                    textSize = 20f
                    setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.text_primary))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                
                val tvDosis = TextView(this).apply {
                    text = getString(R.string.dose_format, rec["dosis"] ?: "")
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.text_secondary))
                    setPadding(0, dp(8), 0, 0)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                
                val tvHora = TextView(this).apply {
                    text = getString(R.string.time_format, rec["hora"] ?: "")
                    textSize = 17f
                    setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.primary_color))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, dp(8), 0, dp(14))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                val btnTomado = Button(this).apply {
                    text = getString(R.string.mark_taken)
                    textSize = 15f
                    setTextColor(ContextCompat.getColor(this@DashboardActivity, R.color.surface_color))
                    setBackgroundResource(R.drawable.bg_button_primary)
                    minimumHeight = dp(54)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setOnClickListener {
                        marcarComoTomado(rec["id"] ?: "", rec["medicamento"] ?: "")
                    }
                }

                // Agregar todos los elementos en orden
                itemLayout.addView(tvNombre)
                itemLayout.addView(tvDosis)
                itemLayout.addView(tvHora)
                itemLayout.addView(btnTomado)
                
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.bottomMargin = dp(14)
                itemLayout.layoutParams = params

                itemLayout.alpha = 0f
                itemLayout.translationY = dp(14).toFloat()
                container.addView(itemLayout)
                itemLayout.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(260)
                    .setStartDelay((index * 35L).coerceAtMost(180L))
                    .start()
            }
        }
    }

    private fun marcarComoTomado(recordatorioId: String, nombreMedicamento: String) {
        val historialId = "hist-${UUID.randomUUID()}"
        val fechaHoraActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        val db = dbHelper.writableDatabase
        
        // Obtener el medicamento_id del recordatorio
        val cursorMed = db.rawQuery("SELECT medicamento_id FROM recordatorios WHERE id = ?", arrayOf(recordatorioId))
        var medicamentoId: String? = null
        if (cursorMed.moveToFirst()) {
            medicamentoId = cursorMed.getString(0)
        }
        cursorMed.close()
        
        // Insertar en historial de tomas
        db.execSQL("""
            INSERT INTO historial_tomas 
            (id, recordatorio_id, medicamento_id, usuario_id, fecha_hora_programada, fecha_hora_toma, estado) 
            SELECT ?, r.id, r.medicamento_id, ?, ?, ?, 'tomado'
            FROM recordatorios r 
            WHERE r.id = ?
        """, arrayOf(historialId, usuarioActual, fechaHoraActual, fechaHoraActual, recordatorioId))
        
        // Verificar si el medicamento temporal completó su tratamiento
        if (medicamentoId != null) {
            val progreso = dbHelper.obtenerProgresoMedicamento(medicamentoId, usuarioActual)
            val esPermanente = progreso["es_permanente"] as? Boolean ?: false
            val porcentaje = (progreso["progreso"] as? String)?.toDoubleOrNull() ?: 0.0
            
            if (!esPermanente && porcentaje >= 100.0) {
                // Desactivar medicamento y recordatorios automáticamente
                dbHelper.verificarYDesactivarTratamientoCompleto(medicamentoId, usuarioActual)
                Toast.makeText(this, 
                    getString(R.string.taken_completed, nombreMedicamento), 
                    Toast.LENGTH_LONG).show()
            } else {
                if (esPermanente) {
                    Toast.makeText(this, getString(R.string.taken_permanent, nombreMedicamento), Toast.LENGTH_SHORT).show()
                } else {
                    val progresoFormateado = String.format(Locale.getDefault(), "%.0f", porcentaje)
                    Toast.makeText(this, getString(R.string.taken_progress, nombreMedicamento, progresoFormateado), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.taken_simple, nombreMedicamento), Toast.LENGTH_SHORT).show()
        }
        
        // Mostrar notificación de confirmación
        notificationHelper.showConfirmationNotification(nombreMedicamento)
        
        // Actualizar la vista
        mostrarInformacionDB()
        mostrarMedicamentosParaTomar()
    }

    private fun mostrarInformacionDB() {
        val tvInfo1 = findViewById<TextView>(R.id.tvInfo1)
        val tvInfo2 = findViewById<TextView>(R.id.tvInfo2)
        val tvInfo3 = findViewById<TextView>(R.id.tvInfo3)

        // Obtener medicamentos del usuario con progreso
        val medicamentos = dbHelper.obtenerTodosLosMedicamentos(usuarioActual)
        val textoMedicamentos = buildString {
            append("Medicamentos Registrados (${medicamentos.size}):\n\n")
            if (medicamentos.isEmpty()) {
                append("No hay medicamentos registrados")
            } else {
                medicamentos.forEach { med ->
                    val medId = med["id"] ?: ""
                    val progreso = dbHelper.obtenerProgresoMedicamento(medId, usuarioActual)
                    val esPermanente = progreso["es_permanente"] as? Boolean ?: false
                    val porcentajeProgreso = progreso["progreso"] as? String ?: "0.0"
                    
                    append("- ${med["nombre"]} - ${med["dosis"]}")
                    if (esPermanente) {
                        append(" [PERMANENTE]")
                    } else {
                        append(" [Progreso: $porcentajeProgreso%]")
                    }
                    append("\n")
                }
            }
        }
        tvInfo1.text = textoMedicamentos

        // Obtener recordatorios de hoy
        val recordatorios = dbHelper.obtenerRecordatoriosHoy()
        val textoRecordatorios = buildString {
            append("Recordatorios Activos (${recordatorios.size}):\n\n")
            if (recordatorios.isEmpty()) {
                append("No hay recordatorios activos")
            } else {
                recordatorios.take(5).forEach { rec ->
                    append("• ${rec["medicamento"]} a las ${rec["hora"]}\n")
                }
            }
        }
        tvInfo2.text = textoRecordatorios

        // Obtener estadísticas de cumplimiento
        val estadisticas = dbHelper.obtenerEstadisticasCumplimiento(usuarioActual)
        val textoHistorial = buildString {
            append("Estadísticas de Cumplimiento:\n\n")
            if (estadisticas.isEmpty()) {
                append("No hay datos de cumplimiento aún")
            } else {
                estadisticas.forEach { est ->
                    append("• ${est["medicamento"]}: ${est["porcentaje"]}%\n")
                    append("  (${est["realizadas"]}/${est["total"]} tomas)\n\n")
                }
            }
        }
        tvInfo3.text = textoHistorial
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la información cada vez que se vuelve a esta pantalla
        mostrarInformacionDB()
        mostrarMedicamentosParaTomar()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    private fun applyEntranceAnimation() {
        val animatedViews = listOf(
            findViewById<View>(R.id.btnAgregarMedicamento),
            findViewById<View>(R.id.btnAgregarRecordatorio),
            findViewById<View>(R.id.btnAgregarCita),
            findViewById<View>(R.id.btnVerHistorial),
            findViewById<View>(R.id.btnBuscarOnline),
            findViewById<View>(R.id.btnVerFarmacias),
            findViewById<View>(R.id.btnActivarSensores),
            findViewById<View>(R.id.tvInfo1),
            findViewById<View>(R.id.tvInfo2),
            findViewById<View>(R.id.tvInfo3)
        )

        animatedViews.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = dp(16).toFloat()
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(320)
                .setStartDelay((index * 45L).coerceAtMost(260L))
                .start()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
