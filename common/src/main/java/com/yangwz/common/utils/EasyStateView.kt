package com.yangwz.common.utils

import android.animation.Animator
import android.widget.FrameLayout
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Parcelable
import android.os.Parcel
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.yangwz.common.R
import java.lang.RuntimeException

/**
 * @author 饶忠俊
 * 2019/10/16 0016
 * email：markraostudio@gmail.com
 * description：{ 状态切换控件 }
 */
class EasyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    // 用来存放 View
    private var mViews: SparseArray<View>? = null

    // 是否使用过渡动画
    private var mUseAnim = false

    // 是否处于动画中
    private var isAniming = false

    // 当前显示的 ViewTag
    var currentState = 0
        private set
    private var mContext: Context? = null
    private var mListener: StateViewListener? = null

    // content View 是否被添加到队列
    private var isAddContent = false

    interface StateViewListener {
        fun onStateChanged(state: Int)
    }

    fun setStateChangedListener(listener: StateViewListener?) {
        mListener = listener
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        mViews = SparseArray()
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyStateView)
        currentState = typedArray.getInt(R.styleable.EasyStateView_esv_viewState, VIEW_CONTENT)
        val emptyResId = typedArray.getResourceId(R.styleable.EasyStateView_esv_emptyView, VIEW_TAG)
        if (emptyResId != VIEW_TAG) {
            val view = LayoutInflater.from(getContext()).inflate(emptyResId, this, false)
            addViewToHash(view, VIEW_EMPTY)
            addViewInLayout(view, -1, view.layoutParams)
        }
        val errorDataResId =
            typedArray.getResourceId(R.styleable.EasyStateView_esv_errorDataView, VIEW_TAG)
        if (errorDataResId != VIEW_TAG) {
            val view = LayoutInflater.from(getContext()).inflate(errorDataResId, this, false)
            addViewToHash(view, VIEW_ERROR_DATA)
            addViewInLayout(view, -1, view.layoutParams)
        }
        val errorNetResId =
            typedArray.getResourceId(R.styleable.EasyStateView_esv_errorNetView, VIEW_TAG)
        if (errorNetResId != VIEW_TAG) {
            val view = LayoutInflater.from(getContext()).inflate(errorNetResId, this, false)
            addViewToHash(view, VIEW_ERROR_NET)
            addViewInLayout(view, -1, view.layoutParams)
        }
        val loadingResId =
            typedArray.getResourceId(R.styleable.EasyStateView_esv_loadingView, VIEW_TAG)
        if (loadingResId != VIEW_TAG) {
            val view = LayoutInflater.from(getContext()).inflate(loadingResId, this, false)
            addViewToHash(view, VIEW_LOADING)
            addViewInLayout(view, -1, view.layoutParams)
        }
        mUseAnim = typedArray.getBoolean(R.styleable.EasyStateView_esv_use_anim, true)
        typedArray.recycle()
    }

    override fun addView(child: View) {
        addContentV(child)
        super.addView(child)
    }

    private fun isContentView(child: View?): Boolean {
        return if (!isAddContent && null != child && null == child.tag) {
            true
        } else false
    }

    private fun addContentV(child: View) {
        if (isContentView(child)) {
            addViewToHash(child, VIEW_CONTENT)
            isAddContent = true
        }
    }

    private fun addViewToHash(child: View, viewTag: Int) {
        child.tag = viewTag
        if (viewTag != currentState) {
            child.visibility = GONE
        }
        mViews!!.put(viewTag, child)
    }

    override fun addView(child: View, index: Int) {
        addContentV(child)
        super.addView(child, index)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        addContentV(child)
        super.addView(child, index, params)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addContentV(child)
        super.addView(child, params)
    }

    override fun addView(child: View, width: Int, height: Int) {
        addContentV(child)
        super.addView(child, width, height)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams): Boolean {
        addContentV(child)
        return super.addViewInLayout(child, index, params)
    }

    override fun addViewInLayout(
        child: View,
        index: Int,
        params: ViewGroup.LayoutParams,
        preventRequestLayout: Boolean
    ): Boolean {
        addContentV(child)
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }



    /**
     * 切换默认状态的 View
     *
     * @param state
     */
    fun showViewState(state: Int) {
        if (!checkState(state)) {
            showViewAnim(state, VIEW_TAG)
        }
    }

    /**
     * 切换 view 时用 loading view 过渡
     *
     * @param state
     */
    fun afterLoadingState(state: Int) {
        if (!checkState(state)) {
            if (currentState == VIEW_LOADING) {
                showViewAnim(state, VIEW_TAG)
            } else {
                showViewAnim(VIEW_LOADING, state)
            }
        }
    }

    /**
     * 检查状态是否合法
     * true 表示不合法，不往下执行
     * false 表示该状态和当前状态不同，并合法数值状态
     *
     * @param state
     * @return
     */
    private fun checkState(state: Int): Boolean {
        if (state <= VIEW_TAG) {
            throw RuntimeException("ViewState 不在目标范围")
        }
        if (state == currentState) {
            return true
        } else if (isAniming) {
            return true
        }
        return false
    }

    fun setUseAnim(useAnim: Boolean) {
        mUseAnim = useAnim
    }

    private fun showViewAnim(showState: Int, afterState: Int) {
        if (!isAniming) {
            isAniming = true
        }
        val showView = getStateView(showState)
        if (null == showView) {
            isAniming = false
            return
        }
        val currentView = getStateView(currentState)
        if (mUseAnim) {
            showAlpha(showState, afterState, showView, currentView)
        } else {
            currentView.visibility = GONE
            if (showView.alpha == 0f) {
                showView.alpha = 1f
            }
            showView.visibility = VISIBLE
            currentState = showState
            if (null != mListener) {
                mListener!!.onStateChanged(showState)
            }
            isAniming = false
        }
    }

    /**
     * 参数依次为：显示的状态、显示之后的状态码、要显示的 View、当前的 View
     *
     * @param showState
     * @param afterState
     * @param showView
     * @param currentView
     */
    private fun showAlpha(
        showState: Int, afterState: Int, showView: View,
        currentView: View
    ) {
        val currentAnim = ObjectAnimator.ofFloat(currentView, "alpha", 1f, 0f)
        currentAnim.duration = 250L
        val showAnim = ObjectAnimator.ofFloat(showView, "alpha", 0f, 1f)
        showAnim.duration = 250L
        showAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (null != mListener) {
                    mListener!!.onStateChanged(showState)
                }
                if (afterState != VIEW_TAG) {
                    showViewAnim(afterState, VIEW_TAG)
                } else {
                    isAniming = false
                }
            }
        })
        currentAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                currentView.visibility = GONE
                showView.visibility = VISIBLE
                showAnim.start()
                currentState = showState
            }
        })
        currentAnim.start()
    }

    fun getStateView(state: Int): View {
        if (state <= VIEW_TAG) {
            throw RuntimeException("ViewState 不在目标范围")
        }
        return mViews!![state]
    }

    fun addUserView(state: Int, layId: Int): View {
        return setUserDefView(state, null, layId)
    }

    fun addUserView(state: Int, view: View?): View {
        return setUserDefView(state, view, -1)
    }

    private fun setUserDefView(state: Int, view: View?, layId: Int): View {
        var view = view!!
        if (state <= 0) {
            throw RuntimeException("自定义的 ViewState TAG 必须大于 0")
        }
        if (null == view && layId != -1) {
            view = LayoutInflater.from(mContext).inflate(layId, this, false)
        }
        addViewToHash(view, state)
        addViewInLayout(view, -1, view.layoutParams)
        return view
    }

    companion object {
        // 内容 View
        const val VIEW_CONTENT = 0

        // 加载 View
        const val VIEW_LOADING = -1

        // 数据异常( 数据异常指原本应该是有数据，但是服务器返回了错误的、不符合格式的数据 ) View
        const val VIEW_ERROR_DATA = -2

        // 网络异常 View
        const val VIEW_ERROR_NET = -3

        // 数据为空 View
        const val VIEW_EMPTY = -4

        // View 的 Tag 标签值
        private const val VIEW_TAG = -5
    }

    init {
        init(context, attrs)
    }
}