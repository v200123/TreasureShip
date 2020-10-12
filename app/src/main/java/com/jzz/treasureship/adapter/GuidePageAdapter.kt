package com.jzz.treasureship.adapter

import android.view.View
import android.view.ViewGroup

import androidx.viewpager.widget.PagerAdapter


class GuidePageAdapter(viewList: List<View>?) : PagerAdapter() {
    private val viewList: List<View>? = viewList
    /**
     * 返回页面的个数
     * @return
     */
    override fun getCount(): Int {
        return viewList?.size ?: 0
    }

    /**
     * 判断对象是否生成界面
     * @param view
     * @param object
     * @return
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    /**
     * 初始化position位置的页面
     * @param container
     * @param position
     * @return
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(viewList!![position])
        return viewList[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(viewList!![position])
    }

}