package com.jzz.treasureship.ui.orderdetail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.activityViewModels
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
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.fragment_apply_refund.*

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
            mViewModel.submitMsg(mOrderDetailViewModel.id,type?:1,mSelectMsg.mId)

        }
        linearLayout2 click {
            XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    for (i in mReFundMsg!!.indices) {
                        if (mReFundMsg!![i].isSelect) {
                            mSelectMsg = mReFundMsg!![i]
                            break
                        }
                    }
                }
            }).asCustom(
                DialogHasSubmit(
                    mContext,
                    mReFundMsg,
                    object : AdapterHelper.ViewHolderConverter<RefundMsg.data> {
                        override fun convert(helper: BaseViewHolder, item: RefundMsg.data) {
                            helper.setText(R.id.tv_item_submit_name, item.mReason)
                                .setChecked(R.id.cb_item_submit, item.isSelect)
                        }

                    })
            )
                .show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                PictureConfig.REQUEST_CAMERA ->{
                    val obtainMultipleResult = PictureSelector.obtainMultipleResult(data)
                    obtainMultipleResult.forEach { it.androidQToPath }
                }
                // onResult Callback
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

    private fun gotoPhoto(){
        PictureSelector.create(this)
            .openCamera(PictureMimeType.ofImage())
            .isWebp(true)
            .maxSelectNum(3)
            .minSelectNum(1)
            .imageEngine(GlideEngine().createGlideEngine()) // Please refer to the Demo GlideEngine.java
            .forResult(PictureConfig.CHOOSE_REQUEST);
    }
}