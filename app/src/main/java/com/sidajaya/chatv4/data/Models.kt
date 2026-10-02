package com.sidajaya.chatv4.data
data class LoginResp(val ok:Boolean=false, val error:String?=null)
data class StartResp(val job_id:String?, val error:String?)
data class StatusResp(val status:String, val reply:String?=null, val error:String?=null, val final_cost:Int?=null)
data class SessionInfo(val session_id:String, val title:String, val updated_at:String?)
data class Msg(val role:String, val message:String, val created_at:String?=null)
