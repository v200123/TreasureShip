package com.jzz.treasureship.ui.address

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomAddPickerBottomPopup
import com.lc.mybaselibrary.assertRead.Companion.getFromAssets
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class AddAddressFragment : BaseVMFragment<AddressViewModel>() {
    private var allPlace by PreferenceUtils(PreferenceUtils.ALL_PLACES, "")
    var topAddressObj: CityPlace? = null
    var midAddressObj: CityPlace? = null
    var lastAddressObj: CityPlace? = null
    override fun getLayoutResId() = R.layout.fragment_add_address

    override fun initVM(): AddressViewModel = getViewModel()

    companion object {
        fun newInstance(): AddAddressFragment {
            return AddAddressFragment()
        }
    }

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        tv_title.text = "新增地址"
        StateAppBar.setStatusBarLightMode(this.activity, mContext.resources.getColor(R.color.white))
        lin_address.setOnClickListener { view ->
            if (allPlace.isNullOrBlank()) {
                try {
                    val fromAssets = getFromAssets(mContext, "city.json")
                    val city = GsonUtils.fromJson(fromAssets, CityPlaces::class.java)
                    showAddressSelect(city)
                } catch (e: Exception) {
                    ToastUtils.showShort("文件下载错误,开始网络请求")
                    mViewModel.getCityPlaces()
                }

            } else {
                val places = GsonUtils.fromJson(allPlace, CityPlaces::class.java)
                XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {

                    override fun onDismiss(popupView: BasePopupView) {
//                        super.onDismiss(popupView)
                        val lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                        if (lastAddress.isNotBlank()) {
                            lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
                        }

                        val topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                        if (topAddress.isNotBlank()) {
                            topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
                        }

                        val midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                        if (midAddress.isNotBlank()) {
                            midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
                        }

                        tv_adress.text =
                            "${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}"
                    }

                }).asCustom(CustomAddPickerBottomPopup(mContext, places)).show()
            }
        }

        btn_saveAddress.setOnClickListener {
            topAddressObj?.let { top ->
                midAddressObj?.let { mid ->
                    lastAddressObj?.let { last ->
                        mViewModel.addAddress(
                            et_name.text.toString(), et_mobile.phoneText, et_mxadress.text.toString(),
                            top, mid, last, if (cb_defult_addr.isChecked) {
                                1
                            } else {
                                0
                            }
                        )
                    }
                }
            }
        }
    }

    private fun showAddressSelect(city: CityPlaces) {
        XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {

            override fun onShow(popupView: BasePopupView?) {
                super.onShow(popupView)
                var lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                var topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                var midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                lastAddress = ""
                topAddress = ""
                midAddress = ""

            }

            override fun onDismiss(popupView: BasePopupView) {
//                        super.onDismiss(popupView)
                val lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                if (lastAddress.isNotBlank()) {
                    lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
                }

                val topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                if (topAddress.isNotBlank()) {
                    topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
                }

                val midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                if (midAddress.isNotBlank()) {
                    midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
                }

                tv_adress.text =
                    "${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}"
            }

        }).asCustom(CustomAddPickerBottomPopup(mContext, city)).show()


    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel.apply {
            val loadingPopup = XPopup.Builder(context).asLoading()
            cityPlacesState.observe(this@AddAddressFragment, Observer {

                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let { places ->
                    allPlace = GsonUtils.toJson(places)
                    var topAddressObj: CityPlace? = null
                    var midAddressObj: CityPlace? = null
                    var lastAddressObj: CityPlace? = null
                    XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView) {
//                            super.onDismiss(popupView)
                            val lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                            if (lastAddress.isNotBlank()) {
                                lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
                            }

                            val topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                            if (topAddress.isNotBlank()) {
                                topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
                            }

                            val midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                            if (midAddress.isNotBlank()) {
                                midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
                            }

                            tv_adress.text =
                                "${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}"
                        }

                    }).asCustom(CustomAddPickerBottomPopup(mContext, places)).show()

                    btn_saveAddress.setOnClickListener {
                        topAddressObj?.let { top ->
                            midAddressObj?.let { mid ->
                                lastAddressObj?.let { last ->
                                    mViewModel.addAddress(
                                        et_name.text.toString(), et_mobile.phoneText, et_mxadress.text.toString(),
                                        top, mid, last, if (cb_defult_addr.isChecked) {
                                            1
                                        } else {
                                            0
                                        }
                                    )
                                }
                            }
                        }
                    }
                    loadingPopup.dismiss()
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                    loadingPopup.dismiss()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@AddAddressFragment.context, LoginActivity::class.java))
                    }
                }
            })

            addressState.observe(this@AddAddressFragment, Observer {

                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let {
                    loadingPopup.dismiss()
                    activity!!.supportFragmentManager.popBackStack()
                    ToastUtils.showShort("操作成功")
                }

                it.showError?.let { message ->
                    loadingPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    loadingPopup.dismiss()
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@AddAddressFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        btn_saveAddress.setOnClickListener {
            topAddressObj?.let { top ->
                midAddressObj?.let { mid ->
                    lastAddressObj?.let { last ->
                        mViewModel.addAddress(
                            et_name.text.toString(), et_mobile.phoneText, et_mxadress.text.toString(),
                            top, mid, last, if (cb_defult_addr.isChecked) {
                                1
                            } else {
                                0
                            }
                        )
                    }
                }
            }
        }
    }
}
