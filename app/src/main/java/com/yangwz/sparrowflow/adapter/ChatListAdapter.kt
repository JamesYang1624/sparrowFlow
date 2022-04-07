package com.yangwz.sparrowflow.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.yangwz.sparrowflow.R


/**
 * @author : yangweizheng
 * @date : 2022/2/17 14:57
 */
class ChatListAdapter(private var list: MutableList<IMMessage>) :
    BaseQuickAdapter<IMMessage, BaseViewHolder>(R.layout.item_chat_list) {
    override fun convert(holder: BaseViewHolder, item: IMMessage) {
        holder.setText(R.id.tvContent, item.content)
    }
}