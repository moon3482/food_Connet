package com.example.abled_food_connect.works

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.util.TimeFormatException
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.abled_food_connect.retrofit.MapSearch
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class GpsWork(val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    lateinit var locatioNManager: LocationManager
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    var perf = context.getSharedPreferences("pref_user_data", 0)
    val userIndex = perf.getInt("user_table_id", 0)
    val roomId = inputData.getString("roomId");

    override suspend fun doWork(): Result {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        val startdate = dateFormat.format(date)
        try {
            return withTimeout((60 * 60 * 1000)) {
                while (true) {
                    Log.e("시작시간", "시간은 $startdate")
                    Log.e("시작방이름", "방이름은 $roomId")
                    gps()
//                    Thread.sleep((3 * 60 * 1000))
                    Thread.sleep((3 * 60 * 1000))
                }
                return@withTimeout Result.success()
            }

        } catch (e: TimeoutCancellationException) {
            WorkManager.getInstance(context).cancelUniqueWork(roomId!!)
            return Result.success()
        }


    }

    private fun gps() {
        locatioNManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!
        var userLocation: Location? = getLatLng()
        if (userLocation != null) {
            currentLatitude = userLocation.latitude
            currentLongitude = userLocation.longitude
            Log.d("CheckCurrentLocation", "현재 내 위치 값: ${currentLatitude}, ${currentLongitude}")
            serverGps(
                userIndex,
                currentLatitude.toString(),
                currentLongitude.toString()
            )
        }
    }

    private fun getLatLng(): Location? {
        val isGPSEnabled = locatioNManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        val isNetworkEnabled = locatioNManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        var currentLatLng: Location? = null
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            if (isNetworkEnabled && !isGPSEnabled) {

                val locatioNProvider = LocationManager.NETWORK_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            } else {
                val locatioNProvider = LocationManager.GPS_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            }
            if (currentLatLng == null) {
                val locatioNProvider = LocationManager.NETWORK_PROVIDER
                currentLatLng = locatioNManager.getLastKnownLocation(locatioNProvider)
            }

        } else {

            currentLatLng = getLatLng()
        }
        return currentLatLng
    }

    private fun serverGps(userIndex: Int, x: String, y: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://52.78.107.230/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java).update(userIndex, x, y).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.body() == "true") {
                    Log.e("업뎃 성공", "업뎃 성공")

                } else {
                    Log.e("업뎃 실패", "업뎃 실패")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })

    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

}