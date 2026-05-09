package com.dice3d.logging

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashLogManager(private val context: Context) {

    private val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    fun install() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleCrash(thread, throwable)
        }
        AppLogger.i(TAG, "CrashLogManager installed")
    }

    private fun handleCrash(thread: Thread, throwable: Throwable) {
        AppLogger.e(TAG, "Uncaught exception on thread ${thread.name}")
        writeCrashLog(thread, throwable)
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun writeCrashLog(thread: Thread, throwable: Throwable) {
        try {
            val logDir = getLogDir()
            val fileName = "${fileDateFormat.format(Date())}.log"
            val logFile = File(logDir, fileName)
            logFile.parentFile?.mkdirs()

            logFile.bufferedWriter().use { writer ->
                writer.write("========== CRASH LOG ==========\n\n")
                writer.write("App Version: ${getAppVersion()}\n")
                writer.write("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                writer.write("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                writer.write("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())}\n")
                writer.write("Thread: ${thread.name}\n\n")

                writer.write("---------- Exception ----------\n")
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                writer.write(sw.toString())
                writer.write("\n\n")

                writer.write("---------- App Log Buffer ----------\n")
                writer.write(AppLogger.getInstance().exportToString())
                writer.write("\n\n")

                writer.write("---------- Recent Logcat ----------\n")
                writer.write(getRecentLogcat())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write crash log", e)
        }
    }

    fun exportLog(): File? {
        return try {
            val logDir = getLogDir()
            val fileName = "${fileDateFormat.format(Date())}.log"
            val logFile = File(logDir, fileName)
            logFile.parentFile?.mkdirs()

            logFile.bufferedWriter().use { writer ->
                writer.write("========== APP LOG EXPORT ==========\n\n")
                writer.write("App Version: ${getAppVersion()}\n")
                writer.write("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                writer.write("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                writer.write("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())}\n\n")

                writer.write("---------- App Log Buffer ----------\n")
                writer.write(AppLogger.getInstance().exportToString())
                writer.write("\n\n")

                writer.write("---------- Recent Logcat ----------\n")
                writer.write(getRecentLogcat())
            }

            AppLogger.i(TAG, "Log exported to ${logFile.absolutePath}")
            logFile
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to export log: ${e.message}")
            null
        }
    }

    fun getLogDir(): File {
        val externalDir = context.getExternalFilesDir(null)
        return File(externalDir, "logs")
    }

    fun getLogFiles(): List<File> {
        val dir = getLogDir()
        if (!dir.exists()) return emptyList()
        return dir.listFiles()
            ?.filter { it.extension == "log" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo: PackageInfo = context.packageManager
                .getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    private fun getRecentLogcat(): String {
        return try {
            val process = Runtime.getRuntime().exec(
                arrayOf("logcat", "-d", "-t", "200", "-v", "time")
            )
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }
            reader.close()
            output.toString()
        } catch (e: Exception) {
            "Failed to read logcat: ${e.message}"
        }
    }

    companion object {
        private const val TAG = "CrashLogManager"
    }
}
