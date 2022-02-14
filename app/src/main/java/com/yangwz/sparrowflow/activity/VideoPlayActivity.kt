package com.yangwz.sparrowflow.activity

import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.yangwz.common.base.BaseActivity
import com.yangwz.sparrowflow.databinding.ActivityVideoPlayBinding
import io.microshow.rxffmpeg.player.MeasureHelper
import io.microshow.rxffmpeg.player.RxFFmpegPlayerControllerImpl
import io.microshow.rxffmpeg.player.RxFFmpegPlayerView


/**
 * @author : yangweizheng
 * @date : 2021/12/6 14:36
 */
class VideoPlayActivity :
    BaseActivity<ActivityVideoPlayBinding>(ActivityVideoPlayBinding::inflate) {
    private var mPlayerView: RxFFmpegPlayerView? = null
    override fun initViewModel() {
    }

    override fun initView() {
        mPlayerView = binding.rxPlayerView
        val appScreenWidth = ScreenUtils.getAppScreenWidth()
        val appScreenHeight = ScreenUtils.getAppScreenHeight()
        LogUtils.iTag(TAG, "appScreenWidth = $appScreenWidth appScreenHeight = $appScreenHeight")
        val videoPath = intent.getStringExtra("videoPath")
        val width = intent.getIntExtra("width", 0)
        val height = intent.getIntExtra("height", 0)
        binding.screenData =
            "屏幕参数 ： 宽：$appScreenWidth   高：$appScreenHeight \n 水印位置 ： 宽 ：$width  高： $height"

        //设置播放器内核
        mPlayerView?.switchPlayerCore(RxFFmpegPlayerView.PlayerCoreType.PCT_RXFFMPEG_PLAYER)
        //设置控制层容器 和 视频尺寸适配模式
        mPlayerView?.setController(
            RxFFmpegPlayerControllerImpl(this),
            MeasureHelper.FitModel.FM_DEFAULT
        )
        if (videoPath != null) {
            mPlayerView?.play(videoPath, false)
        }
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        //恢复播放

        mPlayerView?.resume()
    }

    override fun onPause() {
        super.onPause()
        //暂停视频
        mPlayerView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //销毁播放器
        mPlayerView?.release()
    }
}