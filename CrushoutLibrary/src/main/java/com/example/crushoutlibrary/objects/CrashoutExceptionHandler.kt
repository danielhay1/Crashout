package com.example.crushoutlibrary.objects

import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.example.crushoutlibrary.activities.CrashActivity
import com.example.crushoutlibrary.utils.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class CrashoutExceptionHandler(
    private val context: Context,
    private val configPage: CrashPageConfig,
    val emailUserAddress: String?,
    val emailPassword: String?,
    val sendTo: String?

) : Thread.UncaughtExceptionHandler {
    /**
     * Class that uses as UncaughtExceptionHandler.
     *
     */

    companion object {
        val PAGE_CONFIG_KEY :String? = "page_config"
        val CRASH_REPORT_KEY :String? = "crash_report"
        val EMAIL_USERNAME :String? = "email_username"
        val EMAIL_PASSWORD :String? = "email_password"
        val EMAIL_SENDTO :String? = "email_send_to"

    }

    // Constractor without email sending
    constructor(context: Context, configPage: CrashPageConfig) : this(
        context,
        configPage,
        null,
        null,
        null
    ) { }

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private var monitor: DeviceMonitor? = null
    private var firebaseServices: FirebaseServices? = null

    init {
        firebaseServices = FirebaseServices.getInstance()
        monitor = DeviceMonitor.getInstance(context)
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Log.d("my_debuger", "CrashoutExceptionHandler: Crushout Object Initiate")
    }

    private fun getCurrentTime(format: String): String {
        /**
         * Method generates String current date and time by input format.
         * Get: String format
         * Return: String date and time.
         */
        val simpleDateFormat = SimpleDateFormat(format)
        val date = Date(System.currentTimeMillis())
        return simpleDateFormat.format(date)
    }

    // Method to handle the
    // uncaught exception
    override fun uncaughtException(t: Thread, e: Throwable) {
        // Custom task that needs to be
        // performed when an exception occurs
        Log.d("my_debuger", "uncaughtException: CrashoutExceptionHandler in progress...")
        try {
            val gson = Gson()
            val crashTime = getCurrentTime("yyyy-MM-dd HH:mm:ss")
            // Create CrashReport & covertToJson
            val crashReport: CrashoutReport =
                monitor?.let { CrashoutReport(e, crashTime, it) } ?: CrashoutReport(e, crashTime)
            val jsonValue = gson.toJson(crashReport)
            startCrashPage(jsonValue)
        } catch (ex: Exception) {
            Log.e("my_debuger", "CrashoutExceptionHandler: Exception:" + ex)
            ex.printStackTrace()
            defaultHandler?.uncaughtException(t,e)
        }
    }


    private fun startCrashPage(jsonCrashoutReport: String) {
        try {
            Log.d("my_debuger", "startCrashPage: ")
            val intent = Intent(context, CrashActivity::class.java)
            intent.putExtra(CRASH_REPORT_KEY, jsonCrashoutReport)
            configPage?.let { intent.putExtra(PAGE_CONFIG_KEY, it) }
            intent.putExtra(EMAIL_USERNAME, emailUserAddress)
            intent.putExtra(EMAIL_PASSWORD, emailPassword)
            intent.putExtra(EMAIL_SENDTO, sendTo)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Process.killProcess(Process.myPid())
            System.exit(0)
        } catch (ex: Exception) {
            Log.e("my_debuger", "startCrashPage Exception: $ex" )
        }
    }
}
