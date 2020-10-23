package com.jzz.treasureship.ui.addressbook

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.ContacterAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.DataX
import com.jzz.treasureship.model.bean.DataXX
import com.jzz.treasureship.ui.customer.CustomerDetailFragment
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomContacterDrawerPopupView
import com.jzz.treasureship.view.CustomNoticeDialog
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_address_book.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class AddressBookFragment : BaseVMFragment<AddressbookViewModel>() {
    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)

    private val mAdapter by lazy { ContacterAdapter() }
    private var currentPosition = -1
    private var mContacters = ArrayList<DataX>()
    private var pageNum = 1
    private var mGoodsList: ArrayList<DataXX> = ArrayList()
    var contacterGoods by PreferenceUtils(PreferenceUtils.CONTACTER_GOODS, -1)

    //购买时间排序顺序：-1默认，0，降序，1升序
    private var buyTimeSort = -1

    //数量排序顺序：-1默认，0，降序，1升序
    private var countSort = -1

    private var mName = ""

    //-1刷新，1加载更多，0默认
    private var loadOrReflesh = 0

    companion object {
        fun newInstance(): AddressBookFragment {
            return AddressBookFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_address_book

    override fun initVM(): AddressbookViewModel = getViewModel()

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
//        activity!!.nav_view.visibility = View.VISIBLE
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))

        srl_contacter.setOnRefreshListener {
            pageNum = 1
            loadOrReflesh = -1
            mViewModel.getMemberList(countSort, buyTimeSort, -1, mName, pageNum)

        }
        srl_contacter.setEnableLoadMore(true)
        srl_contacter.setOnLoadMoreListener {
            loadOrReflesh = 1
            mViewModel.getMemberList(countSort, buyTimeSort, -1, mName, ++pageNum)

        }

        et_search.run {
            this.setOnTouchListener(View.OnTouchListener { _, event ->
                // getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                val drawable: Drawable = et_search.compoundDrawables[2] ?: return@OnTouchListener false
                //如果右边没有图片，不再处理
                //如果不是按下事件，不再处理
                if (event.action != MotionEvent.ACTION_UP) return@OnTouchListener false
                if (event.x > (et_search.width
                            - et_search.paddingRight
                            - drawable.intrinsicWidth)
                ) {
                    et_search.setText("")
                    mName = ""
                }
                false
            })

            this.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    val tmpList = ArrayList<DataX>()
//                    for (ele in mContacters) {
//                        if (et_search.text.toString() == ele.nikeName) {
//                            tmpList.add(ele)
//                        }
//                    }
//                    mAdapter.setNewData(tmpList)
//                    mAdapter.notifyDataSetChanged()
                    mName = et_search.text.toString()
                    mViewModel.getMemberList(-1, -1, -1, mName, 1)
                }
                false
            }
        }

        tv_cancal.setOnClickListener {
            et_search.setText("")
            mName = ""
            buyTimeSort = -1
            countSort = -1
            Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_default))
                .into(view!!.rootView.findViewById(R.id.iv_buyTimeSort))
            Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_default))
                .into(view!!.rootView.findViewById(R.id.iv_countSort))

            pageNum = -1
            mViewModel.getMemberList()
        }

        tv_chooseProduct.setOnClickListener {
            buyTimeSort = -1
            countSort = -1

            Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_default))
                .into(view!!.rootView.findViewById(R.id.iv_buyTimeSort))
            Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_default))
                .into(view!!.rootView.findViewById(R.id.iv_countSort))

            XPopup.Builder(it.context).setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView) {
                    super.onDismiss(popupView)
                    if (contacterGoods != -1) {
                        mGoodsList[contacterGoods].let { dataXX ->
                            mViewModel.getMemberList(-1, -1, -1, dataXX.name, 1)
                        }
                    } else {
                        pageNum = -1
                        mViewModel.getMemberList()
                    }
                }
            }).asCustom(CustomContacterDrawerPopupView(it.context, mGoodsList)).show()
        }

        ll_buyTime.setOnClickListener {
            when (buyTimeSort) {
                -1 -> {
                    //默认状态点击后，变为升序1，图标变为升序图标
                    buyTimeSort = 0
//                    countSort = -1
                    Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_up))
                        .into(it.findViewById(R.id.iv_buyTimeSort))
//                    mAdapter.data.sortBy {
//                        it.lastedBuyTime
//                    }
//                    mAdapter.notifyDataSetChanged()
                    mViewModel.getMemberList(-1, 1, -1, mName, 1)
                }
                0 -> {
                    //升序状态点击后，变为降序2，图标变为降序图标
                    buyTimeSort = 1
//                    countSort = -1
                    Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_down))
                        .into(it.findViewById(R.id.iv_buyTimeSort))

//                    mAdapter.data.sortByDescending {
//                        it.lastedBuyTime
//                    }
//                    mAdapter.notifyDataSetChanged()
                    mViewModel.getMemberList(-1, 2, -1, mName, 1)
                }
                1 -> {
                    //降序状态点击后，变为升序状态1，图标变为升序图标
                    buyTimeSort = 0
//                    countSort = -1
                    Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_up))
                        .into(it.findViewById(R.id.iv_buyTimeSort))
