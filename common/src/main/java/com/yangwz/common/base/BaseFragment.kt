package com.yangwz.common.base

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.yangwz.common.utils.DensityUtil
import com.yangwz.common.utils.EasyStateView

/**
 * @author yangweizheng
 * @date 2021/05/22
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment() {
    protected val TAG: String = BaseFragment::class.java.simpleName
//    lateinit var viewModel: ViewModel

    //    abstract fun viewModelClass(): Class<ViewModel>//获取 ViewModel 类
//    abstract fun initViewModel(): Class<ViewModel>

    /**
     * 控件是否初始化完成
     */
    private var isViewCreated: Boolean = false

    /**
     * 是否加载过数据
     */
    private var isComplete: Boolean = false

    lateinit var binding: VB
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        binding = DataBindingUtil.inflate(inflater, getLayout(), null, false)
        isViewCreated = true
//        viewModel = ViewModelProvider(requireActivity()).get(viewModelClass())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        lazyLoad()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoad()
        }
    }

    /**
     * 懒加载
     */
    private fun lazyLoad() {
        if (userVisibleHint && isViewCreated && !isComplete) {
            //可见 或者 控件初始化完成 就 加载数据
            loadData()
            isComplete = true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        isComplete = false
    }

    @LayoutRes
    abstract fun getLayout(): Int

    protected abstract fun initView()
    protected abstract fun initViewModel()

    protected abstract fun loadData()

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
    protected open fun setBarHeight(view: View) {
        //获取状态栏高度
        val statusBarHeight = DensityUtil.getStatusBarHeight(requireContext())
//        AppLogger.json("setBarHeight statusBarHeight = ", statusBarHeight)
        if (statusBarHeight > 0) {
            val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
            layoutParams.setMargins(0, statusBarHeight, 0, 0)
            view.layoutParams = layoutParams
        }
    }
}