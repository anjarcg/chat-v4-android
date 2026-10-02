package com.sidajaya.chatv4.data
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {
    @FormUrlEncoded @POST("index.php")
    suspend fun login(@Field("username") u:String, @Field("password") p:String, @Field("action") a:String="login"): ResponseBody
    @Multipart @POST("chat_start")
    suspend fun startChat(
        @Part("message") message: RequestBody?,
        @Part("sessionId") sessionId: RequestBody,
        @Part("model") model: RequestBody?,
        @Part("temperature") temp: RequestBody?,
        @Part files: List<MultipartBody.Part> = emptyList()
    ): StartResp
    @GET("chat_status") suspend fun poll(@Query("job_id") id:String): StatusResp
    @FormUrlEncoded @POST("chat.php") suspend fun chatAction(@Field("action") a:String, @Field("session_id") sid:String, @Field("new_title") t:String?=null): ResponseBody
}
class ApiFactory(private val sm: SessionManager){
    fun create(base:String): ApiService {
        val jar = object: CookieJar {
            private val store = mutableMapOf<String, List<Cookie>>()
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>){ store[url.host] = cookies }
            override fun loadForRequest(url: HttpUrl): List<Cookie> = store[url.host] ?: emptyList()
        }
        val client = OkHttpClient.Builder().cookieJar(jar)
            .connectTimeout(15, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply{ level = HttpLoggingInterceptor.Level.BASIC }).build()
        return Retrofit.Builder().baseUrl(if(base.endsWith("/")) base else "$base/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}
fun String.toRB() = toRequestBody("text/plain".toMediaTypeOrNull())
