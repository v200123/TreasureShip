package com.jzz.treasureship.ui.withdraw

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ext.getResColor
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.fragment_with_draw.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.layout_withdraw_comfirm.view.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.math.BigDecimal


class WithdrawActivity : BaseVMActivity<WithdrawViewModel>() {

  

    private var withDrawMoney: String? = "0.00"
    var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")

    var userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    var confirmWithdraw by PreferenceUtils(PreferenceUtils.COMFIRM_WITHDRAW, false)

    override fun getLayoutResId() = R.layout.fragment_with_draw

    override fun initVM(): WithdrawViewModel = getViewModel()

    override fun initView() {
//        activity!!.nav_view.visibility = View.GONE
        tv_title.text = "提现"
        StateAppBar.setStatusBarLightMode(this, getResColor(R.color.white))
        rlback.setOnClickListener {
           finish()
        }
        //TODO 提现服务协议还没有，先屏蔽
        layout_lic.visibility = View.INVISIBLE

        val userObj = GsonUtils.fromJson(userJson, User::class.java)
        tv_withDrawAll.setOnClickListener {
            edit_price.setText(withDrawMoney)
            edit_price.setSelection(edit_price.text.length)

        }

        edit_price.addTextChangedListener(object : TextWatcher {
            // 是否需要删除末尾
            var deleteLastChar = false

            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    return
                }
                if (deleteLastChar) {
                    // 设置新的截取的字符串
                    edit_price.setText(s.toString().substring(0, s.toString().length - 1))
                    // 光标强制到末尾
                    edit_price.setSelection(edit_price.text.length)
                }
                // 以小数点开头，前面自动加上 "0"
                if (s.toString().startsWith(".")) {
                    edit_price.setText("0$s")
                    edit_price.setSelection(edit_price.text.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().contains(".")) {
                    // 如果点后面有超过三位数值,则删掉最后一位
                    val length: Int = s!!.length - s.toString().lastIndexOf(".")
                    // 说明后面有三位数值
                    deleteLastChar = length >= 4
                }
            }
        })

        edit_price.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            val etMoney = v as EditText
            if (!hasFocus and (etMoney.text != null) and etMoney.text.toString().endsWith(".")) {
                etMoney.setText(etMoney.text.subSequence(0, etMoney.text.length - 1))
                etMoney.setSelection(etMoney.text.length)
            }
        }

        btn_submint.setOnClickListener {
            //TODO 提现服务协议还没有，先屏蔽
//            if (!cb_protocol.isChecked) {
//                ToastUtils.showShort("请先仔细阅读《提现服务协议》，并勾选")
//                return@setOnClickListener
//            }

            val value = BigDecimal(edit_price.text.toString())
            val minValue = BigDecimal("0.03")
            if (value < minValue) {
                ToastUtils.showShort("最低提现金额：3分")
                return@setOnClickListener
            }

            val compValue = BigDecimal(withDrawMoney)
            //0:等于    >0:大于    <0:小于
            val result = value.compareTo(compValue)
            if (result > 0) {
                ToastUtils.showShort("提现金额不在可提现金额范围内！请确认并重新输入正确的提现金额")
                return@setOnClickListener
            } else {
                if (userObj.wxOpenid.isNullOrBlank()) {
                    ToastUtils.showShort("提现需先绑定微信！")
                } else {
                    //提现对话框
                    XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView) {
                            super.onDismiss(popupView)
                            if (confirmWithdraw) {
                                mViewModel.askWithdraw(edit_price.text.toString())
                            }
                        }
                    }).asCustom(CustomWithDrawDialog(mContext, edit_price.text.toString())).show()
                    //mViewModel.askWithdraw(edit_price.text.toString())
                }
            }
        }

        if (userObj.wxOpenid.isNullOrBlank()) {
            Glide.with(this).load(ContextCompat.getDrawable(mContext,R.drawable.icon_withdraw_wechat)).into(icon_avatar)
            tv_wx_name.text = "请绑定微信"
            layout_user_bind.setOnClickListener {
                if (!App.iwxapi.isWXAppInstalled) {
                    ToastUtils.showShort("未安装微信客户端，无法使用微信授权")
                    return@setOnClickListener
                }
                val req: SendAuth.Req = SendAuth.Req()
                req.scope = "snsapi_userinfo"
                req.state = "treasureship_wx_bind"

                val wxReturn = App.iwxapi.sendReq(req)
                val wxCode by PreferenceUtils(PreferenceUtils.WX_CODE_BIND, "")
                if (wxReturn and wxCode.isNotBlank()) {
                    mViewModel.bindWeChat(wxCode)
                }
            }
        } else {
            Glide.with(this).load(userObj.wxAvatar).apply(RequestOptions.bitmapTransform(CircleCrop())).into(icon_avatar)
            tv_wx_name.text = userObj.wxNickName
        }
    }

    override fun initData() {
        mViewModel.getWallet()
    }

    override fun startObserve() {

        mViewModel.canWithDraw.observe(this,{
            edit_price.hint = "可提现$it"
            withDrawMoney = BigDecimal(it).toString()
        })
        mViewModel.isUse.observe(this, {
            if(it.mCouponStatus == 1)
                App.dialogHelp.showWithdrawSuccess()
        })
        mViewModel.apply {
            val xPopup = XPopup.Builder(mContext).asLoading()
            userState.observe(this@WithdrawActivity, Observer { it ->
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    userJson = GsonUtils.toJson(it)
                    var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE_BIND, "")
                    wxCode = ""
                    userJson = GsonUtils.toJson(it)
                    access = it.accessToken!!
                    Glide.with(mContext).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(icon_avatar)
                    tv_wx_name.text = it.nickName

                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }
            })

            withDrawState.observe(this@WithdrawActivity, Observer {
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    confirmWithdraw = false
                    ToastUtils.showShort("${it}")
                    supportFragmentManager.popBackStack()
                    mViewModel.getCouponUse()


                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }
            })
        }

//        mViewModel.isUse.observe(this, {
//            App.dialogHelp.showWithdrawSuccess()
//        })
    }



    inner class CustomWithDrawDialog(context: Context, withDrawMoney: String) : CenterPopupView(context) {

        private val mWithDrawMoney = withDrawMoney

        override fun getImplLayoutId() = R.layout.layout_withdraw_comfirm

        override fun initPopupContent() {
            super.initPopupContent()

            tv_wantWithDrawMoney.text = "¥ $mWithDrawMoney"

//            val tmp = MoneyUtil.moneyMul("$mWithDrawMoney", "0.2")
//            tv_taxMoney.text = "¥ ${BigDecimal(tmp).stripTrailingZeros().toPlainString()}"

//            val tmp2 = BigDecimal(MoneyUtil.moneySub("$mWithDrawMoney", "${tmp}")).stripTrailingZeros().toPlainString()
            tv_actualGetMoney.text =
                "¥ ${mWithDrawMoney}"

            layout_dissmiss.setOnClickListener {

                this.dismiss()
            }

            layout_confirm.setOnClickListener {
                confirmWithdraw = true
                dismiss()
            }
        }

    }
}