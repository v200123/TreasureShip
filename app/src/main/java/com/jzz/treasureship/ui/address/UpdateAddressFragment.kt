package com.jzz.treasureship.ui.address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.Address
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomAddPickerBottomPopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class UpdateAddressFragment : BaseVMFragment<AddressViewModel>() {
    override fun getLayoutResId() = R.layout.fragment_add_address

    override fun initVM(): AddressViewModel = getViewModel()

    var topAddressObj: CityPlace? = null
    var midAddressObj: CityPlace? = null
    var lastAddressObj: CityPlace? = null

    var mAddress: Address? = null

    private var addressId: Int = -1

    companion object {
        fun newInstance(address: Address): UpdateAddressFragment {
            val f = UpdateAddressFragment()
            val bundle = Bundle()
            bundle.putParcelable("address", address)
            f.arguments = bundle
            return f
        }
    }

    override fun initView() {

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

        activity!!.nav_view.visibility = View.GONE
        tv_title.text = "修改地址"

        arguments?.let {
            val address = it.getParcelable<Address>("address")
            mAddress = address
            et_name.setText(address!!.consignee!!)
            et_mobile.setText(address.mobile!!)
            tv_adress.text = "${address!!.provinceName} ${address!!.cityName} ${address!!.districtName}"
            et_mxadress.setText(address.address)

            cb_defult_addr.isChecked = address.isDefault == 1

            addressId = address.id!!
        }

        lin_address.setOnClickListener { view ->
            mViewModel.getCityPlaces()
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel.apply {
            val loadingPopup = XPopup.Builder(context).asLoading()
            cityPlacesState.observe(this@UpdateAddressFragment, Observer {

                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let { places ->

                    XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {

                        override fun onDismiss() {
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

                    }).asCustom(CustomAddPickerBottomPopup(context!!, places)).show()

                    loadingPopup.dismiss()
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                    loadingPopup.dismiss()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@UpdateAddressFragment.context, LoginActivity::class.java))
                    }
                }
            })

            addressState.observe(this@UpdateAddressFragment, Observer {

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
                        startActivity(Intent(this@UpdateAddressFragment.context, LoginActivity::class.java))
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
            Log.d("caicaicai", "1:${topAddressObj},2:${midAddressObj},3:${lastAddressObj}")
            mViewModel.updateAddress(
                addressId,
                et_name.text.toString(), et_mobile.text.toString().replace(" ", ""), et_mxadress.text.toString(),
                topAddressObj!!, midAddressObj!!, lastAddressObj!!, if (cb_defult_addr.isChecked) {
                    1
                } else {
                    0
                }
            )
        }
    }
}
