package com.example.data.system

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.RandomAccessFile

data class StorageState(
    val totalSpaceBytes: Long = 0,
    val freeSpaceBytes: Long = 0,
    val usedSpaceBytes: Long = 0
)

data class RamState(
    val totalRamBytes: Long = 0,
    val availRamBytes: Long = 0,
    val usedRamBytes: Long = 0,
    val isLowMemory: Boolean = false
)

data class CpuState(
    val cores: Int = 0,
    val architecture: String = "",
    val model: String = "",
    val usagePercentage: Float = 0f
)

class DeviceInfoProvider(private val context: Context) {
    private val _storageState = MutableStateFlow(StorageState())
    val storageState: StateFlow<StorageState> = _storageState.asStateFlow()
    
    private val _ramState = MutableStateFlow(RamState())
    val ramState: StateFlow<RamState> = _ramState.asStateFlow()
    
    private val _cpuState = MutableStateFlow(CpuState())
    val cpuState: StateFlow<CpuState> = _cpuState.asStateFlow()

    fun updateStorageInfo() {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        
        val totalSpace = totalBlocks * blockSize
        val freeSpace = availableBlocks * blockSize
        val usedSpace = totalSpace - freeSpace
        
        _storageState.value = StorageState(
            totalSpaceBytes = totalSpace,
            freeSpaceBytes = freeSpace,
            usedSpaceBytes = usedSpace
        )
    }
    
    fun updateRamInfo() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val total = memoryInfo.totalMem
        val avail = memoryInfo.availMem
        val used = total - avail
        
        _ramState.value = RamState(
            totalRamBytes = total,
            availRamBytes = avail,
            usedRamBytes = used,
            isLowMemory = memoryInfo.lowMemory
        )
    }
    
    fun updateCpuInfo() {
        val cores = Runtime.getRuntime().availableProcessors()
        val arch = System.getProperty("os.arch") ?: "Unknown"
        
        var model = "Unknown"
        try {
            val reader = RandomAccessFile("/proc/cpuinfo", "r")
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.contains("Hardware", ignoreCase = true) || line.contains("model name", ignoreCase = true)) {
                    val parts = line.split(":")
                    if (parts.size > 1) {
                        model = parts[1].trim()
                        break
                    }
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Approximate overall CPU usage is complex in Android 8+. We'll put a placeholder or read /proc/stat
        val usage = getCpuUsage()
        
        _cpuState.value = CpuState(
            cores = cores,
            architecture = arch,
            model = model,
            usagePercentage = usage
        )
    }
    
    private var lastTotal = 0L
    private var lastIdle = 0L
    
    private fun getCpuUsage(): Float {
        try {
            val reader = RandomAccessFile("/proc/stat", "r")
            val load = reader.readLine()
            reader.close()
            
            val toks = load.split(" +".toRegex()).toTypedArray()
            val idle1 = toks[4].toLong()
            val cpu1 = toks[1].toLong() + toks[2].toLong() + toks[3].toLong() + toks[5].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
            
            val total = idle1 + cpu1
            val idle = idle1
            
            val diffTotal = total - lastTotal
            val diffIdle = idle - lastIdle
            
            lastTotal = total
            lastIdle = idle
            
            return if (diffTotal > 0) ((diffTotal - diffIdle).toFloat() / diffTotal.toFloat()) * 100f else 0f
        } catch (e: Exception) {
            return 0f
        }
    }
    
    fun getOsInfo(): Map<String, String> {
        return mapOf(
            "Android Version" to Build.VERSION.RELEASE,
            "API Level" to Build.VERSION.SDK_INT.toString(),
            "Device" to Build.DEVICE,
            "Model" to Build.MODEL,
            "Manufacturer" to Build.MANUFACTURER,
            "Board" to Build.BOARD,
            "Hardware" to Build.HARDWARE,
            "Bootloader" to Build.BOOTLOADER
        )
    }
}
