package com.jzz.treasureship.wxapi

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.ui.activity.PaySuccessActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    private lateinit var api: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = App.iwxapi;
        api.handleIntent(intent, this);
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    override fun onResp(baseResp: BaseResp?) {
        //支付的响应
        if (baseResp != null && baseResp is PayResp) {
            //响应的类型是支付类型
            if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                when (baseResp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> {
                        finish()
                    }
                    BaseResp.ErrCode.ERR_COMM -> {
                    }
                    BaseResp.ErrCode.ERR_USER_CANCEL -> {

                    }
                    else -> {
                        ToastUtils.showShort("支付出现问题！${baseResp.errCode}！")

                    }
                }
                finish()
            }
        }
    }

    override fun onReq(baseReq: BaseReq?) {

    }
}