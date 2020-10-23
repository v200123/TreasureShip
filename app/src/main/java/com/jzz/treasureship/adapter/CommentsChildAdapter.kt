package com.jzz.treasureship.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CommentData


class CommentsChildAdapter(layoutResId: Int = R.layout.comment_child_layout) :
    BaseBindAdapter<CommentData>(layoutResId)  {
    override fun convert(holder: BaseViewHolder, item: CommentData) {
        super.convert(holder, item)
        holder.setText(R.id.tv_childName,"${item.nickName}:")
        holder.setText(R.id.tv_childContent, item.content)
        //只支持二级评论
        //helper.addOnClickListener(R.id.layout_childContent)
    }
}