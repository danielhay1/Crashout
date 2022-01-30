package com.example.crushoutlibrary.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class DeviceMonitor(private val context: Context) {
    companion object {
        @Volatile private var instance : DeviceMonitor? = null
        @Synchronized
        fun getInstance(context: Context) : DeviceMonitor {
            if (instance == null)
                instance = DeviceMonitor(context)
            return instance as DeviceMonitor
        }
    }

    private fun batteryStatus() :Intent? {
        return IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
    }

    public fun getBatteryStatus() : Boolean {
        val batteryStatus: Intent? = batteryStatus()
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }

    public fun getBatteryLevel(): Float {
        val batteryStatus = batteryStatus()
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        return batteryPct?: -1.0f
    }
}