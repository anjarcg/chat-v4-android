package com.sidajaya.chatv4.data
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
private val Context.prefs by preferencesDataStore("chatv4")
class SessionManager(private val ctx: Context){
    private val K_BASE = stringPreferencesKey("base_url")
    private val K_COOKIE = stringPreferencesKey("cookie")
    private val K_USER = stringPreferencesKey("username")
    suspend fun baseUrl():String = ctx.prefs.data.map{it[K_BASE]?: "https://ai.sidajaya.shop/"}.first()
    suspend fun setBaseUrl(v:String){ ctx.prefs.edit{it[K_BASE]=v} }
    suspend fun cookie():String? = ctx.prefs.data.map{it[K_COOKIE]}.first()
    suspend fun setCookie(v:String){ ctx.prefs.edit{it[K_COOKIE]=v} }
    suspend fun clear(){ ctx.prefs.edit{it.clear()} }
}
