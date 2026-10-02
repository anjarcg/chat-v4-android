package com.sidajaya.chatv4.data
import android.content.Context
import android.content.SharedPreferences
class SessionManager(ctx: Context){
    private val sp: SharedPreferences = ctx.getSharedPreferences("chatv4", Context.MODE_PRIVATE)
    fun baseUrl():String = sp.getString("base_url","https://ai.sidajaya.shop/") ?: "https://ai.sidajaya.shop/"
    fun setBaseUrl(v:String){ sp.edit().putString("base_url", v).apply() }
}