//                    mAdapter.data.sortBy {
//                        it.lastedBuyTime
//                    }
//                    mAdapter.notifyDataSetChanged()
                    mViewModel.getMemberList(-1, 1, -1, mName, 1)
                }
            }
        }

        ll_count.setOnClickListener {
            when (countSort) {
                -1 -> {
                    countSort = 0
//                    buyTimeSort = -1
                    Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_up))
                        .into(it.findViewById(R.id.iv_countSort))
//                    mAdapter.data.sortBy {
//                        it.buyNum
//                    }
//                    mAdapter.notifyDataSetChanged()
                    mViewModel.getMemberList(1, -1, -1, mName, 1)
                }
                0 -> {
                    countSort = 1
//                    buyTimeSort = -1
                    Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_down))
                        .into(it.findViewById(R.id.iv_countSort))
//                    mAdapter.data.sortByDescending {
//                        it.buyNum
//                    }
//                    mAdapter.notifyDataSetChanged()
                    mViewModel.getMemberList(2, -1, -1, mName, 1)
                }
                1 -> {
                    countSort = 0
//                    buyTimeSort = -1
                    Glide.with(view!!.context).load(view!!.context.resources.getDrawable(R.drawable.icon_sort_up))
                        .into(it.findViewById(R.id.iv_countSort))
//                    mAdapter.data.sortBy {
//                        it.buyNum
//                    }
//                    mAdapter.notifyDataSetChanged()
                    mViewModel.getMemberList(1, -1, -1, mName, 1)
                }
            }
        }
        initRecycleView()
    }

    private fun initRecycleView() {
        rcv_contacter.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            mAdapter.run {
                setOnItemChildClickListener() { adapter, view, position ->
                    when (view.id) {
                        R.id.iv_clock -> {
                            currentPosition = position
                            XPopup.Builder(view.context).setPopupCallback(object : SimpleCallback() {
                                override fun onDismiss(popupView: BasePopupView) {
                                    super.onDismiss(popupView)
                                    val cancleNotice by PreferenceUtils(PreferenceUtils.CANCLE_NOTICE, false)
                                    if (cancleNotice) {
                                        mViewModel.setNotice(mAdapter.getItem(position)!!.id!!, 2, "")
                                    }
                                }
                            }).asCustom(CustomNoticeDialog(view.context)).show()
                        }

                        R.id.layout_middle_info,
                        R.id.iv_userIco,
                        R.id.layout_times -> {
//                            activity!!.supportFragmentManager.beginTransaction()
//                                .addToBackStack(AddressBookFragment.javaClass.name)
//                                .hide(this@AddressBookFragment)//隐藏当前Fragment
//                                .add(R.id.frame_content, CustomerDetailFragment.newInstance(mAdapter.getItem(position)!!), CustomerDetailFragment.javaClass.name)
//                                .commit()
                        }
                    }
                }

            }

            adapter = mAdapter
        }
    }


    override fun initData() {
        if (isLogin) {
            mViewModel.getMemberList()
            mViewModel.getGoodsList()
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            membersState.observe(this@AddressBookFragment, Observer {

                it.showSuccess?.let {
                    when (loadOrReflesh) {
                        0, -1 -> {
                            mContacters.clear()
                            for (ele in it.data!!) {
                                mContacters.add(ele!!)
                            }
                            mAdapter.setNewData(mContacters)
                            mAdapter.notifyDataSetChanged()
                            srl_contacter.finishRefresh()
                        }
                        1 -> {
                            for (ele in it.data!!) {
                                mContacters.add(ele!!)
                            }
                            mAdapter.setNewData(mContacters)
                            mAdapter.notifyDataSetChanged()
                            srl_contacter.finishLoadMore()
                        }
                        else -> {
                            Log.d("caicos", "${loadOrReflesh}")
                        }
                    }

                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                    ToastUtils.showShort("未登录，请登录后再操作！")
                    startActivity(Intent(this@AddressBookFragment.context, LoginActivity::class.java))
                }
                }
            })

            goodsState.observe(this@AddressBookFragment, Observer {
                it.showSuccess?.let {
                    for (ele in it.data!!) {
                        mGoodsList.add(ele!!)
                    }
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@AddressBookFragment.context, LoginActivity::class.java))
                    }
                }
            })

            noticeState.observe(this@AddressBookFragment, Observer {
                it.showError?.let {
                    ToastUtils.showShort(if (it.isBlank()) "网络异常" else it)
                }
                it.showSuccess.let {
                    ToastUtils.showShort("成功！")
                    Glide.with(context!!)
                        .load(context!!.resources.getDrawable(R.drawable.icon_clock_normal))
                        .into(rcv_contacter[currentPosition].findViewById(R.id.iv_clock))

                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@AddressBookFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
//            activity!!.nav_view.visibility = View.VISIBLE
//            activity!!.nav_view.menu[2].isChecked = true
            StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        }
    }
}