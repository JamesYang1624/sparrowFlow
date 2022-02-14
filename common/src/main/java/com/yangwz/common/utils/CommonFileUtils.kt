package com.yangwz.common.utils

import com.blankj.utilcode.util.FileUtils
import java.text.DecimalFormat
import kotlin.math.sign


/**
 * @author : yangweizheng
 * @date : 2021/12/7 10:02
 */
object CommonFileUtils {
    /**
     *获取文件大小单位为B的double值
     */
    private val SIZETYPE_B = 1

    /**
     * 获取文件大小单位为KB的double值
     */
    private val SIZETYPE_KB = 2

    /**
     * 获取文件大小单位为MB的double值
     */
    private val SIZETYPE_MB = 3

    /**
     * 获取文件大小单位为GB的double值
     */
    private val SIZETYPE_GB = 4

    fun getSizeKB(path: String) {
        getFileSize(FileUtils.getFileLength(path), SIZETYPE_KB)
    }

    fun getSizeB(path: String) {
        getFileSize(FileUtils.getFileLength(path), SIZETYPE_B)
    }

    fun getSizeMB(path: String) {
        getFileSize(FileUtils.getFileLength(path), SIZETYPE_MB)
    }

    fun getSizeGB(path: String) {
        getFileSize(FileUtils.getFileLength(path), SIZETYPE_GB)
    }

    fun getSizeAutoUnit(path: String): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        val fileS = FileUtils.getFileLength(path)
        when (fileS) {
            0L -> {
                fileSizeString = wrongSize
            }
            in 1L..1023L -> {
                fileSizeString = getFileSize(fileS, SIZETYPE_B)
            }
            in 1024L..1048575 -> {
                fileSizeString = getFileSize(fileS, SIZETYPE_KB)
            }
            in 1048576..1073741823 -> {
                fileSizeString = getFileSize(fileS, SIZETYPE_MB)
            }
            in 1073741824..Long.MAX_VALUE -> {
                fileSizeString = getFileSize(fileS, SIZETYPE_GB)
            }
        }
        return fileSizeString
    }

    private fun getFileSize(fileS: Long, unit: Int): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
//        val fileS = FileUtils.getFileLength(path)
        if (fileS == 0L) {
            return wrongSize
        }
        when (unit) {
            SIZETYPE_B -> {
                fileSizeString = df.format(fileS).plus("B")
            }
            SIZETYPE_KB -> {
                fileSizeString = df.format(fileS / 1024).plus("KB")
            }
            SIZETYPE_MB -> {
                fileSizeString = df.format(fileS / 1048576).plus("MB")
            }
            SIZETYPE_GB -> {
                fileSizeString = df.format(fileS / 1073741824).plus("GB")
            }
        }
        return fileSizeString
    }

}