package com.jzz.treasureship.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import cn.jpush.android.api.JPushInterface
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.GuidePageAdapter
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_welcome_guide.*


class WelcomeGuideActivity : AppCompatActivity(), OnPageChangeListener {

    private var FIRST_BOOT by PreferenceUtils(PreferenceUtils.FIRST_BOOT, true)

    private var vp: ViewPager2? = null
    //图片资源的数组
    private lateinit var imageIdArray: IntArray
    //图片资源的集合
    private var viewList: List<View>? = null
    //放置圆点
    private var vg: ViewGroup? = null
    private var iv_point: ImageView? = null
    private lateinit var ivPointArray: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_welcome_guide)
        //打开通知栏的权限
        if (JPushInterface.isNotificationEnabled(this) == 0) {
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("通知权限未打开，是否前去打开？")
                .setPositiveButton(
                    "是"
                ) { d: DialogInterface?, w: Int ->
                    JPushInterface.goToAppNotificationSettings(this)
                }
                .setNegativeButton("否", null)
                .show()
        }
        //申请定位、存储权限
        JPushInterface.requestPermission(this)
        tv_guideStart.setOnClickListener {
            FIRST_BOOT = false
            val intent = Intent(this@WelcomeGuideActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //加载ViewPager
        initViewPager()
        //加载底部圆点
        initPoint()
        vg!!.getChildAt(0).setBackgroundResource(R.drawable.full_holo)
    }

    /**
     * 加载底部圆点
     */
    private fun initPoint() { //这里实例化LinearLayout
        vg = findViewById<View>(R.id.guide_ll_point) as ViewGroup
        //根据viewpager的item数量实例化数组
        ivPointArray = arrayOfNulls<ImageView>(viewList!!.size)
        //循环新建底部圆点imageview，将生成的imageview保存到数组中
        val size = viewList!!.size
        for (i in 0 until size) {
            iv_point = ImageView(this)
            iv_point!!.layoutParams = ViewGroup.LayoutParams(50, 50)
            iv_point!!.setPadding(60, 60, 60, 60) //left,top,right,bottom
            ivPointArray[i] = iv_point
            //第一个页面需要设置为选中状态，这里采用两张不同的图片
            if (i == 0) {
                iv_point!!.setImageResource(R.drawable.full_holo)
            } else {
                iv_point!!.setImageResource(R.drawable.empty_holo)
            }
            //将数组中的imageView加到viewGroup
            vg!!.addView(ivPointArray[i])
        }
    }

    /**
     * 加载图片ViewPager
     */
    private fun initViewPager() {
        //实例化图片资源
        imageIdArray = intArrayOf(R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3)
        viewList = ArrayList()
        //获取一个Layout参数，设置为全屏
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        //循环创建View并加入到集合中
        val len = imageIdArray.size
        for (i in 0 until len) { //new ImageView并设置全屏和图片资源
            val imageView = ImageView(this)
            imageView.layoutParams = params
            imageView.setBackgroundResource(imageIdArray[i])
            //将ImageView加入到集合中
            (viewList as ArrayList<View>).add(imageView)
        }
        //view集合初始化好后，设置Adapter
        guide_vp2.adapter = GuidePageAdapter(viewList)
        //设置滑动监听
        guide_vp2.setOnPageChangeListener(this)
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
    }

    /**
     * 滑动后的监听
     * @param position
     */
    override fun onPageSelected(position: Int) { //循环设置当前页的标记图标
        val length = imageIdArray.size
        for (i in 0 until length) {
            if (i == position) {
                vg!!.getChildAt(position).setBackgroundResource(R.drawable.full_holo)
            } else {
                vg!!.getChildAt(i).setBackgroundResource(R.drawable.empty_holo)
            }
        }
        //判断是否是最后一页
        if (position == imageIdArray.size - 1) {
            tv_guideStart.visibility = View.VISIBLE
        } else {
            tv_guideStart.visibility = View.GONE
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
}
