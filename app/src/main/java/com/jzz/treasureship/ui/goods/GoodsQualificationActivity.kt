package com.jzz.treasureship.ui.goods

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.GsonUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsDetail
import kotlinx.android.synthetic.main.activity_goods_qualification.*
import kotlinx.android.synthetic.main.include_title.*

/**
 *@date: 2020/9/18
 *@describe:
 *@Auth: 29579
 **/
class GoodsQualificationActivity : AppCompatActivity(R.layout.activity_goods_qualification) {
    private var mType = 0
    private val mAdapter by lazy { qualificationAdapter(this) }

    companion object {
        const val QualificationType = "Qualification"
        const val QualificationTypeList = "QualificationList"
        fun newInstance(context: Context, type: Int, list: String): Intent {
            return Intent(context, GoodsQualificationActivity::class.java)
                .apply {
                    putExtra(QualificationType, type)
                    putExtra(QualificationTypeList, list)
                }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mType = intent.getIntExtra(QualificationType, 0)
        val stringExtra = intent.getStringExtra(QualificationTypeList)
        val good = GsonUtils.fromJson(stringExtra, GoodsDetail::class.java)
        rlback.setOnClickListener { finish() }
        if (mType == 0) {
            tv_title.text = "商家资质"
            ll_qualification_type01.visibility = View.VISIBLE
            tv_qualification_shopName.text = good.shopName
            tv_qualification_shopPhone.text = good.shopQualification.companyPhone
            Glide.with(this).asDrawable().load(good.shopQualification.imageList[0].path)
                .into(iv_qualification_01)

        } else {
            tv_title.text = "产品参数"
            tv_qualification_productName.text = good.goodsName
            ll_qualification_type02.visibility = View.VISIBLE
            tv_qualification_from.text = if(good.goodsType ==1) "中国" else "境外"
            tv_qualification_type.text = good.goodsAttributeList[0].attrValue
            tv_qualification_features.text = good.goodsAttributeList[0].attrValueName
            tv_qualification_norm.text = good.goodsSku?.get(0)?.specValue ?: ""
            if(good.instructionsPictureList.isNotEmpty()) {
                Glide.with(this).asDrawable().load(good.instructionsPictureList[0])
                    .into(iv_produce_image01)
            }else{
                tv_qualification_use.visibility = View.GONE
            }
            if(good.qualificationPictureList.isNotEmpty()) {
                Glide.with(this).asDrawable().load(good.qualificationPictureList[0])
                    .into(iv_produce_image02)
            }else{
                tv_qualification_shop.visibility = View.GONE
            }
            if(good.specialDescriptionPictureList.isNotEmpty()) {
                Glide.with(this).asDrawable().load(good.specialDescriptionPictureList[0])
                    .into(iv_produce_image03)
            }else{
                tv_qualification_safe.visibility = View.GONE
            }
        }

    }

    class qualificationAdapter(var mActivity: AppCompatActivity) :
        BaseQuickAdapter<String, BaseViewHolder>(
            R.layout.item_simple_imageview
        ) {
        override fun convert(
            holder: BaseViewHolder,
            item: String
        ) {

            Glide.with(mActivity).asDrawable().load(item).into(holder.getView(R.id.iv_item))

        }
    }
}