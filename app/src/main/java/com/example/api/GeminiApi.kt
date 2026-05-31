package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "thinkingConfig") val thinkingConfig: ThinkingConfig? = null,
    @Json(name = "responseModalities") val responseModalities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class ThinkingConfig(
    @Json(name = "thinkingBudget") val thinkingBudget: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
        }
}

// Model structures representing our platform's rich multi-AI options
data class AIModel(
    val id: String,
    val name: String,
    val provider: String,
    val iconEmoji: String,
    val description: String,
    val category: String, // "Reasoning", "Research", "Coding", "General", "Creative"
    val systemPromptModifier: String
) {
    companion object {
        val MODELS = listOf(
            AIModel(
                id = "gemini-3.5-flash",
                name = "Gemini 3.5 Flash",
                provider = "Google",
                iconEmoji = "♊",
                description = "Google's fastest multimodal model, optimized for speed and creative tasks.",
                category = "General",
                systemPromptModifier = "You are Gemini 3.5 Flash, developed by Google. Respond with a helpful, friendly, and visually structured structure."
            ),
            AIModel(
                id = "gemini-3.1-pro-preview",
                name = "Gemini 3.1 Pro (Preview)",
                provider = "Google",
                iconEmoji = "✨",
                description = "Deep multimodal reasoning and highly complex analytical solutions.",
                category = "Reasoning",
                systemPromptModifier = "You are Gemini 3.1 Pro, Google's advanced reasoning flagship model. Ensure deep depth and rigorous analysis in your responses."
            ),
            AIModel(
                id = "gpt-4o",
                name = "GPT-4o (Nexus Proxy)",
                provider = "OpenAI",
                iconEmoji = "🤖",
                description = "Great balance of coding, creative text, and general conversational accuracy.",
                category = "General",
                systemPromptModifier = "You are GPT-4o by OpenAI, fully proxied through the Infinity Nexus AI platform. Maintain a highly professional, structured, balanced, and direct tone."
            ),
            AIModel(
                id = "claude-3-5-sonnet",
                name = "Claude 3.5 Sonnet (Nexus Proxy)",
                provider = "Anthropic",
                iconEmoji = "🎭",
                description = "The gold standard for software engineering, deep formatting, and nuanced writing.",
                category = "Coding",
                systemPromptModifier = "You are Claude 3.5 Sonnet by Anthropic, proxied through Infinity Nexus AI. Your answers should be deeply thoughtful, intellectually humble, extremely structured, and use Markdown extensively with beautiful spacing."
            ),
            AIModel(
                id = "deepseek-r1",
                name = "DeepSeek-R1 (Nexus Reasoning)",
                provider = "DeepSeek",
                iconEmoji = "🐳",
                description = "Open source math, logic, coding, and chain-of-thought capability.",
                category = "Reasoning",
                systemPromptModifier = "You are DeepSeek-R1, a powerful reasoning model. Start your response with a thinking step enclosed in a <thinking> ... </thinking> block, showing step-by-step logic, before arriving at your final structured output."
            ),
            AIModel(
                id = "grok-3",
                name = "Grok 3 (Nexus Real-Time)",
                provider = "xAI",
                iconEmoji = "👁️",
                description = "Sarcastic, direct, and witty insights backed by real-time intelligence feeds.",
                category = "Research",
                systemPromptModifier = "You are Grok 3 by xAI, proxied through Infinity Nexus AI. Be witty, slightly provocative, humorous but extremely fact-driven, utilizing up-to-the-minute details where possible."
            )
        )
    }
}
