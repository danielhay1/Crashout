package com.example.crushoutlibrary.objects

import android.os.Parcel
import java.io.Serializable

class CrashPageConfig() : Serializable {
    /**
     * @param DestinationActivity will opened after the crash, MUST BE UR STARTING ACTIVITY
     */
    var destinationActivity: Class<*>? = null
    var crashTitle: String? = null
    var crashTitleColor: String? = null
    var detailsButonText: String? = null
    var restartButtonText: String? = null
    var imagePath: Int? = null
}