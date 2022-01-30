package com.example.crushoutlibrary.objects

import com.example.crushoutlibrary.utils.DeviceMonitor
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.io.Serializable

data class CrashoutReport(
    var crashTime: String = "",
    var parsedThrowable: ParsedThrowable = ParsedThrowable(),
    var monitorData: MonitorData = MonitorData()
): Serializable {

    constructor(throwable: Throwable, crashTime: String) : this() {
        this.crashTime = crashTime
        this.parsedThrowable = ParsedThrowable(throwable)
    }

    constructor(throwable: Throwable, crashTime: String, deviceMonitor: DeviceMonitor) : this() {
        this.crashTime = crashTime
        this.parsedThrowable = ParsedThrowable(throwable)
        this.monitorData = MonitorData(deviceMonitor)
    }

    override fun toString(): String {
        return "${javaClass.simpleName}: (crashTime: $crashTime\nparsedThrowable: ($parsedThrowable)\nmonitorData: ($monitorData))"
    }

    class ParsedThrowable(
        var stackTrace: String = "",
        var cause: String = "",
        var description: String = "",
        var exceptionClass: String = "",
    ): Serializable {

        constructor(throwable: Throwable) : this() {
            this.stackTrace = getStringStackTrace(throwable)
            this.cause = throwable.cause.toString()
            this.description = throwable.message.toString()
            this.exceptionClass = throwable.javaClass.toString()
            //this.shortStack = getCriticalThrowInfo()    //Consider to remove
        }

        private fun getStringStackTrace(throwable: Throwable): String {
            val stackTrace: Writer = StringWriter()
            val printWriter: PrintWriter = PrintWriter(stackTrace)
            throwable.printStackTrace(printWriter)
            val stringStackTrace = stackTrace.toString()
            printWriter.close()
            return stringStackTrace
        }

        private fun getCriticalThrowInfo(): String {
            var value: String = ""
            val splitedData: List<String> = stackTrace.split(cause.toString())
            if(splitedData.size > 0) {
                value = splitedData[splitedData.size-1]
            }
            return value
        }

        override fun equals(other: Any?): Boolean {
            return stackTrace.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }


        override fun toString(): String {
            return  "Cause= $cause\n" +
                    "Description= $description\n" +
                    "StackTrace= $stackTrace\n" +
                    "ExceptionClass= $exceptionClass"
        }
    }

    data class MonitorData(var batteryLevel: Float = -1f, var isCharging: Boolean = false): Serializable {

        constructor(deviceMonitor: DeviceMonitor) : this() {
            this.batteryLevel = deviceMonitor.getBatteryLevel()
            this.isCharging = deviceMonitor.getBatteryStatus()
        }
    }

}