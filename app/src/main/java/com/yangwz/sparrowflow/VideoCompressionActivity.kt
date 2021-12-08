package com.yangwz.sparrowflow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.blankj.utilcode.util.*
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.yangwz.common.base.BaseActivity
import com.yangwz.common.ffmpege.FFmpegeInstruction
import com.yangwz.common.utils.GlideEngine
import com.yangwz.common.utils.SparrowFileUtils
import com.yangwz.sparrowflow.databinding.ActivityVideoCompressionBinding
import com.yangwz.viewmodel.VideoCompressionViewModel
import io.microshow.rxffmpeg.RxFFmpegCommandList
import java.util.ArrayList
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import java.lang.ref.WeakReference


/**
 * @author : JamesYang
 * @date : 2021/11/30 14:04
 * @GitHub : https://github.com/JamesYang1624
 */
class VideoCompressionActivity :
    BaseActivity<ActivityVideoCompressionBinding>(ActivityVideoCompressionBinding::inflate) {
    private var hasPermission = false
    private val viewModel by viewModels<VideoCompressionViewModel>()
    private lateinit var myRxFFmpegSubscriber: MyRxFFmpegSubscriber

    private var textWidth = 0
    private var textHeight = 0
    private lateinit var outPut: String
    private lateinit var addWaterOutPut: String
    private lateinit var playVideo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("AutoDispose")
    override fun initView() {
        binding.viewModel = viewModel
        binding.progressBar.max = 100
        binding.progressBar.progress = 0
        binding.progress = "0%"
        binding.seekBar1.max = 100
        FileUtils.createOrExistsDir("/storage/emulated/0/atest")
        FileUtils.createOrExistsFile("/storage/emulated/0/atest/outPut.mp4")
        outPut = "/storage/emulated/0/atest/outPut.mp4"
        FileUtils.createOrExistsFile("/storage/emulated/0/atest/AddWaterOutPut.mp4")
        addWaterOutPut = "/storage/emulated/0/atest/AddWaterOutPut.mp4"
        XXPermissions.with(this)
            .permission(/*Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,*/
                Permission.MANAGE_EXTERNAL_STORAGE
            ).request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    hasPermission = true
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    hasPermission = false
                }
            })

