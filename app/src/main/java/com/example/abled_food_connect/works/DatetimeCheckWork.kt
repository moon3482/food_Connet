package com.example.abled_food_connect.works

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.abled_food_connect.R
import com.example.abled_food_connect.RoomInformationActivity
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class DatetimeCheckWork(val context:Context,workerParameters: WorkerParameters): CoroutineWorker(context,workerParameters) {
    override suspend fun doWork(): Result {

        val getShared = context.getSharedPreferences("pref_user_data",Context.MODE_PRIVATE)
        val userIndex = getShared.getString("user_table_id",null)
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.

        return Result.success()
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