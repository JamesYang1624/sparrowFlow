package com.yangwz.common.ffmpege

import com.blankj.utilcode.util.ScreenUtils
import io.microshow.rxffmpeg.RxFFmpegCommandList


/**
 * @author : yangweizheng
 * @date : 2021/12/7 11:21
 */
object FFmpegeInstruction {
    /**
     * 视频添加水印指令
     */
    fun addWatermark(inPut: String, waterMark: String, addWaterOutPut: String): Array<String> {
        val cmdList = RxFFmpegCommandList()

        /**
         *
        ffmpeg -y -i /storage/emulated/0/1/input.mp4
        -i /storage/emulated/0/1/input.png
        -filter_complex [0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay=0:0
        -preset superfast /storage/emulated/0/1/result.mp4
         */

        //overlay：水印的位置，距离屏幕左侧的距离＊距离屏幕上侧的距离；mainW主视频宽度， mainH主视频高度，overlayW水印宽度，overlayH水印高度
        //
        //　　左上角overlay参数为 overlay=0:0
        //
        //　　右上角为 overlay= main_w-overlay_w:0
        //
        //　　右下角为 overlay= main_w-overlay_w:main_h-overlay_h
        //
        //　　左下角为 overlay=0: main_h-overlay_h
        //
        //     上面的0可以改为5，或10像素，以便多留出一些空白。

        cmdList.append("-y")
        cmdList.append("-i")
        cmdList.append(inPut)
        cmdList.append("-i")
        cmdList.append(waterMark)
        cmdList.append("-filter_complex")
        cmdList.append("[0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay=main_w-overlay_w:main_h-overlay_h")
        cmdList.append("-preset")
        cmdList.append("superfast")
        cmdList.append(addWaterOutPut)
        return cmdList.build()
    }

    /**
     *视频模糊滤镜指令
     *
     */
    fun getBoxUrl(inPut: String,outPut:String): Array<String> {
        val cmdList = RxFFmpegCommandList()
        cmdList.append("-y")
        cmdList.append("-i")
        cmdList.append(inPut)
        cmdList.append("-vf")
        cmdList.append("boxblur=5:1")
        cmdList.append("-preset")
        cmdList.append("superfast")
        cmdList.append(outPut)
        return cmdList.build()
    }

    /**
     * 封装压缩指令
     */
    fun compressVideo(inPut: String,outPut:String): Array<String> {
        val cmdList = RxFFmpegCommandList()
        cmdList.append("-y")
        cmdList.append("-i")
        cmdList.append(inPut)
        cmdList.append("-b")
        cmdList.append("2097k")
        cmdList.append("-r")
        cmdList.append("30")
        cmdList.append("-vcodec")
        cmdList.append("libx264")
        cmdList.append("-preset")
        cmdList.append("superfast")
        cmdList.append(outPut)
        return cmdList.build()
    }


}