//        binding.inputPath.setText("ffmpeg -y -i /storage/emulated/0/DCIM/Camera/VID_20211201_152309.mp4 -vf boxblur=5:1 -preset superfast /storage/emulated/0/1/result.mp4")
//        binding.inputPath.setText("ffmpeg -y -i /storage/emulated/0/DCIM/Camera/VID_20211201_152309.mp4 -vf boxblur=5:1 -preset superfast /storage/emulated/0/1/outPut.mp4")

        viewModel.selectPhotoListener { code ->
            when (code) {
                viewModel.CLICK_SELECT -> {
                    if (hasPermission) selectPhotot()
                }
                viewModel.CLICK_COMPRESS -> {
//                    val input = "/storage/emulated/0/DCIM/Camera/VID_20211201_152309.mp4"
//                    outPut = "/storage/emulated/0/1/result.mp4"
//                    val text = "ffmpeg -y -i $input -vf boxblur=5:1 -preset superfast $outPut"
//                    val commands: Array<String> = text.split(" ").toTypedArray()
                    if (hasPermission) {
                        val myRxFFmpegSubscriber = MyRxFFmpegSubscriber(this)
                        myRxFFmpegSubscriber.setProgress {
                            binding.progressBar.progress = it
                            binding.progress = "$it%"
                            binding.seekBar1.progress = it
                            if (it >= 1000) {
                                SPUtils.getInstance()
                                    .put(SpConstants.KEY_COMPRESS_OUT_VIDEO_PATH, outPut)
                                playVideo = outPut
                                binding.afterFileInfo = "压缩后   文件路径 ： $outPut 文件大小：${
                                    SparrowFileUtils.getSizeAutoUnit(
                                        outPut
                                    )
                                }"
                            }
                        }
                        binding.beforeFileInfo = "压缩前   文件路径 ： ${
                            SPUtils.getInstance()
                                .getString(SpConstants.KEY_SELECT_VIDEO_PATH)
                        } 文件大小：${
                            SparrowFileUtils.getSizeAutoUnit(
                                SPUtils.getInstance()
                                    .getString(SpConstants.KEY_SELECT_VIDEO_PATH)
                            )
                        }"

                        binding.afterFileInfo = ""
                        //开始执行FFmpeg命令
                        RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(
                                FFmpegeInstruction.compressVideo(
                                    SPUtils.getInstance()
                                        .getString(SpConstants.KEY_SELECT_VIDEO_PATH), outPut
                                )
                            )
                            .subscribe(myRxFFmpegSubscriber)
                    }
                }
                viewModel.CLICK_ADDWATERMARK -> {
//                    outPut = "/storage/emulated/0/atest/AddWaterOutPut.mp4"
//                    val text =
//                        "ffmpeg -y -i /storage/emulated/0/DCIM/Camera/VID_20211201_152309.mp4 -i /storage/emulated/0/atest/acfun.png -filter_complex [0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay=0:0 -preset superfast /storage/emulated/0/atest/AddWaterOutPut.mp4"
//                    val commands = text.split(" ").toTypedArray()

                    val myRxFFmpegSubscriber = MyRxFFmpegSubscriber(this)

                    myRxFFmpegSubscriber.setProgress {
                        if (it >= 1000) {
                            playVideo = addWaterOutPut
                        } else {
                            binding.progressBar.progress = it
                            binding.progress = "$it%"
                            binding.seekBar1.progress = it
                        }
                    }
                    if (!binding.editTextWidth.text.isNullOrEmpty()) {
                        textWidth = binding.editTextWidth.text.toString().toInt()
                    }
                    if (!binding.editTextHeight.text.isNullOrEmpty()) {
                        textHeight = binding.editTextHeight.text.toString().toInt()
                    }
                    if (textWidth <= 0) {
                        textWidth = ScreenUtils.getScreenWidth()
                    }
                    if (textHeight <= 0) {
                        textHeight = ScreenUtils.getScreenHeight()
                    }
                    val cmdAddWaterMark = FFmpegeInstruction.addWatermark(
                        SPUtils.getInstance().getString(SpConstants.KEY_COMPRESS_OUT_VIDEO_PATH),
                        "/storage/emulated/0/acfun.png", addWaterOutPut
                    )
                    LogUtils.iTag("$TAG cmdAddWaterMark = ", cmdAddWaterMark)
                    if (!cmdAddWaterMark.isNullOrEmpty()) {
                        //开始执行FFmpeg命令
                        RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(
                                cmdAddWaterMark
                            )
                            .subscribe(myRxFFmpegSubscriber)
//                    startAddWater()
                    }
                }
                viewModel.CLICK_ADDWATERPLAY -> {
                    if (addWaterOutPut.isEmpty()) {
                        ToastUtils.showShort("文件不存在")
                        return@selectPhotoListener
                    }
                    toVideo(addWaterOutPut)
                }
                viewModel.CLICK_COMPRESS_PLAY -> {
                    if (outPut.isEmpty()) {
                        ToastUtils.showShort("文件不存在")
                        return@selectPhotoListener
                    }
                    toVideo(outPut)
                }
            }
        }
    }

    private fun toVideo(path: String) {
        val intent = Intent(this, VideoPlayActivity::class.java)
        intent.putExtra("videoPath", path)
        intent.putExtra("width", textWidth)
        intent.putExtra("height", textHeight)
        startActivity(intent)
    }


    private fun selectPhotot() {
        EasyPhotos.createAlbum(
            this@VideoCompressionActivity,
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
                        SPUtils.getInstance()
                            .put(SpConstants.KEY_SELECT_VIDEO_PATH, videoFile.path)


                        if (videoFile != null && !outPut.isNullOrEmpty()) {
                            LogUtils.iTag(TAG, "操作文件:${videoFile.absolutePath}")
                            LogUtils.iTag(TAG, "文件创建成功:${outPut}")
                            // /storage/emulated/0/1/result.mp4
                            // /storage/emulated/0/test/outPut.mp4

                            val myRxFFmpegSubscriber =
                                MyRxFFmpegSubscriber(this@VideoCompressionActivity)
                            myRxFFmpegSubscriber.setProgress {

                                if (it >= 1000) {
                                    SPUtils.getInstance().put(
                                        SpConstants.KEY_COMPRESS_OUT_VIDEO_PATH,
                                        outPut
                                    )
                                    binding.afterFileInfo = "压缩后   文件路径 ： $outPut 文件大小：${
                                        SparrowFileUtils.getSizeAutoUnit(outPut)
                                    }"
                                    playVideo = outPut
                                } else {
                                    binding.progressBar.progress = it
                                    binding.progress = "$it%"
                                    binding.seekBar1.progress = it
                                }
                            }
                            binding.beforeFileInfo = "压缩前   文件路径 ： ${videoFile.path} 文件大小：${
                                SparrowFileUtils.getSizeAutoUnit(videoFile.path)
                            }"
                            binding.afterFileInfo = ""
                            RxFFmpegInvoke.getInstance()
                                .runCommandRxJava(
                                    FFmpegeInstruction.compressVideo(
                                        videoFile.path, outPut
                                    )
                                )
                                .subscribe(myRxFFmpegSubscriber)
//                            compress(
//                                videoFile.absolutePath,
//                                outFilePath.absolutePath
//                            )
                        } else
                            LogUtils.iTag(TAG, "文件创建失败")
                    }
                }

                override fun onCancel() {
                    LogUtils.i("EasyPhotos 取消选择 ")
                }
            })
    }

    override fun initData() {

    }

    //    override fun getViewModel(): AndroidViewModel  = viewModel
    override fun initViewModel() {
    }

    class MyRxFFmpegSubscriber(activity: VideoCompressionActivity) :
        RxFFmpegSubscriber() {
        private val mWeakReference: WeakReference<VideoCompressionActivity> =
            WeakReference(activity)
        private val TAG = "MyRxFFmpegSubscriber"
        override fun onFinish() {
            val mActivity: VideoCompressionActivity? = mWeakReference.get()
            if (mActivity != null) {
                LogUtils.iTag(TAG, "处理成功")
//                mActivity.cancelProgressDialog("处理成功")
                onProgressListener.invoke(1000)
            }
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            val mActivity: VideoCompressionActivity? = mWeakReference.get()
            if (mActivity != null) {
                //progressTime 可以在结合视频总时长去计算合适的进度值
//                mActivity.setProgressDialog(progress, progressTime)
                LogUtils.iTag(TAG, "处理中: $progress")
                onProgressListener.invoke(progress)
            }
        }

        override fun onCancel() {
            val mActivity: VideoCompressionActivity? = mWeakReference.get()
            if (mActivity != null) {
//                mActivity.cancelProgressDialog("已取消")
                LogUtils.iTag(TAG, "取消处理")
            }
        }

        override fun onError(message: String) {
            val mActivity: VideoCompressionActivity? = mWeakReference.get()
            if (mActivity != null) {
                LogUtils.iTag(TAG, "处理异常${message.toString()}")
//                mActivity.cancelProgressDialog("出错了 onError：$message")
            }
        }

        private lateinit var onProgressListener: (Int) -> Unit
        fun setProgress(progress: (Int) -> Unit) {
            onProgressListener = progress
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myRxFFmpegSubscriber.dispose()
    }


}