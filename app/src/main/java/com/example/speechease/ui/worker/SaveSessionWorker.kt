package com.example.speechease.ui.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.speechease.di.Injection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SaveSessionWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.d("SaveSessionWorker", "doWork() started")
        val userRepository = Injection.provideRepository(applicationContext)
        val session = runBlocking { userRepository.getSession().first() }

        val updatedSession = session.copy(token = session.token)

        runBlocking {
            userRepository.saveSession(updatedSession)
        }
        Log.d("SaveSessionWorker", "Session data saved to DataStore: $updatedSession")
        Log.d("SaveSessionWorker", "doWork() finished")

        return Result.success()
    }
}