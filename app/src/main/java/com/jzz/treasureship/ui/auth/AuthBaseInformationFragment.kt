package com.jzz.treasureship.ui.auth

import android.R.anim
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.GsonUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.ui.auth.viewmodel.AuthBaseInfoViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomAddPickerBottomPopup
import com.lc.mybaselibrary.out
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.fragment_base_information.*


/**
 *@date: 2020/9/11
 *@describe: 医护认证的基本页面，包含了个人信息，地址的选择
 *@Auth: 29579
 **/
class AuthBaseInformationFragment : BaseVMFragment<AuthBaseInfoViewModel>(false) {


    companion object {
        const val result = 500
        const val EXTRA_POSITION = "com.position"
        const val EXTRA_NAME = "com.name"
    }

    private val activityViewModel by activityViewModels<CommonDataViewModel>()
    private var allPlace by PreferenceUtils(PreferenceUtils.ALL_PLACES, "")
    var topAddressObj: CityPlace? = null
    var midAddressObj: CityPlace? = null
    var lastAddressObj: CityPlace? = null

    override fun getLayoutResId(): Int = R.layout.fragment_base_information

    override fun initVM(): AuthBaseInfoViewModel = AuthBaseInfoViewModel()

    override fun initView() {
        xll_raduis.apply {
            radius = 24
            showDividers = SHOW_DIVIDER_MIDDLE
        }

        et_base_name.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                activityViewModel.mConfirmBody.mRealName = s.toString()
            }
        })

        et_base_carName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                activityViewModel.mConfirmBody.mOrganizationName = s.toString()

            }
        })


        fl_baseinfo_department.setOnClickListener {
            startActivityForResult(Intent(mContext, DepartmentSreachActivity::class.java), 500)
        }

        fl_baseinfo_address.setOnClickListener {
            if (allPlace.isBlank()) {
                mViewModel.getCityPlaces()
            } else {
                val places = GsonUtils.fromJson(allPlace, CityPlaces::class.java)

                XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {

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

                        et_base_address.setText("${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}")
                        activityViewModel.mConfirmBody.apply {
                            this.mAreaProvince = topAddressObj!!.id
                            this.mAreaCity = midAddressObj!!.id
                            this.mAreaDistrict = lastAddressObj!!.id
                        }
                    }

                }).asCustom(CustomAddPickerBottomPopup(mContext, places)).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 500) {
            if (requestCode == result && data != null) {
                tv_department_name.setText(data.getStringExtra(EXTRA_NAME)!!)
                activityViewModel.mConfirmBody.mDepartmentId = data.getIntExtra(EXTRA_POSITION,1)
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.CityPlacesLiveData.observe(this, {
            allPlace = GsonUtils.toJson(it)
            XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {

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
                    et_base_address.setText("${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}")
                    activityViewModel.mConfirmBody.apply {
                        this.mAreaProvince = topAddressObj!!.id
                        this.mAreaCity = midAddressObj!!.id
                        this.mAreaDistrict = lastAddressObj!!.id
                    }
                }

            }).asCustom(CustomAddPickerBottomPopup(mContext, it)).show()
        })

    }

    override fun initListener() {
    }

}
