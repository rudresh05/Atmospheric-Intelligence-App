package com.rudresh05.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rudresh05.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import android.widget.SearchView
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//api key = 8c43e6d9c6c5dc0a1e56800cea1e08d3
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("mirzapur")
        SearchCity()
    }

    private fun SearchCity(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    fetchWeatherData(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                   return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String?) {
       val retrofit = Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiIntrerface::class.java)

        val response = retrofit.getWeatherData(cityName,"8c43e6d9c6c5dc0a1e56800cea1e08d3","metric")
        response.enqueue(object : Callback<WeatherApp>
        {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherApp?>,
                response: Response<WeatherApp?>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    binding.location.text = responseBody.name
                    binding.currentCondition.text = condition
                    binding.today.text ="Today"
                    binding.days.text = dayName(System.currentTimeMillis())
//                    Log.d("TAG", "onResponse: $condition")

//                    Log.d("TAG", "onResponse: $temperature")
                    binding.currentTemp.text = "$temperature °C"
                    binding.maxTemp.text = "Max : $temperature °C"
                    binding.minTemp.text = "Min : $temperature °C"
                    binding.humidityP.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed M/s"
                    binding.sunSet.text = time(sunSet)
                    binding.sunRise.text = time(sunRise)
                    binding.seaLevel.text = "$seaLevel hPa"
                    binding.condition.text = responseBody.weather.firstOrNull()?.main?: "unknown"
                    binding.date.text = date()
//                    binding.days.text = responseBody.name

                    when(condition){
                        "Clear Sky", "Sunny", "Clear" -> {
                            binding.lottieAnimationView.setAnimation(R.raw.sun)
                            binding.root.setBackgroundResource(R.drawable.sunny_background)
                        }
                        "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                            binding.lottieAnimationView.setAnimation(R.raw.cloud)
                            binding.root.setBackgroundResource(R.drawable.colud_background)
                        }
                        "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                            binding.lottieAnimationView.setAnimation(R.raw.rain)
                            binding.root.setBackgroundResource(R.drawable.rain_background)
                        }
                        "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                            binding.lottieAnimationView.setAnimation(R.raw.snow)
                            binding.root.setBackgroundResource(R.drawable.snow_background)
                        }
                        else -> {
                            binding.lottieAnimationView.setAnimation(R.raw.sun)
                        }
                    }
                }
            }



            override fun onFailure(
                call: Call<WeatherApp?>,
                t: Throwable
            ) {
                TODO("Not yet implemented")
            }

            fun dayName(timeStamp : Long) : String{
                val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
                return sdf.format((Date()))
            }
            fun time(timeStamp : Long) : String? {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                return sdf.format(Date(timeStamp*1000))
            }
            private fun date():String {
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                return sdf.format((Date()))
            }

        })
    }
}


