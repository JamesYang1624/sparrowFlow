package com.yangwz.sparrowflow.app

import android.app.Application
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ProcessUtils
import io.microshow.rxffmpeg.RxFFmpegInvoke
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver
import com.netease.nimlib.sdk.util.NIMUtil
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.auth.AuthService

import com.netease.nimlib.sdk.auth.AuthServiceObserver


/**
 * @author : JamesYang
 * @date : 2021/11/30 12:20
 * @GitHub : https://github.com/JamesYang1624
 */
class MyApp : Application() {
    val TAG = "MyAppTag"
    private var isDebug: Boolean? = null
    private var isMainProcess: Boolean? = null


    override fun onCreate() {
        super.onCreate()
        initLog()
        initRxFF()
        initNim()
    }

    private fun initNim() {
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将进行自动登录）。不能对初始化语句添加进程判断逻辑。
        NIMClient.init(this, loginInfo(), options())
        // 使用 `NIMUtil` 类可以进行主进程判断。
        // boolean mainProcess = NIMUtil.isMainProcess(context)
        if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
            LogUtils.i(TAG, "NimInit isMainProcess")
            NIMClient.getService(SdkLifecycleObserver::class.java)
                .observeMainProcessInitCompleteResult(
                    Observer {
                        if (it != null && it) {
                            // 主进程初始化完毕，可以开始访问数据库
                            LogUtils.i(
                                TAG,
                                "NimInit NimDebug observeMainProcessInitCompleteResult 主进程初始化完毕，可以开始访问数据库"
                            )
                        }
                    }, true
                )

            NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus(
                { status ->
                    //获取状态的描述

                    val desc = status.desc
                    if (status.shouldReLogin() || status.wontAutoLogin()) {
                        ToastUtils.showShort("云信离线")
                    } else if (status == StatusCode.LOGINED) {
                        ToastUtils.showShort("云信登陆成功")
                    }
//                    if (status.wontAutoLogin()) {
//                        // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
//                        LogUtils.i(
//                            TAG,
//                            "NimInit NimDebug 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作"
//                        )
//                    }
                    LogUtils.json("$TAG NimInit NimDebug observeOnlineStatus 在线状态监听 ", status)
                }, true
            )
        }


//        NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
//            @Override
//            public void onEvent(Boolean aBoolean) {
//                if (aBoolean != null && aBoolean) {
//                    // 主进程初始化完毕，可以开始访问数据库
//                    ...
//                }
//            }
//        }, true);
    }

    // 如果提供，将同时进行自动登录。如果当前还没有登录用户，请传入null。详见自动登录章节。
    private fun loginInfo(): LoginInfo? {
        val info = LoginInfo(ChatAccId.NimAccId, ChatAccId.NimToken)
//        return if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
//            DemoCache.setAccount(account.toLowerCase())
//            LoginInfo(account, token)
//        } else {
//            null
//        }
        return info
    }

    // 设置初始化配置参数，如果返回值为 null，则全部使用默认参数。
    private fun options(): SDKOptions {
        val options = SDKOptions()
        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true

        return options
    }

    private fun initRxFF() {
        RxFFmpegInvoke.getInstance().setDebug(true)
    }

    private fun initLog() {

        val config =
            LogUtils.getConfig().setLogSwitch(isDebug())// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(isDebug())// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印 log 时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
                .setFileExtension(".log")// 设置日志文件后缀
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
                // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
                .addFileExtraHead("ExtraKey", "ExtraValue")
        LogUtils.i(config.toString())
    }

    private fun isDebug(): Boolean {
        if (isDebug == null) isDebug = AppUtils.isAppDebug()
        return isDebug!!
    }

    fun isMainProcess(): Boolean {
        if (isMainProcess == null) isMainProcess = ProcessUtils.isMainProcess()
        return isMainProcess!!
    }

//    private fun doLogin() {
//        val info = LoginInfo("cc","940862d53b3c1e276bf1942731fec373")
//        val callback: RequestCallback<LoginInfo> = object : RequestCallback<LoginInfo> {
//            override fun onSuccess(param: LoginInfo?) {
//
//                LogUtils.i(TAG, "login success")
//                // your code
//            }
//
//            override fun onFailed(code: Int) {
//                if (code == 302) {
//                    LogUtils.i(TAG, "login onFailed $code 账号密码错误")
//                    // your code
//                } else {
//                    // your code
//                    LogUtils.i(TAG, "login onFailed $code")
//                }
//            }
//
//            override fun onException(exception: Throwable) {
//                LogUtils.i(TAG, "login onException ${exception.message}")
//                // your code
//            }
//        }
//
//        //执行手动登录
//
//        //执行手动登录
//        NIMClient.getService(AuthService::class.java).login(info).setCallback(callback)
//
//    }
}
