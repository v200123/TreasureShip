package com.jzz.treasureship.adapter

import android.view.View
import android.widget.TextView
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.X
import com.shuyu.gsyvideoplayer.video.GSYSampleADVideoPlayer.GSYADVideoModel


class TraceAdapter(layoutResId: Int = R.layout.item_trace) :
    BaseBindAdapter<X>(layoutResId, BR.traceInfo) {

    companion object {
        private const val TYPE_TOP = 0x0000
        private const val TYPE_NORMAL = 0x0001
    }

    override fun convert(helper: BindViewHolder, item: X) {
        super.convert(helper, item)

        if (getItemViewType(helper.adapterPosition) == TYPE_TOP) {
            // 第一行头的竖线不显示
            helper.getView<TextView>(R.id.tvTopLine).visibility = View.INVISIBLE
            helper.getView<TextView>(R.id.tvDot).background =
                mContext.resources.getDrawable(R.drawable.icon_trace_arrivied)
            helper.getView<TextView>(R.id.tv_content).setTextColor(mContext.resources.getColor(R.color.blue_light))
            helper.getView<TextView>(R.id.tvBotLine).setBackgroundColor(mContext.resources.getColor(R.color.blue_light))
        } else if (getItemViewType(helper.layoutPosition)  == TYPE_NORMAL) {
            helper.getView<TextView>(R.id.tvTopLine).visibility = View.INVISIBLE
            helper.getView<TextView>(R.id.tvDot).background =
                mContext.resources.getDrawable(R.drawable.icon_trace_normal)
            helper.getView<TextView>(R.id.tv_content).setTextColor(mContext.resources.getColor(R.color.Home_text_bold))
        }


        helper.setText(R.id.tvAcceptTime, item.time)
        helper.setText(R.id.tv_content, item.content)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_TOP
        } else TYPE_NORMAL
    }
}