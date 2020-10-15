package com.lc.mybaselibrary

import android.content.Context
import android.content.Intent

/**
 *@date: 2020/9/7
 *@describe:
 *@Auth: 29579
 **/
inline fun <reified T > Context.start(block: Intent.()->Unit = {}){
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent )
}
