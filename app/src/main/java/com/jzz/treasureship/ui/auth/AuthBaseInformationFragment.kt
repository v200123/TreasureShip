package com.jzz.treasureship.ui.auth

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.ui.auth.viewmodel.AuthBaseInfoViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomAddPickerBottomPopup
import com.lc.mybaselibrary.assertRead.Companion.getFromAssets
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
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

        et_base_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                activityViewModel.mConfirmBody.mRealName = s.toString()
            }
        })

        et_base_carName.addTextChangedListener(object : TextWatcher {
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
            try {
                val fromAssets = getFromAssets(mContext, "city.json")
                val city = GsonUtils.fromJson(fromAssets, CityPlaces::class.java)
                showAddressSelect(city)
            } catch (e: Exception) {
                ToastUtils.showShort("文件下载错误,开始网络请求")
                mViewModel.getCityPlaces()
            }
//

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 500) {
            if (requestCode == result && data != null) {
                tv_department_name.setText(data.getStringExtra(EXTRA_NAME)!!)
                activityViewModel.mConfirmBody.mDepartmentId = data.getIntExtra(EXTRA_POSITION, 1)
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.CityPlacesLiveData.observe(this, {
            allPlace = GsonUtils.toJson(it)
            XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {

                override fun onDismiss(popupView: BasePopupView) {
                    super.onDismiss(popupView)
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
                    et_base_address.setText(
                        "${topAddressObj?.areaName ?: ""} ${midAddressObj?.areaName ?: ""} " +
                                "${lastAddressObj?.areaName ?: ""}"
                    )
                    activityViewModel.mConfirmBody.apply {
                        this.mAreaProvince = topAddressObj?.id ?: 0
                        this.mAreaCity = midAddressObj?.id ?: 0
                        this.mAreaDistrict = lastAddressObj?.id ?: 0
                    }
                }

            }).asCustom(CustomAddPickerBottomPopup(mContext, it)).show()
        })
    }

    override fun initListener() {
    }


    fun showAddressSelect(places: CityPlaces) {
        XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {

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
                val isFinish: Boolean
//                        super.onDismiss(popupView)

                var lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                var topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                var midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                isFinish =
                    lastAddress.isNotBlank() && topAddress.isNotBlank() && midAddress.isNotBlank()

                if (isFinish) {
                    lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
                    topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
                    midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
                    activityViewModel.mConfirmBody.apply {
                        this.mAreaProvince = topAddressObj?.id ?: 0
                        this.mAreaCity = midAddressObj?.id ?: 0
                        this.mAreaDistrict = lastAddressObj?.id ?: 0
                    }
                    et_base_address.setText(
                        "${topAddressObj?.areaName ?: ""} ${midAddressObj?.areaName ?: ""} ${lastAddressObj?.areaName ?: ""}"
                    )
                    lastAddress = ""
                    topAddress = ""
                    midAddress = ""
                } else {
                    et_base_address.setHint("请选择所在区域")
                }


//                        super.onDismiss(popupView)
//                val lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
//                if (lastAddress.isNotBlank()) {
//                    lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
//                }
//
//                val topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
//                if (topAddress.isNotBlank()) {
//                    topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
//                }
//
//                val midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
//                if (midAddress.isNotBlank()) {
//                    midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
//                }

//                et_base_address.setText(
//                    "${topAddressObj?.areaName ?: ""} ${midAddressObj?.areaName ?: ""} " +
//                            "${lastAddressObj?.areaName ?: ""}"
//                )

            }

        }).asCustom(CustomAddPickerBottomPopup(mContext, places)).show()
    }

//    fun getFromAssets(fileName: String): String? {
//        val content: String? = null //结果字符串
//        try {
//            val inputReader = InputStreamReader(resources.assets.open(fileName!!))
//            val bufReader = BufferedReader(inputReader)
//            var line: String? = ""
//            val builder = StringBuilder()
//            while (bufReader.readLine().also { line = it } != null) {
//                builder.append(line)
//            }
//            inputReader.close()
//            bufReader.close()
//            return builder.toString()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        return content
//    }

}
