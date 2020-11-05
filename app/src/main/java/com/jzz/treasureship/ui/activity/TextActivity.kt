package com.jzz.treasureship.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.orderdetail.ApplyRefundFragment

/**
 *@PackageName: com.jzz.treasureship.ui.activity
 *@Auth： 29579
 **/
class TextActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportFragmentManager.commit {
//            add(R.id.frame_content, ApplyRefundFragment())
//        }
    }

}