package com.jzz.treasureship.ui.orderdetail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.orderdetail.viewModel.OrderDetailViewModel
import com.jzz.treasureship.utils.BackHandlerHelper

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 **/
class MainOrderDetailActivity : AppCompatActivity(R.layout.activity_main) {

    private val mOrderDetailViewModel by viewModels<OrderDetailViewModel>()

    companion object {
        const val EXTRA_ORDER = "com.orderdetail_id"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOrderDetailViewModel.id = intent.getIntExtra(EXTRA_ORDER,644)
        supportFragmentManager.commit {
            add(R.id.frame_content, MainOrderDetailFragment(),"MainOrderDetailFragment")
        }
    }

    override fun onBackPressed() {

        if(supportFragmentManager.backStackEntryCount == 0)
        {
            finish()
        }

        if (BackHandlerHelper.handleBackPress(this)) {
            supportFragmentManager.popBackStack()
            return
        }else{
            super.onBackPressed()
        }
    }
}