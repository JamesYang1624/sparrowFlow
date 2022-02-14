package com.yangwz.common.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileUtils
import java.io.File
import java.io.IOException
import android.os.Build


/**
 * @author : yangweizheng
 * @date : 2021/12/9 16:57
 */
object CardFileUtils {
    fun getRootPath(context: Context): String {
        var dir: File? = null
        val state = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        dir = if (state) {
            if (Build.VERSION.SDK_INT >= 29) {
                //Android10之后
                context.getExternalFilesDir(null)
            } else {
                Environment.getExternalStorageDirectory()
            }
        } else {
            Environment.getRootDirectory()
        }
        return dir.toString()
    }

    fun createDirAtRoot(context: Context, name: String): String {
        return getRootPath(context).plus("/$name")
    }


    fun getFilesPath(context: Context): String {
        return context.filesDir.absolutePath
    }

    fun createFileByPath(name: String): Boolean {
        return createOrExistsFile(getFileByPath(name))
    }

    fun createDirByPath(name: String): Boolean {
        return createOrExistsDir(getFileByPath(name))
    }

    fun getCacheDir(context: Context): String {
        return context.cacheDir.absolutePath
    }

    fun getExternalPicDir(context: Context): String {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
    }

    fun getExternalMusicDir(context: Context): String {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath
    }

    fun getExternalMoviesDir(context: Context): String {
        return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.absolutePath
    }

    fun getExternalDownloadDir(context: Context): String {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath
    }

    fun getExternalDCIMDir(context: Context): String {
        return context.getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath
    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (!FileUtils.createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun getFileByPath(filePath: String?): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }
}