package com.jzz.treasureship.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import com.bumptech.glide.Glide
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.OrderDetailsBean
import com.lc.liuchanglib.ext.click
import kotlinx.android.synthetic.main.item_order_detail.view.*
import q.rorbin.badgeview.DisplayUtil

/**
 *@PackageName: com.jzz.treasureship.view
 *@Auth： 29579
 **/
class ItemOrderDetailView @JvmOverloads constructor(
   var orderDetailSku: OrderDetailsBean.GoodsSku,
    shopName: String?,shopNo: String, needShowShop: Boolean,
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.item_order_detail, this, true)
        showButton(orderDetailSku)
        showShopName(shopName,shopNo, needShowShop)
        showOtherInformation(orderDetailSku)
    }



    private fun showOtherInformation(orderDetailSku: OrderDetailsBean.GoodsSku){
        Glide.with(this).load(orderDetailSku.mSkuPicture).override(DisplayUtil.dp2px(context,75f)).into(shapeImageView)
        tv_item_order_name.text =if(orderDetailSku.mGoodsType == 1) "【境外商品】${orderDetailSku.mGoodsName}" else orderDetailSku.mGoodsName
        tv_item_order_sku.text = orderDetailSku.mAttrValue
        tv_item_order_count.text = "x${orderDetailSku.mNum}"
        tv_item_order_sku_price.text = orderDetailSku.mPrice.toString()
    }

    private fun showShopName(shopName: String?,shopNo:String, needShowShop: Boolean) {
        if (needShowShop) {
            tv_item_detail_shoop_name.visibility = View.VISIBLE
            shopName?.apply {
                tv_item_detail_shoop_name.text = shopName
            }?:let { tv_item_detail_shoop_name.setCompoundDrawables(null,null,null,null)
                tv_item_detail_shoop_name.text = "订单编号: $shopNo"
            }
        }
    }

    inline fun setViewClickListener(@IdRes clickID:Int, crossinline block:OrderDetailsBean.GoodsSku.()->Unit){
       findViewById<View>(clickID) click {
           block(orderDetailSku)
       }
   }

    private fun showButton(orderDetailSku: OrderDetailsBean.GoodsSku) {
        when (orderDetailSku.mOrderStatus) {
            1 -> {
                tv_item_order_add_shop_car.visibility = View.VISIBLE
                tv_item_order_apply_refund.visibility = View.VISIBLE
            }

            8 -> {
                tv_item_order_add_shop_car.visibility = View.VISIBLE
                tv_item_order_apply_afterSale.visibility = View.VISIBLE
            }
            9 -> {
                tv_item_order_add_shop_car.visibility = View.VISIBLE
                tv_item_order_refunding.visibility = View.VISIBLE

            }

            11 -> {
                tv_item_order_add_shop_car.visibility = View.VISIBLE

            }
        }

    }
}