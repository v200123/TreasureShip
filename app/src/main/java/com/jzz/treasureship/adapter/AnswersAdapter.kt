package com.jzz.treasureship.adapter

import android.widget.TextView
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.AnswerItem
import org.json.JSONObject

class AnswersAdapter(layoutResId: Int = R.layout.layout_item_answer) : BaseBindAdapter<AnswerItem>(
    layoutResId,
    BR.answerItem
) {

override fun convert(helper: BindViewHolder, item: AnswerItem) {
        super.convert(helper, item)

        val itemObj = JSONObject(item.item)

        helper.addOnClickListener(R.id.layout_ansItem)

        helper.getView<TextView>(R.id.tv_choice).text = "${itemObj.getString("item")}."
        helper.getView<TextView>(R.id.tv_content).text = itemObj.getString("text")
    }
}
