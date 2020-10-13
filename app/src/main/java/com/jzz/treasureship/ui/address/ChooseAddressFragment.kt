package com.jzz.treasureship.ui.address

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_set_address.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class ChooseAddressFragment : BaseVMFragment<AddressViewModel>() {
    private val addressAdapter: AddressAdapter by lazy { AddressAdapter() }
    private var mDeletedItem: Address? = null
    private var addressList: ArrayList<Address> = ArrayList()
    private var pageNum = 1
    private var selectedAddress by PreferenceUtils(PreferenceUtils.SELECTED_ADDRESS, "")

    companion object {
        fun newInstance(): ChooseAddressFragment {
            return ChooseAddressFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_set_address

    override fun initVM(): AddressViewModel = getViewModel()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        tv_title.text = "设置地址"
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        rlback.setOnClickListener {
            if (addressList.size == 0) {
                selectedAddress = ""
            }
            activity!!.supportFragmentManager.popBackStack()
        }

        layout_addAddress.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(ChooseAddressFragment.javaClass.name)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, AddAddressFragment.newInstance(), AddAddressFragment.javaClass.name)
                .commit()
        }

        rv_addressList.run {
            layoutManager = LinearLayoutManager(activity).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            this.adapter = addressAdapter
        }

        addressAdapter.run {
            setOnItemChildClickListener() { adapter, view, position ->
                when (view.id) {
                    R.id.tv_addr_edit -> {

                        requireActivity().supportFragmentManager.beginTransaction().addToBackStack(ChooseAddressFragment.javaClass.name).hide(this@ChooseAddressFragment)
                            .add(R.id.frame_content,
                                UpdateAddressFragment.newInstance(adapter.data[position] as Address),
                                UpdateAddressFragment.javaClass.name
                            ).commit()
                    }
                    R.id.tv_addr_delete -> {
                        val popup = XPopup.Builder(view.context)
                        popup.asConfirm("删除地址", "确认删除此地址吗？", OnConfirmListener {
                            //删除收货地址
                            mDeletedItem = addressList[position]

                            mViewModel.delAddress(addressList[position].id!!)
                        }).show()
                    }
                    R.id.layout_item -> {
                        selectedAddress = GsonUtils.toJson(addressList[position])
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    R.id.cb_setDefault -> {
                        if ((view as CheckBox).isChecked) {
                            mViewModel.setAddressDefault((adapter.data[position] as Address).id!!)
                        } else {

                        }
                    }
                }
            }
        }

        srl_address.setOnRefreshListener {
            pageNum = 1
            mViewModel.getAddressPageList(1)
        }

        srl_address.setEnableLoadMore(true)
        srl_address.setOnLoadMoreListener {
            mViewModel.getAddressPageList(++pageNum)
        }
    }



    override fun initData() {
        mViewModel.getAddressPageList()
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(this@ChooseAddressFragment.context).asLoading("收货地址获取中")
            addressPageListState.observe(this@ChooseAddressFragment, Observer {

                if (it.showLoading) {
                    xPopup.show()
                }

                it.showSuccess?.let { list ->
                    xPopup.dismiss()
                    rv_addressList.run {

                        if (pageNum > 1) {
                            if (list.data.isNotEmpty()) {
                                addressList.addAll(list.data as ArrayList<Address>)
                                addressAdapter.run {
                                    setNewData(addressList)
                                    notifyDataSetChanged()
                                }
                            } else {
                                //第二页没内容，不增加
                                --pageNum
                            }
                            srl_address.finishLoadMore(0)
                        } else {
                            addressList.clear()
                            addressList.addAll(list.data as ArrayList<Address>)
                            addressAdapter.run {
                                setNewData(addressList)

                                notifyDataSetChanged()
                            }
                            srl_address.finishRefresh(0)
                        }
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    xPopup.dismiss()
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@ChooseAddressFragment.context, LoginActivity::class.java))
                    }
                }
            })

            addressState.observe(this@ChooseAddressFragment, Observer {

                it.showSuccess?.let { address ->
                    if (address.equals("null") or address.isBlank()) {
                        mViewModel.getAddressPageList()
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    xPopup.dismiss()
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@ChooseAddressFragment.context, LoginActivity::class.java))
                    }
                }
            })

            delAddressState.observe(this@ChooseAddressFragment, Observer {
                it.showSuccess?.let { it ->
                    if (it.equals("null") or it.isBlank()) {
                        mViewModel.getAddressPageList()
                        if (GsonUtils.toJson(mDeletedItem) == selectedAddress) {
                            if (addressList.size > 0) {
                                selectedAddress = GsonUtils.toJson(addressList[0])
                            } else {
                                selectedAddress = ""
                            }
                        }
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    xPopup.dismiss()
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@ChooseAddressFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.getAddressPageList()
        }
    }
    override fun onBackPressed(): Boolean {
        return true
    }
}
