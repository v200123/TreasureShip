package com.jzz.treasureship.ui.invite

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.BuildConfig
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.InvitedAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.DataXXXX
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.changeImage
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mine_invite.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class InviteFragment : BaseVMFragment<InviteViewModel>() {
    private val mInvitedDetailsFragment by lazy { InviteDetailsFragment.newInstance() }
    private val mAdapter by lazy { InvitedAdapter() }
    private var allInvitedList = ArrayList<DataXXXX>()
    private var  showRedEnvelopeOpen:BasePopupView? = null
    companion object {
        const val isNeedBackToMain = "isNeedBackToMain"
        fun newInstance(isNeed:Boolean = false): InviteFragment {
            return InviteFragment().apply {
                val bundle = Bundle()
                bundle.putBoolean(isNeedBackToMain,isNeed)
                arguments = bundle
            }
        }
    }

    override fun getLayoutResId() = R.layout.fragment_mine_invite

    override fun initVM(): InviteViewModel = getViewModel()

    override fun onResume() {
        super.onResume()
        activity!!.nav_view.visibility = View.GONE
    }

    override fun initView() {
        val boolean = arguments?.getBoolean(isNeedBackToMain)?:false
        activity!!.nav_view.visibility = View.GONE
        iv_inviteBack.setOnClickListener {
            if(boolean)
                mContext.start<MainActivity> {  }
                else
            activity!!.supportFragmentManager.popBackStack()
        }

        tv_share2WxFriends.setOnClickListener {
            if (!App.iwxapi.isWXAppInstalled) {
                ToastUtils.showShort("未安装微信客户端，请先安装微信！")
                return@setOnClickListener
            }

            val userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
            val userObj = GsonUtils.fromJson(userJson, User::class.java)

            val webpage = WXWebpageObject()
            // http://119.3.125.1:8090/downLoad/index?type=1&yuserId=429&yType=1
            if(BuildConfig.DEBUG)
            {
                webpage.webpageUrl = "http://119.3.125.1:8090/downLoad/index?type=1&yuserId=${userObj.id}&yType=1"
            }
            else {
                webpage.webpageUrl = "http://bj.jzzchina.com/downLoad/index?type=1&yuserId=${userObj.id}&yType=1"
            }
            val msg = WXMediaMessage(webpage)
            msg.title = "好友邀请"
            msg.description = "宝舰医疗全新的购物体验"
            val thumbBmp = BitmapFactory.decodeResource(context!!.resources, R.mipmap.ic_launcher)
            msg.thumbData = thumbBmp.changeImage()


            //构造一个Req
            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("webpage")
            req.message = msg

            req.userOpenId = userObj.wxOpenid
            req.scene = SendMessageToWX.Req.WXSceneSession

            val isShare = App.iwxapi.sendReq(req)

            if (isShare) {
                //ToastUtils.showLong("分享成功！")
                mViewModel.getInvitedList()
            } else {
                //ToastUtils.showLong("分享失败！")
            }
        }

        tv_share2WxTimeLine.setOnClickListener {
            if (!App.iwxapi.isWXAppInstalled) {
                ToastUtils.showShort("未安装微信客户端，请先安装微信！")
                return@setOnClickListener
            }

            val userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
            val userObj = GsonUtils.fromJson(userJson, User::class.java)

            val webpage = WXWebpageObject()
            if(BuildConfig.DEBUG)
            {
                webpage.webpageUrl = "http://119.3.125.1:8090/downLoad/index?type=1&yuserId=${userObj.id}&yType=1"
            }
            else {
                webpage.webpageUrl = "http://bj.jzzchina.com/downLoad/index?type=1&yuserId=${userObj.id}&yType=1"
            }

            val msg = WXMediaMessage(webpage)
            msg.title = "App分享"
            msg.description = "医护小伙伴一起来分享"
            val thumbBmp = BitmapFactory.decodeResource(context!!.resources, R.mipmap.ic_launcher)
            msg.thumbData = thumbBmp.changeImage()


            //构造一个Req
            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("webpage")
            req.message = msg

            req.userOpenId = userObj.wxOpenid
            req.scene = SendMessageToWX.Req.WXSceneTimeline

            val isShare = App.iwxapi.sendReq(req)

            if (isShare) {
                //ToastUtils.showLong("分享成功！")
                mViewModel.getInvitedList()
            } else {
                //ToastUtils.showLong("分享失败！")
            }
        }

        layout_go2SeeMore.setOnClickListener {

            activity!!.supportFragmentManager.beginTransaction().addToBackStack(InviteFragment.javaClass.name)
                .hide(
                    this
                ).add(
                    R.id.frame_content,
                    mInvitedDetailsFragment,
                    mInvitedDetailsFragment.javaClass.name
                ).commit()
        }

        rcv_inviteds.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            adapter = mAdapter
        }
    }

    override fun initData() {
        mViewModel.getInvitedList()
        mViewModel.getCount()
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).asLoading()
            invitedsState.observe(this@InviteFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    allInvitedList = it.data as ArrayList<DataXXXX>
                    val list = ArrayList<DataXXXX>(3)
                    if (it.data.size > 3) {
                        for (i in 0..3) {
                            list.add(it.data[i])
                        }
                        mAdapter.setNewData(list)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        mAdapter.setNewData(it.data)
                        mAdapter.notifyDataSetChanged()
                    }
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@InviteFragment.context, LoginActivity::class.java))
                    }
                }
            })
            mCountData.observe(this@InviteFragment) { count ->
                if (count == 0) {
                    iv_small_red.visibility = View.INVISIBLE
                } else {
                    iv_small_red.visibility = View.VISIBLE
                    iv_small_red.setOnClickListener {
                        App.dialogHelp.showRedEnvelopeClose(count) {
                            mViewModel.getMoney()
                        }
                    }
                }
            }
            redEnvelopOpen.observe(this@InviteFragment) {
                if(showRedEnvelopeOpen !=null) {
                    showRedEnvelopeOpen!!.dismiss()
                }
                showRedEnvelopeOpen= App.dialogHelp.showRedEnvelopeOpen(
                    it.mInviteRewardCount,
                    it.mInviteRewardAmount, {
                        mViewModel.getCount()
                    },{
                        it.dismiss()
                    }
                ) {
                    mViewModel.getMoney()
                }
            }
        }


    }



    override fun initListener() {
    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
}
