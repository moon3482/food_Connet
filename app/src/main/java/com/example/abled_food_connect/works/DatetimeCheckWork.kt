package com.example.abled_food_connect.works

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.abled_food_connect.R
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import androidx.work.WorkManager
import com.google.gson.annotations.SerializedName


class DatetimeCheckWork(val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        var success = true
        val getShared = context.getSharedPreferences("pref_user_data", Context.MODE_PRIVATE)
        val userIndex = getShared.getInt("user_table_id", 0)
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.dateCheck().enqueue(object : Callback<ArrayList<DatetimeCheckWork.RoomsSchedule>> {
            override fun onResponse(call: Call<ArrayList<DatetimeCheckWork.RoomsSchedule>>, response: Response<ArrayList<DatetimeCheckWork.RoomsSchedule>>) {
                if (response.body()!=null){
                    for (item in response.body()!!){
                        doWorkWithPeriodic(context,item.time,item.string)}
                    }

                success = true
            }

            override fun onFailure(call: Call<ArrayList<DatetimeCheckWork.RoomsSchedule>>, t: Throwable) {
                success = false
            }
        })
        return when (success) {
            true -> Result.success()

            else -> Result.failure()
        }

    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
    private fun doWorkWithPeriodic(context: Context,delay:Long,roomTag:String) {
        Log.d("CheckGpsWorker", "worker 시작함수 진입")
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<GpsWork>().setInitialDelay(delay,
            TimeUnit.SECONDS).addTag(roomTag).build()
        /*
            ExistingPeriodicWorkPolicy.KEEP     :  워크매니저가 실행중이 아니면 새로 실행하고, 실행중이면 아무작업도 하지 않는다.
            ExistingPeriodicWorkPolicy.REPLACE  :  워크매니저를 무조건 다시 실행한다.
         */
        WorkManager.getInstance(context).enqueueUniqueWork(roomTag,ExistingWorkPolicy.KEEP,workRequest)
    }
    inner class RoomsSchedule(@SerializedName("roomId") val string: String,@SerializedName("time")val time:Long)
}
