package com.jzz.treasureship.ui.search

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
R
adapter.HotSearchAdapter
base.BaseVMFragment
ui.login.LoginActivity
view.RecyclerViewSpacesItemDecoration
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_search.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class SearchFragment : BaseVMFragment<SearchViewModel>() {

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun getLayoutResId() = R.layout.home_search

    override fun initVM(): SearchViewModel = getViewModel()

    private val hotSearchAdapter by lazy { HotSearchAdapter() }

    private var type: Int = 0

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE

        arguments?.let {
            type = it.getInt("type")
        }

        tv_cancal.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        etSearch.setOnTouchListener(OnTouchListener { _, event ->
            // getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
            val drawable: Drawable = etSearch.compoundDrawables[2] ?: return@OnTouchListener false
            //如果右边没有图片，不再处理
            if (event.action != MotionEvent.ACTION_UP) return@OnTouchListener false
            if (event.x > (etSearch.width
                        - etSearch.paddingRight
                        - drawable.intrinsicWidth)
            ) {
                etSearch.setText("")
            }
            false
        })

        etSearch.setOnEditorActionListener(OnEditorActionListener { arg0, arg1, arg2 ->
            if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg2 != null && arg2.keyCode === KeyEvent.KEYCODE_ENTER && arg2.action === KeyEvent.ACTION_DOWN) {
                // 先隐藏键盘
                (etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    etSearch.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                activity!!.supportFragmentManager.beginTransaction()
                    .addToBackStack(SearchFragment.javaClass.name)
                    .hide(this)//隐藏当前Fragment
                    .add(
                        R.id.frame_content,
                        SearchResultsFragment.newInstance(etSearch.text.toString(), 2, type),
                        SearchResultsFragment.javaClass.name
                    ).commit()
                return@OnEditorActionListener true
            }
            false
        })

        initRecycleView()
    }

    private fun initRecycleView() {
        mrv_search_hot.run {
            val gridLayoutManager = GridLayoutManager(context, 2)
            gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
            gridLayoutManager.spanSizeLookup
            layoutManager = gridLayoutManager


            val stringIntegerHashMap: HashMap<String, Int> = HashMap()
            stringIntegerHashMap[RecyclerViewSpacesItemDecoration.TOP_DECORATION] = 15 //top间距


            stringIntegerHashMap[RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION] = 15 //底部间距


            stringIntegerHashMap[RecyclerViewSpacesItemDecoration.LEFT_DECORATION] = 33 //左间距


            stringIntegerHashMap[RecyclerViewSpacesItemDecoration.RIGHT_DECORATION] = 60 //右间距

            addItemDecoration(RecyclerViewSpacesItemDecoration(stringIntegerHashMap))
        }
    }

    override fun initData() {
        mViewModel.run {
            this.getHotSearchList()
            this.getIllnessList()
            this.getBrandList()
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            val loadingPopup = XPopup.Builder(context).asLoading()
            //品牌
            brandUiState.observe(this@SearchFragment, Observer {
                if (it.showLoading) {
                    loadingPopup.show()
                }
                it.showSuccess?.let { brands ->
                    loadingPopup.dismiss()
                    for (element in brands) {
                        val tv = LayoutInflater.from(context).inflate(
                            R.layout.layout_search_flexbox_item, mfl_search_brand, false
                        ) as TextView
                        tv.text = element.brandName
                        val str = tv.text.toString()

                        tv.setOnClickListener {
                            activity!!.supportFragmentManager.beginTransaction()
                                .addToBackStack(SearchFragment.javaClass.name)
                                .hide(this@SearchFragment)//隐藏当前Fragment
                                .add(
                                    R.id.frame_content,
                                    SearchResultsFragment.newInstance(element.id, 0, type),
                                    SearchResultsFragment.javaClass.name
                                ).commit()
                        }
                        //添加到父View
                        mfl_search_brand.addView(tv)
                    }
                }
                it.showError?.let { message ->
                    loadingPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    loadingPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            //病症
            illnessUiState.observe(this@SearchFragment, Observer {
                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let { illnesses ->
                    loadingPopup.dismiss()
                    for (element in illnesses) {
                        val tv = LayoutInflater.from(context).inflate(
                            R.layout.layout_search_flexbox_item, mfl_search_disease, false
                        ) as TextView
                        tv.text = element.name
                        val str = tv.text.toString()
                        //点击事件
                        tv.setOnClickListener {
                            activity!!.supportFragmentManager.beginTransaction()
                                .addToBackStack(SearchFragment.javaClass.name)
                                .hide(this@SearchFragment)//隐藏当前Fragment
                                .add(
                                    R.id.frame_content,
                                    SearchResultsFragment.newInstance(tv.text.toString(), 1, type),
                                    SearchResultsFragment.javaClass.name
                                ).commit()
                        }
                        //添加到父View
                        mfl_search_disease.addView(tv)
                    }
                }
                it.showError?.let { message ->
                    loadingPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    loadingPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            //热搜列表
            hotSearchUiState.observe(this@SearchFragment, Observer {
                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let { hotSearches ->
                    loadingPopup.dismiss()
                    hotSearchAdapter.run {
                        setNewData(hotSearches)
                        setOnItemChildClickListener() { adapter, view, position ->
                            when (view.id) {
                                R.id.layout_hot_search -> {
                                    activity!!.supportFragmentManager.beginTransaction()
                                        .addToBackStack(SearchFragment.javaClass.name)
                                        .hide(this@SearchFragment)//隐藏当前Fragment
                                        .add(
                                            R.id.frame_content,
                                            SearchResultsFragment.newInstance(hotSearchAdapter.getItem(position)!!.videoName, 2, type),
                                            SearchResultsFragment.javaClass.name
                                        ).commit()
                                }
                            }
                        }
                    }

                    mrv_search_hot.adapter = hotSearchAdapter
                }
                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    loadingPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            searchState.observe(this@SearchFragment, Observer {
                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let { searchResults ->
                    loadingPopup.dismiss()

                }
                it.showError?.let { message ->
                    loadingPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
                it.needLogin?.let {
                    loadingPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })
        }
    }



    override fun initListener() {

    }
}