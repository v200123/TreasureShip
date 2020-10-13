package com.jzz.treasureship.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R


class CommentsChildAdapter(layoutResId: Int = R.layout.comment_child_layout) :
    BaseBindAdapter<CommentData>(layoutResId)  {
    override fun convert(helper: BaseViewHolder, item: CommentData) {
        super.convert(helper, item)
        helper.setText(R.id.tv_childName,"${item.nickName}:")
        helper.setText(R.id.tv_childContent, item.content)
        //只支持二级评论
        //helper.addOnClickListener(R.id.layout_childContent)
    }
}