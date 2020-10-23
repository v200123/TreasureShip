package com.jzz.treasureship.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CommentData


class CommentsAdapter(layoutResId: Int = R.layout.comment_item_layout) :
    BaseBindAdapter<CommentData>(layoutResId) {

    init {
        addChildClickViewIds(
            R.id.iv_praise,
            R.id.tv_comment_content_parent
        )
    }

    override fun convert(holder: BaseViewHolder, item: CommentData) {
        super.convert(holder, item)
        val childAdapter by lazy { CommentsChildAdapter() }
        Glide.with(context).load(item.userAvatar).into(holder.getView(R.id.iv_commnet_head))
        holder.setText(R.id.tv_comment_name, item.nickName)
        holder.setText(R.id.tv_comment_date, item.createTime)
        holder.setText(R.id.tv_praise_count, "${item.likeCount}")
        holder.setText(R.id.tv_comment_content, item.content)
        if (item.isLike == 0) {
            //未点赞
            Glide.with(context).load(ContextCompat.getDrawable(context,R.drawable.icon_unlike))
                .into(holder.getView(R.id.iv_praise))
        } else if (item.isLike == 1) {
            //点赞
            Glide.with(context).load(ContextCompat.getDrawable(context,R.drawable.icon_like))
                .into(holder.getView(R.id.iv_praise))
        }

        holder.getView<RecyclerView>(R.id.mrv_repaly_list).apply {
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
            childAdapter.setList(data)
        }

//        //点赞评论
//        helper.addOnClickListener(R.id.iv_praise)
//        //点外部评论视频
//        helper.addOnClickListener(R.id.layout_comment_item)
//        //点评论回复评论
//        helper.addOnClickListener(R.id.tv_comment_content_parent)
    }
}