package com.example.abled_food_connect.works

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class ScheduleCheckWork(val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        Log.e("정각에 스케쥴 두워크", "실행중")
        return Result.success()
    }


    private fun doWorkWithPeriodic(context: Context, delay: Long, roomTag: String) {
        Log.d("CheckGpsWorker", "worker 시작함수 진입")
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<GpsWork>().setInitialDelay(
            delay,
            TimeUnit.SECONDS
        ).addTag(roomTag).build()
        /*
            ExistingPeriodicWorkPolicy.KEEP     :  워크매니저가 실행중이 아니면 새로 실행하고, 실행중이면 아무작업도 하지 않는다.
            ExistingPeriodicWorkPolicy.REPLACE  :  워크매니저를 무조건 다시 실행한다.
         */
        WorkManager.getInstance(context).enqueueUniqueWork(
            roomTag,
            ExistingWorkPolicy.KEEP, workRequest
        )
    }

    fun check(tag: String): Boolean {
        val instance = WorkManager.getInstance(context)
        var status: ListenableFuture<MutableList<WorkInfo>> = instance.getWorkInfosByTag(tag)
        try {
            var running = false;
            var list: MutableList<WorkInfo> = status.get()
            for (workInfo in list) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            return running
        } catch (e: ExecutionException) {
            return false
        } catch (e: InterruptedException) {
            return false
        }
    }

}