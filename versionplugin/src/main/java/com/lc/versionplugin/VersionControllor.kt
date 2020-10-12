package com.lc.versionplugin

import com.lc.versionplugin.VersionControllor.version.appCompatVersion
import com.lc.versionplugin.VersionControllor.version.constraintlayoutVersion
import com.lc.versionplugin.VersionControllor.version.koinVersion
import com.lc.versionplugin.VersionControllor.version.xuiVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *@author LC
 *@createTime 2020/7/20 21:39
 *@description
 */
class VersionControllor : Plugin<Project> {
    override fun apply(p0: Project) {
        println("当前我的名字是${p0.name}，是一个管理依赖库的版本的")
    }
private object version{
    val appCompatVersion = "1.2.0-rc01"
    val constraintlayoutVersion = "1.1.3"
    val ktxCoretVersion = "1.3.0"
    val koinVersion = "2.1.5"
    val oppoVersion = "2.0.2"
    val BRAVHVersion = "3.0.4"
    val RETROFITVersion = "2.9.0"
    val coroutinesVersion = "1.3.8"
    val moshiVersion = "1.9.3"
    val fragmentKtxVersion = "1.2.5"
    val viewModelKtxVersion = "1.2.5"
    val lifecycleVersion = "2.2.0"
    val toastyVersion = "1.4.2"
    val mmkvVersion = "1.2.2"
    val fileVersion = "1.0.2"
    val xuiVersion = "1.1.5"
    val materialVersion = "material-rc01"
//    https://github.com/JackLiaoJH/ImageSelect
    val imageChoiseVersion = "1.2.1"
    val xpopVersion = "2.0.11"
    val flexboxVersion = "2.0.1"
    val loggerVersion = "2.2.0"
}

    object Android{
        val compileSdkVersion = 29
        val applicationId = "com.jzz.treasureship"
        val minSdkVersion = 21
        val targetSdkVersion = 28
        val versionCode = 135
        val versionName = "1.3.5"
    }
 object AndroidX {
     val appCompat = "androidx.appcompat:appcompat:${appCompatVersion}"
     val constraintlayout = "androidx.constraintlayout:constraintlayout:$constraintlayoutVersion"
     val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${version.coroutinesVersion}"
     val viewmodelSavedstate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${version.lifecycleVersion}"
     val material = "com.google.android.material:${version.materialVersion}"
     val flexboxLayout ="com.google.android:flexbox:${version.flexboxVersion}"
 }

    object koin{
        val koinAndroid = "org.koin:koin-android:$koinVersion"
        val koinAndroidxViewmodel = "org.koin:koin-androidx-viewmodel:$koinVersion"
        val koinExt = "org.koin:koin-androidx-ext:$koinVersion"
    }
    object dependance{
        val BRAVH = "com.github.CymChad:BaseRecyclerViewAdapterHelper:${version.BRAVHVersion}"
        val RETROFIT = "com.squareup.retrofit2:retrofit:${version.RETROFITVersion}"
        val MOSHI= "com.squareup.retrofit2:converter-moshi:${version.RETROFITVersion}"
        val MOSHI02= "com.squareup.moshi:moshi-kotlin:${version.moshiVersion}"
        val TOASTY= "com.github.GrenderG:Toasty:${version.toastyVersion}"
        val MMKV= "com.tencent:mmkv-static:${version.mmkvVersion}"
//        文件选择器
        val FILE= "com.sky.filePicker:filePicker:${version.fileVersion}"
        val xui = "com.github.xuexiangjys:XUI:${xuiVersion}"
        val imageSelector = "com.github.JackLiaoJH:ImageSelect:${version.imageChoiseVersion}"
//        弹窗  @{link:https://github.com/li-xiaojun/XPopup}
        val xpop = "com.lxj:xpopup:${version.xpopVersion}"


        val logger = "com.orhanobut:logger:${version.loggerVersion}"
    }

    object oppo{
        val oppoPush = "com.heytap.mcssdk:mcssdk:${version.oppoVersion}"
    }

    object Ktx{
        val fragmentKtx = "androidx.fragment:fragment-ktx:${version.fragmentKtxVersion}"
        val ktxCore = "androidx.core:core-ktx:${version.ktxCoretVersion}"
        val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx::${version.viewModelKtxVersion}"
    }
}