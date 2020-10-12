package com.lc.mybaselibrary

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 *@date: 2020/9/21
 *@describe:
 *@Auth: 29579
 **/
class assertRead {
    companion object{
        fun getFromAssets(mContext: Context, fileName: String): String? {
            val content: String? = null //结果字符串
            try {
                val inputReader = InputStreamReader(mContext.assets.open(fileName!!))
                val bufReader = BufferedReader(inputReader)
                var line: String? = ""
                val builder = StringBuilder()
                while (bufReader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                inputReader.close()
                bufReader.close()
                return builder.toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return content
        }
    }
}