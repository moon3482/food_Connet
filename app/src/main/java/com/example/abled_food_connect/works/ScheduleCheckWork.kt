package com.example.abled_food_connect.works

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ScheduleCheckWork(val context:Context,workerParameters: WorkerParameters):Worker(context,workerParameters) {
    override fun doWork(): Result {

        return Result.success()
    }
}