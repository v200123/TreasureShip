package com.jzz.treasureship.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.changeImage
import com.lxj.xpopup.core.BottomPopupView
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXVideoObject
import kotlinx.android.synthetic.main.dialog_home_share.view.*


class CustomShareGoosBottomPopup(
    context: Context
) :
    BottomPopupView(context) {

    internal enum class SHARE_TYPE {
        Type_WXSceneSession, Type_WXSceneTimeline
    }

    override fun getImplLayoutId() = R.layout.dialog_home_share

    override fun initPopupContent() {
        super.initPopupContent()

        //分享给微信好友
        layout_wechat_share.setOnClickListener {

        }

        //分享到朋友圈
        layout_share_life.setOnClickListener {

        }

        iv_cancle.setOnClickListener {
            dismiss()
        }
    }

    private fun shareGoods(type: SHARE_TYPE, url: String?, title: String?, description: String?, iconUrl: String?) {

        val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
        if (isLogin) {
            val video = WXVideoObject()
            video.videoUrl = url

            //用 WXVideoObject 对象初始化一个 WXMediaMessage 对象
            val msg = WXMediaMessage(video)
            msg.title = title
            msg.description = description

            val thumbBmp = BitmapFactory.decodeResource(this.context.resources, R.mipmap.ic_launcher)
            msg.thumbData = thumbBmp.changeImage()

            //构造一个Req
            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("video")
            req.message = msg
            val userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
            val userObj = GsonUtils.fromJson(userJson, User::class.java)
            req.userOpenId = userObj.wxOpenid

            //调用api接口，发送数据到微信
            when (type) {
                SHARE_TYPE.Type_WXSceneSession -> {
                    req.scene = WXSceneSession
                }
                SHARE_TYPE.Type_WXSceneTimeline -> {
                    req.scene = WXSceneTimeline
                }
            }
            App.iwxapi.sendReq(req)
        } else {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
}
