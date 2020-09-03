package com.jzz.treasureship.adapter

import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CommentData

class CommentsChildAdapter(layoutResId: Int = R.layout.comment_child_layout) :
    BaseBindAdapter<CommentData>(layoutResId, BR.data)  {
    override fun convert(helper: BindViewHolder, item: CommentData) {
        super.convert(helper, item)
        helper.setText(R.id.tv_childName,"@${item.nickName}:")
        helper.setText(R.id.tv_childContent, item.content)
        //只支持二级评论
        //helper.addOnClickListener(R.id.layout_childContent)
    }
}