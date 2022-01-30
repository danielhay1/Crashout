package com.example.crushoutlibrary.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.crushoutlibrary.objects.CrashoutExceptionHandler
import com.example.crushoutlibrary.R
import com.example.crushoutlibrary.objects.CrashPageConfig
import com.example.crushoutlibrary.objects.CrashoutReport
import com.example.crushoutlibrary.utils.EmailServices.GmailSender
import com.example.crushoutlibrary.utils.FirebaseServices
import com.google.gson.Gson


class CrashActivity : AppCompatActivity() {
    private var crash_BTN_restart: Button? = null
    private var crash_BTN_details: Button? = null
    private var crashout_TV_title: TextView? = null
    private var crash_IMG_image: ImageView? = null
    private var crash_FL_back: FrameLayout? = null
    private var crashPageConfig: CrashPageConfig? = null

    var emailUserAddress: String? = null
    var emailPassword: String? = null
    var sendTo: String? = null
    var crashReport : CrashoutReport? = null
    private var firebaseServices: FirebaseServices? = null

    private val PENDING_INTENT_ID: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        firebaseServices = FirebaseServices.getInstance()
        initCrashoutData()
        sendReport()
        findViewsById()
        configPage()
        initButtons()
    }

    private fun sendReport() {
        crashReport?.let {
            Thread({ firebaseServices!!.saveCrashToFireBase(it) }).start() // Send To FireBase
            if (emailUserAddress!=null && emailPassword!=null && sendTo!=null) {    // Send Email
                safeEmailSend(
                        emailUserAddress!!,
                        emailPassword!!,
                        sendTo!!,
                        "Crashout: ${it.crashTime}",
                        crashReport.toString()
                )
            }
        }
    }

    private fun initCrashoutData() {
        crashReport = getCrashDetails()
        emailUserAddress = intent.getStringExtra(CrashoutExceptionHandler.EMAIL_USERNAME).toString()
        emailPassword = intent.getStringExtra(CrashoutExceptionHandler.EMAIL_PASSWORD).toString()
        sendTo = intent.getStringExtra(CrashoutExceptionHandler.EMAIL_SENDTO).toString()
    }

    private fun initButtons() {
        crash_BTN_details?.setOnClickListener {
            crashReport?.let{
                Log.d("my_debugger", "initButtons: crashReport = $crashReport")
                val builderDetay: AlertDialog.Builder = AlertDialog.Builder(this)
                builderDetay.setTitle("Crash details")
                builderDetay.setMessage("Crash reason: " + it.parsedThrowable.cause +
                        "\n\nCrash time: " + it.crashTime +
                        "\nBattery status: " +
                        "\nBattery level: " + it.monitorData.batteryLevel +
                        "\nBattery charging status: " + it.monitorData.isCharging
                )
                builderDetay.setPositiveButton("CLOSE", null)
                builderDetay.show()
            }
        }

        crash_BTN_restart?.setOnClickListener {
            restartApp()
        }
    }

    private fun restartApp() {
        crashPageConfig?.let {
            val startActivity = Intent(applicationContext, it.destinationActivity)
            val pendingIntent: PendingIntent  = PendingIntent.getActivity(applicationContext, PENDING_INTENT_ID, startActivity, PendingIntent.FLAG_CANCEL_CURRENT)
            val mgr = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
            System.exit(0)

            val intent = Intent(applicationContext, it.destinationActivity)
            startActivity(intent)
        }

    }

    private fun configPage() {
        val gson = Gson()
        crashPageConfig = this.intent.getSerializableExtra(CrashoutExceptionHandler.PAGE_CONFIG_KEY) as CrashPageConfig?
        crashPageConfig?.let {
            it.restartButtonText?.let { it1 -> crash_BTN_restart?.setText(it1)}
            it.detailsButonText?.let { it1 -> crash_BTN_details?.setText(it1)}
            it.crashTitle?.let { it1 -> crashout_TV_title?.setText(it1)}
            it.crashTitleColor?.let { it1 -> crashout_TV_title?.setTextColor(Color.parseColor(it1))}
            it.imagePath?.let { it1 -> crash_IMG_image?.setImageResource(it1) }
        }
    }

    private fun getCrashDetails(): CrashoutReport? {
        val gson = Gson()
        val temp:String = intent.getStringExtra(CrashoutExceptionHandler.CRASH_REPORT_KEY).toString()
        Log.d("my_debuger", "getCrashDetails: $temp")
        return gson.fromJson(temp, CrashoutReport::class.java)
    }

    private fun findViewsById() {
        crash_BTN_restart = findViewById(R.id.crash_BTN_restart)
        crash_BTN_details = findViewById(R.id.crash_BTN_details)
        crash_IMG_image = findViewById(R.id.crash_IMG_image)
        crashout_TV_title = findViewById(R.id.crashout_TV_title)
        crash_FL_back = findViewById(R.id.crash_FL_back)
    }

    private fun safeEmailSend(
            userEmail: String,
            emailPass: String,
            sendTo: String,
            subject: String,
            body: String
    ) {
        Thread({
            Log.d("my_debuger", "safeEmailSend: sending email to: " + sendTo)
            try {
                val sender = GmailSender(userEmail, emailPass)
                sender.sendMail(
                        subject,
                        body,
                        userEmail,
                        sendTo
                )
            } catch (e: java.lang.Exception) {
                Log.e(
                        "my_debuger",
                        "Email send to: \"$sendTo\" failed, exception details:\n$e.message"
                )
            }
        }).start()
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The [OnBackPressedDispatcher][.getOnBackPressedDispatcher] will be given a
     * chance to handle the back button before the default behavior of
     * [android.app.Activity.onBackPressed] is invoked.
     *
     * @see .getOnBackPressedDispatcher
     */
    override fun onBackPressed() {
        super.onBackPressed()
    }
}