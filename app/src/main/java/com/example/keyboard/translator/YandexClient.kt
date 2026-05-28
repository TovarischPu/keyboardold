package com.example.keyboard.translator

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class GptRequest(val message: String)
data class GptResponse(val response: String)

interface GatewayApi {
    @POST("generate")
    suspend fun generate(@Body request: GptRequest): GptResponse
}

object GptClient {
    private const val BASE_URL = "https://d5dsmjrjmldsfs2k70o0.628pfjdx.apigw.yandexcloud.net/"

    private val api: GatewayApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GatewayApi::class.java)
    }

    suspend fun askModel(userText: String): Result<String> = try {
        val response = api.generate(GptRequest(message = userText))
        Result.success(response.response)
    } catch (e: Exception) {
        Result.failure(e)
    }
}