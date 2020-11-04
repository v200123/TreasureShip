package com.jzz.treasureship.ui.orderdetail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.GlideEngine
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.RefundMsg
import com.jzz.treasureship.ui.orderdetail.viewModel.ApplyRefundViewModel
import com.jzz.treasureship.ui.orderdetail.viewModel.OrderDetailViewModel
import com.jzz.treasureship.view.DialogHasSubmit
import com.jzz.treasureship.view.DialogSimpleList
import com.lc.liuchanglib.adapterUtils.AdapterHelper
import com.lc.liuchanglib.adapterUtils.setChecked
import com.lc.liuchanglib.ext.click
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_apply_refund.*
import kotlinx.android.synthetic.main.include_title.*

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 **/
class ApplyRefundFragment : BaseVMFragment<ApplyRefundViewModel>() {
    val type by lazy { arguments?.getInt(RefundType) }
    val mOrderDetailViewModel by activityViewModels<OrderDetailViewModel>()


    private var mReFundMsg: List<RefundMsg.data>? = null

    //    用于保存已选中的理由
    private var mSelectMsg = RefundMsg.data()
        set(value) {
            tv_show_refund_msg.text = value.mReason
            field = value
        }

    companion object {
        const val RefundType = "com.ApplyRefundFragment.type"
        fun newInstance(type: Int) = ApplyRefundFragment().apply {
            arguments = Bundle().apply {
                putInt(RefundType, type)
            }
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_apply_refund

    override fun initVM(): ApplyRefundViewModel = ApplyRefundViewModel()

    override fun initView() {

        tv_title.text = "申请退款"

        // TODO: 2020/11/4 需要增加一个对ViewGroup监听的事件


        if (type == 2) {
            view9.visibility = View.VISIBLE
            fl_apply_refund_goods.visibility = View.VISIBLE
            ll_apply_refund_container.visibility = View.VISIBLE

        }
        mOrderDetailViewModel.singleOrderInfo?.let {
            tv_apply_refund_shop_name.text = it.mShopName
            Glide.with(this).asDrawable().load(it.mSkuPicture)
                .into(tv_item_order_list_image)
            tv_apply_refund_name.text = it.mGoodsName
            tv_apply_refund_sku.text = it.mAttrValue
            tv_apply_refund_sku_price.text = "￥" + it.mPrice
            tv_apply_refund_count.text = "x ${it.mNum}"
            tv_apply_refund_total_price.text = "￥${it.mPayMoney}"
        } ?: {

        }

    }

    override fun initData() {
        mViewModel.getRefundInformation(type ?: 1)
    }

    override fun startObserve() {
        mViewModel.refundMsg.observe(this) {
            mReFundMsg = it.mList.apply { get(0).isSelect = true }
        }
    }

    override fun initListener() {

        add_refund click {
            XPopup.Builder(mContext).asCustom(
                DialogSimpleList(
                    mContext,
                    arrayOf("从手机相册选择", "拍摄")
                ).apply {
                    click = {
                        if (it == 0) {
                            gotoPhoto()
                        } else {
                            gotoCamera()
                        }
                        this.dismiss()
                    }
                }
            ).show()
        }

        tv_apply_refund_submit click {
            mViewModel.submitMsg(
                mOrderDetailViewModel.id,
                type ?: 1,
                mSelectMsg.mId,
                mOrderDetailViewModel.singleOrderInfo!!.mId.toString()
            )
        }
        linearLayout2 click {
            XPopup.Builder(mContext)

                .asCustom(
                    DialogHasSubmit(
                        mContext,
                        mReFundMsg,
                        object : AdapterHelper.ViewHolderConverter<RefundMsg.data> {

                            override fun convert(helper: BaseViewHolder, item: RefundMsg.data) {
                                helper.setText(R.id.tv_item_submit_name, item.mReason)
                                    .setChecked(R.id.cb_item_submit, item.isSelect)
                            }
                        }) { baseQuickAdapter: BaseQuickAdapter<*, *>, view: View, i: Int ->
                        mSelectMsg = mReFundMsg!![i]
                        mReFundMsg!!.forEach { it.isSelect = false }
                        mReFundMsg!!.get(i).isSelect = true
                        baseQuickAdapter.notifyDataSetChanged()
                    }
                )
                .show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST, PictureConfig.REQUEST_CAMERA -> {
                    val obtainMultipleResult = PictureSelector.obtainMultipleResult(data)
                    obtainMultipleResult.forEach {
                        ll_apply_refund_container
                            .addView(
                                layoutInflater.inflate(
                                    R.layout.layout_imageview_close, ll_apply_refund_container, false
                                ).apply {
                                    Glide.with(this@ApplyRefundFragment)
                                        .load(it.path)
                                        .into(findViewById(R.id.iv_layout_image))
                                    findViewById<ImageView>(R.id.iv_layout_image) click {
                                        ll_apply_refund_container.removeView(this)
                                        add_refund.visibility = View.VISIBLE
                                    }
                                }, ll_apply_refund_container.childCount - 1
                            )
                    }
                    if (ll_apply_refund_container.childCount > 3) {
                        add_refund.visibility = View.GONE
                    }
                }

            }
        }
    }


    private fun gotoCamera() {
        PictureSelector.create(this)
            .openCamera(PictureMimeType.ofImage())
            .isWebp(true)
            .imageEngine(GlideEngine().createGlideEngine()) // Please refer to the Demo GlideEngine.java
            .forResult(PictureConfig.REQUEST_CAMERA);
    }

    private fun gotoPhoto() {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())
            .isWebp(true)
            .maxSelectNum(3)
            .minSelectNum(1)
            .imageEngine(GlideEngine().createGlideEngine()) // Please refer to the Demo GlideEngine.java
            .forResult(PictureConfig.CHOOSE_REQUEST);
    }
}