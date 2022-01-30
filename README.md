# Crashout [![](https://jitpack.io/v/danielhay1/Crashout.svg)](https://jitpack.io/#danielhay1/Crashout)
<img src="https://i.ibb.co/mB8Bgzn/crushout-Logo.png" align="left"
width="250" hspace="10" vspace="10">
## Description:
Android library to handle uncaught exceptions.
Crashout will start a default Activity on a crash that can be customized.</br>
In addition, on a crash, the Library will upload crash details to Firebase (real-time Database) and notify by email the application owner (in the case credentials were provided).
## Highlight features:
- Customizable crash activity.
- Integrated with Firebase library and managed all application reported crashes there.
- Optional: adding your google credentials will allow the library to notify the crash details from your email to which email addresses you desire.
## Screenshots:
<a href="https://ibb.co/YyRrCr4"><img src="https://i.ibb.co/VQjK1K0/Screenshot-1643496422.png" alt="Screenshot-1643496422" border="0" width="300" hspace="10"><a href="https://ibb.co/7SMBdsw"><img src="https://i.ibb.co/CwC43Rk/Screenshot-1643497670.png" alt="Screenshot-1643497670" border="0" width="300" hspace="10"/></a><a href="https://ibb.co/J7Zvny5"><img src="https://i.ibb.co/DCd9485/Screenshot-1643497147.png" alt="Screenshot-1643497147" border="0" width="300" hspace="10"/></a>
## Installation:
Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
Add the dependency:
```gradle
dependencies {
	        implementation 'com.github.danielhay1:Crashout:Tag'
}
```
### Notes:
- Add firebase to your project:
	- add google-services.json to your project
	- add this line to Project-level build.gradle in dependencies:
	- classpath 'com.google.gms:google-services:4.3.10'
- Allow access of lesssecureapps to your google account account (to enable crashout email sending on crash):
  - https://myaccount.google.com/lesssecureapps?pli=1&rapt=AEjHL4O1iqSRXtAkWL0taN2cCctzH0wHGIPWHd4o1ZO-L15QXeQLxyJo6G1d8xir3MN2_JFh_n2hfl0nUYveA_9HvzPCHh3CNQ

## Implementation:
### Option 1:
```
package com.example.crashout

import android.app.Application
import com.example.crushoutlibrary.CrashoutExceptionHandler
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
        config.imagePath = R.drawable.crashed
        config.destinationActivity = MainActivity::class.java
        config.detailsButonText = "Details"
        config.restartButtonText = "Restart"
        val crashout: Thread.UncaughtExceptionHandler = CrashoutExceptionHandler(this, config, MY_EMAIL_ADDRESS, MY_EMAIL_PASSWORD, MY_EMAIL_ADDRESS)
        Thread.setDefaultUncaughtExceptionHandler(crashout)
    }
}
```
### Option 2:
```
package com.example.crashout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.crushoutlibrary.CrashoutExceptionHandler
import com.example.crushoutlibrary.objects.CrashPageConfig

class MainActivity : AppCompatActivity() {
    // Implementation Example:
    val MY_EMAIL_ADDRESS: String = "username"
    val MY_EMAIL_PASSWORD: String = "password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Implementation Example:
        val config = CrashPageConfig()
        config.crashTitle = "Oh no, Application Crashed!"
        config.imagePath = R.drawable.crashed
        config.destinationActivity = MainActivity::class.java
        config.detailsButonText = "Details"
        config.restartButtonText = "Restart"
        val crashout: Thread.UncaughtExceptionHandler = CrashoutExceptionHandler(this, config, MY_EMAIL_ADDRESS, MY_EMAIL_PASSWORD, MY_EMAIL_ADDRESS)
        Thread.setDefaultUncaughtExceptionHandler(crashout)
    }
}
```
Note, you can use either (with or without email credentials):
```
val crashout: Thread.UncaughtExceptionHandler = CrashoutExceptionHandler(this, config, MY_EMAIL_ADDRESS, MY_EMAIL_PASSWORD, MY_EMAIL_ADDRESS)
Thread.setDefaultUncaughtExceptionHandler(crashout)
```
```
val crashout: Thread.UncaughtExceptionHandler = CrashoutExceptionHandler(this, config)
Thread.setDefaultUncaughtExceptionHandler(crashout)
```
## Troubleshoot:
If mail is not sending please make sure port 465 isn't block (mostly by antiVirus)

### License - Ⓒ free to use, the use of user responsibility, all right belong to daniel hay.
