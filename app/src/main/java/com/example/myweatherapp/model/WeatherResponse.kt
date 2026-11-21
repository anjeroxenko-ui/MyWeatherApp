package com.example.myweatherapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "name") val name: String? = null,
    @Json(name = "weather") val weather: List<WeatherDescription>? = null
)

@JsonClass(generateAdapter = true)
data class WeatherDescription(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "main") val main: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "icon") val icon: String? = null
)