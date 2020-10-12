package com.jzz.treasureship.ui.auth

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.model.bean.DepartmentBean
import com.jzz.treasureship.ui.auth.adapter.SearchAdapter
import com.jzz.treasureship.ui.auth.viewmodel.DepartmentSreachViewModel
import kotlinx.android.synthetic.main.activity_department_search.*
import kotlinx.android.synthetic.main.include_title.*

/**
 *@date: 2020/9/14
 *@describe:
 *@Auth: 29579
 **/
class DepartmentSreachActivity : BaseVMActivity<DepartmentSreachViewModel>(false) {
    private lateinit var mDepartmentList: List<DepartmentBean.DepartmentType>
    private val mAdapter by lazy { SearchAdapter() }
    override fun initVM(): DepartmentSreachViewModel = DepartmentSreachViewModel()

    override fun getLayoutResId(): Int = R.layout.activity_department_search

    override fun initView() {
        rv_department_list.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(mContext)
        }
        tv_title.text = "选择科室"
        rlback.setOnClickListener { finish() }
        mAdapter.setOnItemClickListener(object : BaseQuickAdapter.OnItemClickListener {
            override fun onItemClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                setResult(500, Intent().apply {
                    putExtra(
                        AuthBaseInformationFragment.EXTRA_POSITION,
                        (adapter.data[position] as DepartmentBean.DepartmentType).mId
                    )
                    putExtra(
                        AuthBaseInformationFragment.EXTRA_NAME,
                        (adapter.data[position] as DepartmentBean.DepartmentType).mName
                    )
                })
                finish()
            }

        })

        et_department_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable) {
                if (s.isBlank()) {
                    mAdapter.setNewData(mDepartmentList)
                } else {
                    val filter = mDepartmentList
                        .filter { it.mName.contains(s.toString()) }
                    mAdapter.setNewData(filter)
                }
            }

        })

    }

    override fun initData() {
        mViewModel.getHospital()
    }

    override fun startObserve() {
        mViewModel.hospitalData.observe(this, {
            mDepartmentList = it.mList
            mAdapter.setNewData(mDepartmentList)
        })
    }


}
