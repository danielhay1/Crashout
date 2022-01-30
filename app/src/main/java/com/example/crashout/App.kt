package com.example.crashout

import android.app.Application
import com.example.crushoutlibrary.objects.CrashoutExceptionHandler
import com.example.crushoutlibrary.objects.CrashPageConfig

class App : Application() {
    // Implementation Example:
    val MY_EMAIL_ADDRESS: String = "username"
    val MY_EMAIL_PASSWORD: String = "password"

    override fun onCreate() {
        super.onCreate()
        // Implementation Example:
        val config = CrashPageConfig()
        config.crashTitle = "Oh no, Application Crashed!"
        config.imagePath = R.drawable.bin_car_crash
        config.destinationActivity = MainActivity::class.java
        config.detailsButonText = "Details"
        config.restartButtonText = "Restart"
        val crashout: Thread.UncaughtExceptionHandler = CrashoutExceptionHandler(this, config, MY_EMAIL_ADDRESS, MY_EMAIL_PASSWORD, MY_EMAIL_ADDRESS)
        Thread.setDefaultUncaughtExceptionHandler(crashout)
    }
}