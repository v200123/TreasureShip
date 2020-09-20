package com.jzz.treasureship.ui.auth.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jzz.treasureship.ui.auth.AuthBaseInformationFragment
import com.jzz.treasureship.ui.auth.AuthUpLoadFragment

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthInfoAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment
        {
            if (position == 0)
                return AuthBaseInformationFragment()
            else {
                return AuthUpLoadFragment()
            }
        }

}
