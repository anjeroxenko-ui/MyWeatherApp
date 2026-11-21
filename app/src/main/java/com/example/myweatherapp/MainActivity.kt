package com.example.myweatherapp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.myweatherapp.model.WeatherResponse
import com.example.myweatherapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // Replace with a valid OpenWeatherMap API key if available
    private val apiKey = "YOUR_API_KEY"
    private val city = "New York"

    private lateinit var tvCity: TextView
    private lateinit var tvMain: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        fetchWeatherData()
    }

    private fun initViews() {
        tvCity = findViewById(R.id.tvCity)
        tvMain = findViewById(R.id.tvMain)
        tvDescription = findViewById(R.id.tvDescription)
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun fetchWeatherData() {
        progressBar.visibility = View.VISIBLE

        RetrofitClient.weatherApiService.getWeather(city, apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            displayWeather(weatherData)
                        } else {
                            handleFailure("Empty response body from server.")
                        }
                    } else {
                        // HTTP Error (e.g., 401 Unauthorized for placeholder API Key)
                        handleFailure("API Error ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    // Network / Connection Error
                    handleFailure("Network Failure: ${t.localizedMessage}")
                }
            })
    }

    private fun displayWeather(weatherResponse: WeatherResponse) {
        tvCity.text = weatherResponse.name ?: city

        val weatherItem = weatherResponse.weather?.firstOrNull()
        if (weatherItem != null) {
            tvMain.text = weatherItem.main ?: ""
            tvDescription.text = weatherItem.description ?: ""

            weatherItem.icon?.let { iconCode ->
                loadWeatherIcon(iconCode)
            }
        }
    }

    private fun loadWeatherIcon(iconCode: String) {
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(ivWeatherIcon)
    }

    private fun handleFailure(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

        // Fallback UI population for demo / missing API key scenarios
        tvCity.text = city
        tvMain.text = "Clouds"
        tvDescription.text = "broken clouds"
        loadWeatherIcon("04d")
    }
}