package com.example.crashout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generateException()
    }

    private fun generateException() :Int {
        /**
         * Function that create Exception - use to trigger CrashoutExceptionHandler
         */
        return 1/0
    }



}