package com.jzz.treasureship

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import cn.jpush.android.api.JPushInterface
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.jzz.treasureship.constants.Constants
import com.jzz.treasureship.log.CrashHandler4SaveLog2File
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.service.OKHttpUpdateHttpService
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.xuexiang.xui.XUI
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION
import com.xuexiang.xupdate.utils.UpdateUtils
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import kotlin.properties.Delegates


class App : Application() {
    companion object {
        var CONTEXT: Context by Delegates.notNull()
        lateinit var CURRENT_USER: User
        lateinit var iwxapi: IWXAPI

        fun getAppContext(): Context {
            return CONTEXT
        }
    }

    override fun onCreate() {
        super.onCreate()
//        ImageSelector.setImageEngine(GlideEngine())
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        DoraemonKit.install(this)
        CONTEXT = applicationContext
        initAutoSize()
//        val crashHandler: CrashHandler4SaveLog2File = CrashHandler4SaveLog2File.getInstance()
//        crashHandler.init(CONTEXT)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        handleSSLHandshake()
        initWxSdk()
        initJPush()
        initUpdate()
    }

    private fun initAutoSize() {
        AutoSizeConfig.getInstance()
            .setBaseOnWidth(true)
            .unitsManager
            .setSupportDP(true)
            .setSupportSP(true)
            .supportSubunits = Subunits.MM
    }

    private fun initWxSdk() {
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID)
        iwxapi.registerApp(Constants.WX_APP_ID)

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { // 将该app注册到微信
                iwxapi.registerApp(Constants.WX_APP_ID)
            }
        }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
    }

    /**
     * 忽略https的证书校验
     * 避免Glide加载https图片报错：
     * javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
     */
    fun handleSSLHandshake() {
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object : X509TrustManager {
                val acceptedIssuers: Array<Array<X509Certificate?>>
                    get() = arrayOf(arrayOfNulls<X509Certificate>(0))

                override fun checkClientTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return acceptedIssuers[0]
                }
            })
            val sc: SSLContext = SSLContext.getInstance("TLS")
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
            HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                    return true
                }
            })
        } catch (ignored: Exception) {
        }
    }

    /**
     * 初始化极光推送
     */
    private fun initJPush() {
        JPushInterface.setDebugMode(BuildConfig.DEBUG)
        //只需要在应用程序启动时调用一次该 API 即可
        JPushInterface.init(this)
    }

    private fun initUpdate() {
        XUpdate.get()
            .setIUpdateHttpService(OKHttpUpdateHttpService())
            .init(this) //这个必须初始化

    }
}
