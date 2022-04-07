package com.yangwz.sparrowflow.activity

import android.annotation.SuppressLint
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SystemMessageType
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.yangwz.common.base.BaseActivity
import com.yangwz.sparrowflow.adapter.ChatListAdapter
import com.yangwz.sparrowflow.databinding.ActivityNimChatBinding
import com.netease.nimlib.sdk.msg.model.RecentContact

import com.netease.nimlib.sdk.RequestCallbackWrapper

import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum

import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum

import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.RequestCallback
import com.yangwz.sparrowflow.app.ChatAccId
import kotlinx.android.synthetic.main.activity_nim_chat.*


/**
 * @author : yangweizheng
 * @date : 2022/2/17 14:34
 */
class NimChatActivity : BaseActivity<ActivityNimChatBinding>(ActivityNimChatBinding::inflate) {
    private var list: MutableList<IMMessage> = mutableListOf()
    private val adapter by lazy {
        ChatListAdapter(list)
    }

    override fun initViewModel() {
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.tvLoginAccId.text = "当前登陆用户accid ： ${ChatAccId.NimAccId}"
        binding.ChatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ChatRecyclerView.adapter = adapter

        binding.sendMsg.setOnClickListener {
            sendMsg()
        }
        binding.queryMsgHistory.setOnClickListener {
            queryMsgHistory()
        }
    }

    private fun sendMsg() {
        if (binding.intPutMsgText.text.toString().isNullOrEmpty()){
            ToastUtils.showShort("消息为空")
            return
        }
        // 该帐号为示例
        // 该帐号为示例
        val account = binding.intPutAccTd.text.toString()
// 以单聊类型为例
// 以单聊类型为例
        val sessionType = SessionTypeEnum.P2P
        val text = binding.intPutMsgText.text.toString()
// 创建一个文本消息
// 创建一个文本消息
        val textMessage = MessageBuilder.createTextMessage(account, sessionType, text)
// 发送给对方
// 发送给对方
        NIMClient.getService(MsgService::class.java).sendMessage(textMessage, false)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {
                    LogUtils.json(
                        "$TAG NimDebug sendMsg  onSuccess 成功",
                        textMessage
                    )
                    list.add(textMessage)
                    adapter.setList(list = list)
                }

                override fun onFailed(code: Int) {
                    LogUtils.json(
                        "$TAG NimDebug sendMsg   onFailed",
                        code
                    )
                }

                override fun onException(exception: Throwable) {
                    LogUtils.json(
                        "$TAG NimDebug sendMsg   onException",
                        exception
                    )
                }
            })

    }

    override fun initData() {
//        observerMsg()
        pullMessagesSynchronously()
    }

    private fun queryMsgHistory() {
        if (binding.intPutMsgText.text.toString().isNullOrEmpty()){
            ToastUtils.showShort("查询用户为空")
            return
        }
// 查询比 anchor时间更早的消息，查询20条，结果按照时间降序排列
        val anchor =
            MessageBuilder.createEmptyMessage(
                binding.intPutAccTd.text.toString(),
                SessionTypeEnum.P2P,
                System.currentTimeMillis()
            )
        LogUtils.json(
            "$TAG NimDebug queryMessageListEx  锚点消息  ",
            anchor
        )
// 查询比 anchor时间更早的消息，查询20条，结果按照时间降序排列
        NIMClient.getService(MsgService::class.java).queryMessageListEx(
            anchor, QueryDirectionEnum.QUERY_OLD,
            100, true
        ).setCallback(object : RequestCallbackWrapper<List<IMMessage>>() {
            override fun onResult(code: Int, result: List<IMMessage>, exception: Throwable?) {
                if (code == 200) {
                    LogUtils.json(
                        "$TAG NimDebug queryMessageListEx    ",
                        result
                    )
                    list.addAll(result)
                    adapter.setList(list)
                } else {

                    LogUtils.i(
                        "$TAG NimDebug queryMessageListEx code =    ",
                        code
                    )

                    LogUtils.json(
                        "$TAG NimDebug queryMessageListEx exception =    ",
                        exception
                    )
                }

            }

        })

    }

    private fun observerMsg() {
        val incomingMessageObserver: Observer<List<IMMessage>> =
            Observer<List<IMMessage>>() {
                @Override
                fun onEvent(messages: List<IMMessage>) {

                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    LogUtils.json("$TAG NimDebug observerMsg new message", messages)
                    LogUtils.json(
                        "$TAG NimDebug observerMsg    MsgTypeEnum.text.value",
                        MsgTypeEnum.text.value
                    )
                    list.addAll(messages)
                    adapter.setList(list)
                }
            }
        NIMClient.getService(
            MsgServiceObserve::class.java
        )
            .observeReceiveMessage(incomingMessageObserver, true)
    }

    /**
     * 同步云信消息
     */
    fun pullMessagesSynchronously() {
        //同步云信消息
        val incomingMessageObserver: Observer<List<IMMessage>> =
            Observer<List<IMMessage>> { messages ->
                newNewsMsgComing(messages)
            }
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeReceiveMessage(incomingMessageObserver, true)
        LogUtils.json(
            "$TAG NimDebug 初始化 同步云信消息方法",
            incomingMessageObserver
        )
    }

    private fun newNewsMsgComing(messages: List<IMMessage>) {
        LogUtils.json(
            "$TAG NimDebug 新消息 NewMessageComing",
            messages
        )
        list.addAll(messages)
        adapter.setList(list = list)
    }

}