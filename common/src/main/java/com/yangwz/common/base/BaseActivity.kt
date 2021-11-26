package com.yangwz.common.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import com.xunai.common.repository.ErrorResult
import com.yangwz.common.base.viewmodel.BaseViewModel
import com.yangwz.common.utils.AntiShake
import com.yangwz.common.utils.DensityUtil
import com.yangwz.common.utils.EasyStateView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * @author ywz
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    protected val TAG: String = BaseActivity::class.java.simpleName
    lateinit var binding: VB
    lateinit var vm: AndroidViewModel
    abstract fun getTitleString(): String //设置标题
    protected val mAntiShake: AntiShake = AntiShake()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        vm = getViewModel()
//        ARouter.getInstance().inject(this)
        binding = DataBindingUtil.setContentView<VB>(this, getLayout())
        job = Job()
        //设置状态栏透明
        //设置状态栏透明
        val statusBarHeight = ImmersionBar.getStatusBarHeight(this)

        initView()
        initData()
        try {
            //loading
            (vm as BaseViewModel).isShowLoading.observe(this, Observer {
//                if (it) showLoading() else dismissLoading()
            })
            //错误信息
            (vm as BaseViewModel).errorData.observe(this, Observer {
//                if (it.show) ToastUtil.showToast(it.errMsg)
                errorResult(it)
            })
//            (vm as BaseViewModel).emptyData.observe(this, Observer {
//                if (it) {
//                    showEmptyView(findViewById<EasyStateView>(R.id.esyStateView), "没有数据")
//                } else {
//
//                }
//            })
        } catch (exception: ClassCastException) {
//            NetLogUtil.i(" CommonBaseActivity viewModel 异常" + exception.message)
        }
    }

    protected open fun setBarHeight(view: View) {
        //获取状态栏高度
        val statusBarHeight1 = ImmersionBar.getStatusBarHeight(this)

        val statusBarHeight = DensityUtil.getStatusBarHeight(this)
        if (statusBarHeight > 0) {
            val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
            layoutParams.setMargins(0, statusBarHeight, 0, 0)
            view.layoutParams = layoutParams
        }else{
            if (statusBarHeight1 >0){
                val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
                layoutParams.setMargins(0, statusBarHeight1, 0, 0)
                view.layoutParams = layoutParams
            }
        }
    }

    //跳转类
    inline fun <reified T : Activity> Context.startActivity(action: Intent.() -> Unit) {
        var intent = Intent(this, T::class.java)
        action(intent)
        this.startActivity(intent)
    }


    @LayoutRes
    protected abstract fun getLayout(): Int

    protected abstract fun getViewModel(): AndroidViewModel
    protected abstract fun initViewModel()
    protected abstract fun initView()
    protected abstract fun initData()
    //44x33x73

//    open fun showProgressDialog(isCancel: Boolean) {
//        if (mProgressDialog == null) {
//            mProgressDialog = MyProgressDialog.getInstance(this).init()
//        }
//        mProgressDialog?.show(isCancel)
//    }
//
//    open fun showProgressDialog(isCancel: Boolean, message: String?) {
//        if (mProgressDialog == null) {
//            mProgressDialog = MyProgressDialog.getInstance(this).init()
//        }
//        mProgressDialog?.show(isCancel, message)
//    }
//
//    open fun cancelProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
//            mProgressDialog!!.dismiss()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭页面后，结束所有协程任务
        job.cancel()
    }

    /**
     * 接口请求错误回调
     */
    open fun errorResult(errorResult: ErrorResult) {}
//    fun showLoading() {
//        if (mProgressDialog == null) {
//            mProgressDialog = MyProgressDialog.getInstance(this).init()
//        }
//        mProgressDialog?.show()
//    }
//
//    fun dismissLoading() {
//        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
//            mProgressDialog!!.dismiss()
//        }
//    }

    /**
     * 当前显示的状态不为空则显示空数据缺省页
     */
//    protected open fun showEmptyView(
//        easyStateView: EasyStateView,
//        string: String?
//    ) {
//        if (easyStateView.currentState != EasyStateView.VIEW_EMPTY
//        ) {
//            val tvMsg: TextView = easyStateView.getStateView(EasyStateView.VIEW_EMPTY)
//                .findViewById(R.id.tv_no_data_tips)
//            tvMsg.text = string
//            easyStateView.showViewState(EasyStateView.VIEW_EMPTY)
//        }
//    }


    /**
     * 当前显示的状态不为空则显示空数据缺省页
     */
//    protected open fun showEmptyView(
//        adapter: RecyclerView.Adapter<*>?,
//        easyStateView: EasyStateView,
//        string: String?
//    ) {
//        if (null != adapter && adapter.itemCount == 0 && easyStateView.currentState != EasyStateView.VIEW_EMPTY
//        ) {
//            val tvMsg: TextView = easyStateView.getStateView(EasyStateView.VIEW_EMPTY)
//                .findViewById(R.id.tv_no_data_tips)
//            tvMsg.text = string
//            easyStateView.showViewState(EasyStateView.VIEW_EMPTY)
//        }
//    }

    /**
     * 当前显示的状态不为空则显示空数据缺省页
     */
    protected open fun showContentView(easyStateView: EasyStateView?) {
        if (null != easyStateView &&
            easyStateView.currentState != EasyStateView.VIEW_CONTENT
        ) {
            easyStateView.showViewState(EasyStateView.VIEW_CONTENT)
        }
    }

}