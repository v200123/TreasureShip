package com.jzz.treasureship.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.ui.withdraw.WithdrawFragment
import com.jzz.treasureship.utils.PreferenceUtils


import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler


class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private lateinit var api: IWXAPI
    private val localManager = LocalBroadcastManager.getInstance(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        api = App.iwxapi
        api.handleIntent(intent, this)

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }

    override fun onResp(baseResp: BaseResp) {
            //认证授权的响应
            if (baseResp is SendAuth.Resp) {
                //响应的类型是授权类型
                if (baseResp.getType() === ConstantsAPI.COMMAND_SENDAUTH) {
                    when (baseResp.errCode) {
                        BaseResp.ErrCode.ERR_OK -> {
                            val state = baseResp.state
                            val code = baseResp .code
                            if (!TextUtils.isEmpty(state)) {
                                //当state和此前设置的req.state相同是就证明请求成功。
                                if (state == "treasureship_wx_login") {
                                    //我们的目的是拿到code去进行下一步的操作
                                    var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                                    wxCode = code
                                    finish()
                                }
                                if (state == "treasureship_wx_bind") {
                                    localManager.sendBroadcast(Intent("WxCode").putExtra(
                                        WithdrawFragment.withDraw,code))
                                    var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE_BIND, "")
                                    wxCode = code

                                }
                            }
                        }
                        BaseResp.ErrCode.ERR_USER_CANCEL -> {
                            ToastUtils.showShort("您取消了微信登录接入！")
                        }
                        BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                            ToastUtils.showShort("您拒绝了微信登录接入！")
                        }
                        else -> {
                            ToastUtils.showShort("微信登录接入失败！${baseResp.errCode}")
                        }
                    }
                    finish()
                }
            }

            //微信分享
            if (baseResp is SendMessageToWX.Resp) {
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) { //响应的类型是发消息类型
                    when (baseResp.errCode) {
                        //成功分享
                        BaseResp.ErrCode.ERR_OK -> {
                            //分享结束后，如果选择了"留在微信"，则不会有此回调
                        }
                        //用户取消分享
                        BaseResp.ErrCode.ERR_USER_CANCEL -> {
                            ToastUtils.showShort("您取消了分享！")
                        }
                        else -> {
                            ToastUtils.showShort("微信分享失败！${baseResp.errCode}")
                        }
                    }
                    finish()
                }
            }
        }

    override fun onReq(baseReq: BaseReq?) {

    }

}
