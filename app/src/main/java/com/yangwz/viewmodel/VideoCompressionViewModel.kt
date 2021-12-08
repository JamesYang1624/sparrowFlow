package com.yangwz.viewmodel

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.yangwz.common.base.viewmodel.BaseViewModel
import com.yangwz.sparrowflow.R


/**
 * @author : yangweizheng
 * @date : 2021/12/1 10:19
 */
class VideoCompressionViewModel : BaseViewModel() {
    val CLICK_SELECT = 0
    val CLICK_COMPRESS = 1
    val CLICK_ADDWATERMARK = 2
    val CLICK_ADDWATERPLAY = 3
    val CLICK_COMPRESS_PLAY = 4


    fun onClick(view: View) {
//        ToastUtils.showShort(view.id.toString())
        when (view.id) {
            R.id.button_select_video -> {
                ToastUtils.showShort("选择视频")
                selectPhotoClick.invoke(CLICK_SELECT)
            }
            R.id.button_compress -> {
                ToastUtils.showShort("开始压缩")
                selectPhotoClick.invoke(CLICK_COMPRESS)
            }
            R.id.addWaterMark -> {
                selectPhotoClick.invoke(CLICK_ADDWATERMARK)
            }
            R.id.addWaterPlay -> {
                selectPhotoClick.invoke(CLICK_ADDWATERPLAY)
            }
            R.id.compress_play -> {
                selectPhotoClick.invoke(CLICK_COMPRESS_PLAY)
            }
        }
    }

    private lateinit var selectPhotoClick: (Int) -> Unit
    fun selectPhotoListener(listener: (Int) -> Unit) {
        selectPhotoClick = listener
    }
}