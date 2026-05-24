package com.example.medirecord4

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class BuscarMedicamentoOnlineActivity : AppCompatActivity() {

    private lateinit var etBuscar: EditText
    private lateinit var btnBuscar: Button
    private lateinit var tvResultados: TextView
    private lateinit var tvSugerencias: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_medicamento_online)

        // Habilitar botón de retroceso
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_search_medication)

        initViews()
        setupListeners()
        tvResultados.text = getString(R.string.search_initial_message)
    }

    private fun initViews() {
        etBuscar = findViewById(R.id.etBuscar)
        btnBuscar = findViewById(R.id.btnBuscar)
        tvResultados = findViewById(R.id.tvResultados)
        tvSugerencias = findViewById(R.id.tvSugerencias)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnBuscar.setOnClickListener {
            val nombreMedicamento = etBuscar.text.toString().trim()
            if (nombreMedicamento.isEmpty()) {
                tvResultados.text = getString(R.string.msg_enter_name)
                return@setOnClickListener
            }
            buscarMedicamento(nombreMedicamento)
        }
    }

    private fun buscarMedicamento(nombre: String) {
        progressBar.visibility = View.VISIBLE
        tvResultados.text = getString(R.string.msg_searching, nombre)
        tvSugerencias.visibility = View.GONE
        btnBuscar.isEnabled = false

        lifecycleScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                obtenerResumenWeb(nombre)
            }

            progressBar.visibility = View.GONE
            btnBuscar.isEnabled = true

            if (resultado != null) {
                mostrarInformacionMedicamento(resultado)
            } else {
                mostrarMedicamentoNoEncontrado(nombre)
            }
        }
    }

    private fun obtenerResumenWeb(nombre: String): ResumenMedicamento? {
        val candidatos = listOf(
            "es" to nombre,
            "es" to nombre.lowercase(),
            "en" to nombre,
            "en" to nombre.lowercase()
        )

        for ((idioma, termino) in candidatos) {
            val encoded = Uri.encode(termino.replace(" ", "_"))
            val endpoint = "https://$idioma.wikipedia.org/api/rest_v1/page/summary/$encoded"
            val conexion = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 9000
                readTimeout = 9000
                setRequestProperty("User-Agent", "MediRecord/1.0 (Android)")
            }

            try {
                if (conexion.responseCode != HttpURLConnection.HTTP_OK) {
                    continue
                }

                val payload = conexion.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(payload)
                val tipo = json.optString("type")

                if (tipo == "disambiguation") continue

                val titulo = json.optString("title")
                val descripcion = json.optString("description")
                val resumen = json.optString("extract")
                val url = json.optJSONObject("content_urls")
                    ?.optJSONObject("desktop")
                    ?.optString("page")
                    .orEmpty()

                if (titulo.isNotBlank() && resumen.isNotBlank()) {
                    return ResumenMedicamento(
                        nombre = titulo,
                        descripcion = descripcion.ifBlank { getString(R.string.search_no_type) },
                        resumen = resumen,
                        fuente = url.ifBlank { endpoint }
                    )
                }
            } catch (_: Exception) {
                // Intentar siguiente opción
            } finally {
                conexion.disconnect()
            }
        }

        return null
    }

    private fun mostrarInformacionMedicamento(resumen: ResumenMedicamento) {
        val resultado = StringBuilder()
        resultado.append(getString(R.string.search_result_name, resumen.nombre))
        resultado.append('\n')
        resultado.append(getString(R.string.search_result_type, resumen.descripcion))
        resultado.append("\n\n")
        resultado.append(getString(R.string.search_result_summary_title))
        resultado.append('\n')
        resultado.append(resumen.resumen)
        resultado.append("\n\n")
        resultado.append(getString(R.string.search_result_warning))
        tvResultados.text = resultado.toString()

        tvSugerencias.text = getString(R.string.search_result_source, resumen.fuente)
        tvSugerencias.visibility = View.VISIBLE
    }

    private fun mostrarMedicamentoNoEncontrado(nombre: String) {
        tvResultados.text = getString(R.string.search_not_found_web, nombre)
        tvSugerencias.text = getString(R.string.search_retry_tip)
        tvSugerencias.visibility = View.VISIBLE
    }

    data class ResumenMedicamento(
        val nombre: String,
        val descripcion: String,
        val resumen: String,
        val fuente: String
    )

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
