package com.example.sjtu_network

import android.app.Application

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            throwable.printStackTrace()
            throw throwable
        }
    }
}