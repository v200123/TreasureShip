package com.jzz.treasureship.view

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
R
adapter.GoodsPropertiesAdapter
model.bean.GoodsAttribute
model.bean.GoodsDetail
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.dialog_goods_properties.view.*
import kotlin.math.roundToInt

class CustomPropertyPopup(context:Context,goodsAttributeList:List<GoodsDetail.GoodsAttribute>) : BottomPopupView(context) {

    private val mList = goodsAttributeList

    override fun getImplLayoutId() = R.layout.dialog_goods_properties

    override fun initPopupContent() {
        super.initPopupContent()
        Log.d("props",mList.toString())

        rv_properties.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
        }
        val mAdapter = GoodsPropertiesAdapter()
        mAdapter.run {
            setList(mList)
            notifyDataSetChanged()
        }

        rv_properties.adapter = mAdapter

        tv_comfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .75f).roundToInt()
    }
}