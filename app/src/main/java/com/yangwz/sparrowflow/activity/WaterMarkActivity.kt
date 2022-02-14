package com.yangwz.sparrowflow.activity

import androidx.activity.viewModels
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.UriUtils
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.yangwz.common.base.BaseActivity
import com.yangwz.common.utils.GlideEngine
import com.yangwz.sparrowflow.app.SpConstants
import com.yangwz.sparrowflow.databinding.ActivityWaterMarkBinding
import com.yangwz.sparrowflow.utils.WatermarkSettings
import com.yangwz.sparrowflow.viewmodel.WaterMarkViewModel
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import java.lang.ref.WeakReference
import java.util.ArrayList


/**
 * @author : yangweizheng
 * @date : 2022/2/10 10:14
 */

class WaterMarkActivity :
    BaseActivity<ActivityWaterMarkBinding>(ActivityWaterMarkBinding::inflate) {
    private val viewModel by viewModels<WaterMarkViewModel>()
    val outFile = "/storage/emulated/0/waterResult.mp4"
    val waterBg = "/storage/emulated/0/acfun.png"
    val nameMarkGg = "/storage/emulated/0/nameMarkGg.png"
    override fun initViewModel() {

    }

    override fun initView() {
        ///storage/emulated/0/1/result.mp4
        val createOrExistsFile = FileUtils.createOrExistsFile(outFile)
        FileUtils.createOrExistsFile(nameMarkGg)
        WatermarkSettings.getmInstance(this)
        WatermarkSettings.saveWaterMarkFile(nameMarkGg, "你的名字")
        binding.progressBar2.max = 100
        binding.progressBar2.progress = 0
        binding.buttonSelectVideo.setOnClickListener {
            selectPhoto()
        }
    }

    override fun initData() {
    }

    private fun selectPhoto() {
        EasyPhotos.createAlbum(
            this,
            false,
            true,
            GlideEngine.getInstance()
        )
            .onlyVideo()
            .setCount(1)
            .setFileProviderAuthority("com.yangwz.sparrowflow.fileprovider")
            .start(object : SelectCallback() {
                override fun onResult(
                    photos: ArrayList<Photo>,
                    isOriginal: Boolean
                ) {
                    LogUtils.i("EasyPhotos 选择完成 ")
                    if (!photos.isNullOrEmpty()) {
                        val videoFile = UriUtils.uri2File(photos[0].uri)
                        val videoPath = videoFile.absolutePath
                        SPUtils.getInstance()
                            .put(SpConstants.KEY_SELECT_VIDEO_PATH, videoFile.path)
                        val myRxFFmpegSubscriber =
                            MyRxFFmpegSubscriber(this@WaterMarkActivity)
                        myRxFFmpegSubscriber.setProgress { progress ->
                            when (progress) {
                                -1 -> {
                                    binding.textViewState.text = "出错"
                                    binding.progressBar2.progress = 0
                                }
                                1000 -> {
                                    binding.textViewState.text = "完成"

                                }
                                else -> {
                                    binding.textViewState.text = "执行中"
                                    binding.progressBar2.progress = progress
                                }
                            }

                        }
                        val txt = "www.baidu.com"
//                        val text =
//                            "ffmpeg -y -i $videoPath -vf boxblur=25:5 -preset superfast /storage/emulated/0/waterResult.mp4"
                        val text =
                            "ffmpeg -y -i $videoPath -i $nameMarkGg -filter_complex [0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay=0:0 -preset superfast $outFile"
                        val text1 =
                            "ffmpeg -y -i $videoPath -vf \"drawtext=text=$txt:x=10:y=10:fontsize=24:fontcolor=white:shadowy=2\" $outFile"
                        val text2 =
                            "ffmpeg -i $videoPath -vf \"drawtext=fontfile=Arial.ttf:text=$txt:y=h-line_h-20:x=(w-text_w)/2:fontsize=34:fontcolor=yellow:shadowy=2\"  $outFile"
                        val commands: Array<String> = text.split(" ").toTypedArray()

                        RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(commands)
                            .subscribe(myRxFFmpegSubscriber)

                    }
                }

                override fun onCancel() {
                    LogUtils.i("EasyPhotos 取消选择 ")
                }
            })
    }

    class MyRxFFmpegSubscriber(activity: WaterMarkActivity) :
        RxFFmpegSubscriber() {
        private val mWeakReference: WeakReference<WaterMarkActivity> =
            WeakReference(activity)
        private val TAG = "MyRxFFmpegSubscriber"
        override fun onFinish() {
            val mActivity: WaterMarkActivity? = mWeakReference.get()
            if (mActivity != null) {
                LogUtils.iTag(TAG, "处理成功")
//                mActivity.cancelProgressDialog("处理成功")
                onProgressListener.invoke(1000)
            }
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            val mActivity: WaterMarkActivity? = mWeakReference.get()
            if (mActivity != null) {
                //progressTime 可以在结合视频总时长去计算合适的进度值
//                mActivity.setProgressDialog(progress, progressTime)
                LogUtils.iTag(TAG, "处理中: $progress")
                onProgressListener.invoke(progress)
            }
        }

        override fun onCancel() {
            val mActivity: WaterMarkActivity? = mWeakReference.get()
            if (mActivity != null) {
                LogUtils.iTag(TAG, "取消处理")
                onProgressListener.invoke(-1)
            }
        }

        override fun onError(message: String) {
            val mActivity: WaterMarkActivity? = mWeakReference.get()
            if (mActivity != null) {
                LogUtils.iTag(TAG, "处理异常${message.toString()}")
                onProgressListener.invoke(-1)
            }
        }

        private lateinit var onProgressListener: (Int) -> Unit
        fun setProgress(progress: (Int) -> Unit) {
            onProgressListener = progress
        }
    }

}