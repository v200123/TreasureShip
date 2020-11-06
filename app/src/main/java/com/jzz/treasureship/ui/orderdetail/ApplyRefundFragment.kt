package com.jzz.treasureship.ui.orderdetail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.GlideEngine
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.RefundMsg
import com.jzz.treasureship.model.bean.UploadImgBean
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
import kotlinx.android.synthetic.main.after_sale_part.*
import kotlinx.android.synthetic.main.fragment_apply_refund.*
import kotlinx.android.synthetic.main.include_title.*
import q.rorbin.badgeview.DisplayUtil
import java.io.File

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 **/
class ApplyRefundFragment : BaseVMFragment<ApplyRefundViewModel>() {
    val type by lazy { arguments?.getInt(RefundType) }
    private val mOrderDetailViewModel by activityViewModels<OrderDetailViewModel>()
    private var mSkuIds = ""
    private val mPhotoList = mutableListOf<UploadImgBean>()
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

        if (type == 2) {
            view9.visibility = View.VISIBLE
            fl_apply_refund_goods.visibility = View.VISIBLE
            ll_apply_refund_container.visibility = View.VISIBLE

        }
        mOrderDetailViewModel.singleOrderInfo?.let {
            cl_apply_refund.visibility = View.VISIBLE
            tv_refund_item_shop_name.text = it.mShopName
            Glide.with(this).asDrawable().load(it.mSkuPicture)
                .into(tv_item_order_list_image)
            tv_apply_refund_name.text = it.mGoodsName
            tv_apply_refund_sku.text = it.mAttrValue
            tv_apply_refund_sku_price.text = "￥" + it.mPrice
            tv_apply_refund_count.text = "x ${it.mNum}"
            tv_apply_refund_total_price.text = "￥${it.mPayMoney}"
        } ?: run {
            vs_apply_refund.visibility = View.VISIBLE
            var mTotalPrice = 0f
            tv_refund_item_shop_name.text = mOrderDetailViewModel.mingleOrderInfo[0].mShopName
            mOrderDetailViewModel.mingleOrderInfo.forEach {
                mTotalPrice += it.mPayMoney!!
                mSkuIds += "${it.mId},"
                ll_after_sale_part.addView(
                    layoutInflater.inflate(
                        R.layout.item_after_sale_part,
                        ll_after_sale_part,
                        false
                    ).apply {
                        Glide.with(this@ApplyRefundFragment).asDrawable().load(it.mSkuPicture)
                            .into(findViewById(R.id.iv_item_after_sale_part))
                        findViewById<TextView>(R.id.tv_item_after_sale_part).text = "￥" + it.mPrice
                    })
            }
            tv_apply_refund_total_price.text = "￥" + mTotalPrice
        }
    }

    override fun initData() {
        mViewModel.getRefundInformation(type ?: 1)
    }

    override fun startObserve() {
        mViewModel.refundMsg.observe(this) {
            mReFundMsg = it.mList.apply { get(0).isSelect = true }
        }
        mViewModel.ImageResultData.observe(this) {
            ToastUtils.showShort("图片上传成功")

            ll_apply_refund_container
                .addView(
                    layoutInflater.inflate(
                        R.layout.layout_imageview_close, ll_apply_refund_container, false
                    ).apply {
                        Glide.with(this@ApplyRefundFragment)
                            .asDrawable()
                            .override(DisplayUtil.dp2px(mContext,74f))
                            .load(it.url)
                            .into(findViewById(R.id.iv_layout_image))


                        findViewById<ImageView>(R.id.iv_layout_close) click {
                            val indexOfChild = ll_apply_refund_container.indexOfChild(this)
                            mPhotoList.removeAt(indexOfChild)
                            ll_apply_refund_container.removeView(this)
                            add_refund.visibility = View.VISIBLE
                        }
                    }, ll_apply_refund_container.childCount - 1
                )
            if (ll_apply_refund_container.childCount > 3) {
                add_refund.visibility = View.GONE
            }
            mPhotoList.add(it)
        }

        mViewModel.refundResultData.observe(this) {
            ToastUtils.showShort("申请上传成功${it.mId}")
        }
    }

    override fun initListener() {
        add_refund click {
            XPopup.Builder(mContext).asCustom(
                DialogSimpleList(
                    mContext,
                    mutableListOf("从手机相册选择", "拍摄")
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
            var photoListString = ""

            mPhotoList.forEachIndexed { i, bean ->
                if (i != mPhotoList.size - 1) {
                    photoListString += "${bean.url},"
                } else {
                    photoListString += bean.url
                }

            }
            mViewModel.submitMsg(
                mOrderDetailViewModel.id,
                type ?: 1,
                mSelectMsg.mId,
                mOrderDetailViewModel.singleOrderInfo?.run { mId.toString() } ?: let { mSkuIds },
                et_apply_refund_remark.text.toString(), photoListString
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
                        mViewModel.uploadImg(File(it.realPath))
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
            .maxSelectNum(1)
            .minSelectNum(1)
            .imageEngine(GlideEngine().createGlideEngine()) // Please refer to the Demo GlideEngine.java
            .forResult(PictureConfig.CHOOSE_REQUEST);
    }
}