package com.example.medirecord4.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val UA = "MediRecord/1.0 (Android)"

interface WikiService {
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun summary(@Path("title") title: String): WikiSummaryDto
}

data class WikiSummaryDto(
    val type: String? = null,
    val title: String? = null,
    val description: String? = null,
    val extract: String? = null,
    val content_urls: ContentUrls? = null
) {
    data class ContentUrls(val desktop: Desktop? = null) { data class Desktop(val page: String? = null) }
}

data class WikiResult(
    val title: String,
    val description: String,
    val summary: String,
    val url: String
)

class WikiClient(
    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", UA)
                    .build()
            )
        }.build()
) {
    private fun service(lang: String): WikiService {
        return Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://$lang.wikipedia.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikiService::class.java)
    }

    suspend fun fetch(title: String): WikiResult? {
        val candidates = listOf("es", "en")
        val safeTitle = title.replace(" ", "_")
        for (lang in candidates) {
            try {
                val dto = service(lang).summary(safeTitle)
                if (dto.type == "disambiguation") continue
                val t = dto.title.orEmpty()
                val d = dto.description ?: "Información general"
                val s = dto.extract.orEmpty()
                val url = dto.content_urls?.desktop?.page ?: ""
                if (t.isNotBlank() && s.isNotBlank()) {
                    return WikiResult(t, d, s, url)
                }
            } catch (_: Exception) {
                continue
            }
        }
        return null
    }
}
