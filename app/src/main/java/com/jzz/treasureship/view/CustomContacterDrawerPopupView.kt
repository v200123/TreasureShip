package com.jzz.treasureship.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.BaseBindAdapter
import com.jzz.treasureship.model.bean.DataXX
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.core.DrawerPopupView
import kotlinx.android.synthetic.main.custom_contacter_drawer_popup.view.*
import kotlinx.android.synthetic.main.layout_item_contacter_goods.view.*


class CustomContacterDrawerPopupView(context: Context, goods: ArrayList<DataXX>) :
    DrawerPopupView(context) {
    var contacterGoods by PreferenceUtils(PreferenceUtils.CONTACTER_GOODS, -1)
    private val mGoods: ArrayList<DataXX> = goods
    private val mAdapter by lazy { ContacterGoodsAdapter() }

    override fun getImplLayoutId(): Int {
        return R.layout.custom_contacter_drawer_popup
    }

    override fun onCreate() {
        super.onCreate()

        rcv_products.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            adapter = mAdapter

            mAdapter.setNewData(mGoods)
            mAdapter.notifyDataSetChanged()

        }

        layout_reset.setOnClickListener {
            contacterGoods = -1
            rcv_products.findViewById<LinearLayout>(R.id.layout_goodsItem).background =
                context.resources.getDrawable(R.color.white)
            rcv_products.findViewById<LinearLayout>(R.id.layout_goodsItem).findViewById<TextView>(R.id.tv_goodsName)
                .setTextColor(context.resources.getColor(R.color.Home_text_normal))
        }

        layout_comfirm.setOnClickListener {
            this.toggle()
        }
    }

    override fun onShow() {
        super.onShow()
        if (contacterGoods != -1) {
            rcv_products.findViewById<LinearLayout>(R.id.layout_goodsItem).background =
                context.resources.getDrawable(R.color.contact_address_goods_item)
            rcv_products.findViewById<LinearLayout>(R.id.layout_goodsItem).findViewById<TextView>(R.id.tv_goodsName)
                .setTextColor(context.resources.getColor(R.color.Home_text_bold))
        }
    }

    override fun onDismiss() {
        super.onDismiss()
    }

    inner class ContacterGoodsAdapter(layoutResId: Int = R.layout.layout_item_contacter_goods) :
        BaseBindAdapter<DataXX>(layoutResId, BR.dataXX) {

        override fun convert(helper: BindViewHolder, item: DataXX) {
            super.convert(helper, item)

            helper.setText(R.id.tv_goodsName, item.name)

            helper.getView<TextView>(R.id.tv_goodsName).setOnClickListener {
                contacterGoods = helper.layoutPosition
                layout_goodsItem.background = context.resources.getDrawable(R.color.contact_address_goods_item)
                rcv_products.findViewById<LinearLayout>(R.id.layout_goodsItem).findViewById<TextView>(R.id.tv_goodsName)
                    .setTextColor(context.resources.getColor(R.color.Home_text_bold))
            }
        }
    }
}