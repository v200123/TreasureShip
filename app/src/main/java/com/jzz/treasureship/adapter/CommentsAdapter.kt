package com.jzz.treasureship.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R


class CommentsAdapter(layoutResId: Int = R.layout.comment_item_layout) :
    BaseBindAdapter<CommentData>(layoutResId) {

    init {
        addChildClickViewIds(
            R.id.iv_praise,
            R.id.layout_comment_item,
            R.id.tv_comment_content_parent
        )
    }

    override fun convert(helper: BaseViewHolder, item: CommentData) {
        super.convert(helper, item)
        val childAdapter by lazy { CommentsChildAdapter() }
        Glide.with(context).load(item.userAvatar).into(helper.getView(R.id.iv_commnet_head))
        helper.setText(R.id.tv_comment_name, item.nickName)
        helper.setText(R.id.tv_comment_date, item.createTime)
        helper.setText(R.id.tv_praise_count, "${item.likeCount}")
        helper.setText(R.id.tv_comment_content, item.content)
        if (item.isLike == 0) {
            //未点赞
            Glide.with(context).load(context.resources.getDrawable(R.drawable.icon_unlike))
                .into(helper.getView(R.id.iv_praise))
        } else if (item.isLike == 1) {
            //点赞
            Glide.with(context).load(context.resources.getDrawable(R.drawable.icon_like))
                .into(helper.getView(R.id.iv_praise))
        }

        helper.getView<RecyclerView>(R.id.mrv_repaly_list).apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            adapter = childAdapter


            //                if (item.comments.size > 3) {
//                    helper.getView<LinearLayout>(R.id.layout_seeMore).visibility = View.VISIBLE
//                }else{
//                    helper.getView<LinearLayout>(R.id.layout_seeMore).visibility = View.GONE
//                }

            val data = item.comments
            childAdapter.setNewData(data)
            childAdapter.notifyDataSetChanged()


        }

//        //点赞评论
//        helper.addOnClickListener(R.id.iv_praise)
//        //点外部评论视频
//        helper.addOnClickListener(R.id.layout_comment_item)
//        //点评论回复评论
//        helper.addOnClickListener(R.id.tv_comment_content_parent)
    }
}