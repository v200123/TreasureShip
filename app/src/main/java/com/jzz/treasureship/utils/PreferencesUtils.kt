package com.jzz.treasureship.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jzz.treasureship.App
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceUtils<T>(val name: String, private val default: T) : ReadWriteProperty<Any?, T> {

    companion object {
        const val FIRST_BOOT = "first_boot"
        const val IS_LOGIN = "is_login"
        const val USER_GSON = "user_gson"
        const val TOP_ADDRESS = "top_address"
        const val MID_ADDRESS = "mid_address"
        const val LAST_ADDRESS = "last_address"
        const val SELECTED_SKU = "selected_sku"
        const val SELECTED_ADDRESS = "selected_address"
        const val DELETED_ADDRESS = "deleted_address"
        const val CLICKED_COLLECT_ID = "clicked_collect_id"
        const val MOVE_VIDEO = "move_video"
        const val DEL_VIDEO = "del_video"
        const val CONTACTER_GOODS = "contacter_goods"
        const val CANCLE_NOTICE = "cancle_notice"
        const val NOTICE_TIME = "notice_time"
        const val NOTICE_TYPE = "notice_type"
        const val WX_CODE = "wx_code"
        const val WX_CODE_BIND = "wx_code_bind"
        const val ACCESS_TOKEN = "access_token"
        const val AUDIT_STATUS = "audit_status"
        const val TSB_UPDATE = "tsb_update"
        const val ALL_PLACES = "all_PLACES"
        const val ORDER_ID = "order_id"
        const val CUR_FRAGMENT = "cur_fragment"
        const val PAY_SUCCESS = "pay_success"
        const val CHECK_AGREEMENT = "check_agreement"
        const val ALREADY_LIC = "already_license"
        const val ACCEPT_LIC = "accept_license"
        const val COMFIRM_WITHDRAW = "comfirm_withdraw"
        const val WITHDRAW_MONEY = "withdraw_money"
        const val start_answer = "start_answer"
        const val goto_wallet = "goto_wallet"
        const val get_reward = "get_reward"
        const val open_reward = "open_reward"
        const val everyday_invite_dialog = "invite_dialog"
        const val auth_is_show= "auth_dialog"
        const val no_auth_show = "niauth_show"
        const val authShowSuccess = "authShowSuccess"
    }

    private val prefs: SharedPreferences by lazy {

        PreferenceManager.getDefaultSharedPreferences(App.CONTEXT)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getValue(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putValue(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun <T> putValue(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> this.getString(name, default)!!
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(this.getString(name, serialize(default))!!)
        }
        return res as T
    }

    /**
     * 删除全部数据
     */
    fun clearPreference() {
        prefs.edit().clear().apply()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * 序列化对象
     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象
     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(): Map<String, *> {
        return prefs.all
    }
}