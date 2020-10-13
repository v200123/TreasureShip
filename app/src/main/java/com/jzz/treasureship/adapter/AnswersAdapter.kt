package com.jzz.treasureship.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.AnswerItem

import org.json.JSONObject

class AnswersAdapter(layoutResId: Int = R.layout.layout_item_answer) : BaseBindAdapter<AnswerItem>(
    layoutResId
) {
    init {
        addChildClickViewIds(R.id.layout_ansItem)
    }

    override fun convert(helper: BaseViewHolder, item: AnswerItem) {
        super.convert(helper, item)

        val itemObj = JSONObject(item.item)

        helper.setText(R.id.tv_choice, "${itemObj.getString("item")}.")
        helper.setText(R.id.tv_content, itemObj.getString("text"))


//        helper.getView<TextView>(R.id.tv_choice).text = "${itemObj.getString("item")}."
//        helper.getView<TextView>(R.id.tv_content).text = itemObj.getString("text")
    }
}
