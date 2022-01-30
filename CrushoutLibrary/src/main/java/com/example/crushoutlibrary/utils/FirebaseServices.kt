package com.example.crushoutlibrary.utils

import android.util.Log
import com.example.crushoutlibrary.objects.CrashoutReport
import com.google.firebase.database.*
import com.google.gson.Gson

class FirebaseServices() {

    interface CallBack_LoadCrashout {
        fun onDataReady(result: CrashoutReport?)
        fun onDataReady(result: ArrayList<CrashoutReport>?)
        fun loadFailed(e: Exception?)
    }

    private val CRASHOUT_ROOT: String = "crashout_root"

    companion object {
        @Volatile private var instance : FirebaseServices? = null
        private var database: FirebaseDatabase? = null
        @Synchronized
        fun getInstance() : FirebaseServices {
            if (instance == null) {
                this.database = FirebaseDatabase.getInstance()
                this.instance = FirebaseServices()
            }
            Log.d("my_debuger", "Init: FirebaseServices")
            return instance as FirebaseServices
        }
    }

    fun saveCrashToFireBase(crashoutReport: CrashoutReport) {
        if (!crashoutReport.crashTime.equals("", true)) {
            database?.let {
                val gson = Gson()
                val myRef = it.getReference(CRASHOUT_ROOT)
                myRef.child(crashoutReport.crashTime).setValue(gson.toJson(crashoutReport))
                Log.d("my_debuger", "saveCrashToFireBase: crashKey: " + crashoutReport.crashTime)

            }
        }
    }

    fun deleteCrashFromFireBase(crashoutReport: CrashoutReport) {
        val ref = FirebaseDatabase.getInstance().getReference(CRASHOUT_ROOT)
        ref?.let {
            val applesQuery = it.child(crashoutReport.crashTime!!).equalTo(crashoutReport.crashTime)
            applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot != null) {
                        Log.d("my_debuger", "DELETE CRASHOUT REPORT!")
                        dataSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("my_debuger", "onCancelled", databaseError.toException())
                }
            })
        }

    }

    fun loadCrashFromFireBase(key: String, callBack_LoadCrashout: CallBack_LoadCrashout) {
        val myRef = database?.getReference(CRASHOUT_ROOT)
        myRef?.let {
            it.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot != null) {
                        val value: CrashoutReport? = snapshot.getValue(CrashoutReport::class.java)
                        Log.d("my_debuger", "Value is: $value")
                        callBack_LoadCrashout.onDataReady(value)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callBack_LoadCrashout.loadFailed(error.toException())
                }
            })
        }
    }

    fun loadAllCrashFromFireBase(key: String, callBack_LoadCrashout: CallBack_LoadCrashout) {
        val myRef = database?.getReference(CRASHOUT_ROOT)
        myRef?.let {
            it.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot != null) {
                        val value: ArrayList<CrashoutReport>? = snapshot.getValue(ArrayList::class.java) as ArrayList<CrashoutReport>?
                        Log.d("my_debuger", "Value is: $value")
                        callBack_LoadCrashout.onDataReady(value)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callBack_LoadCrashout.loadFailed(error.toException())
                }
            })
        }
    }






}