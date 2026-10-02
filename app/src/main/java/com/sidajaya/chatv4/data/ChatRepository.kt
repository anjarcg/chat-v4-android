package com.sidajaya.chatv4.data
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.sidajaya.chatv4.util.Constants

class ChatRepository(private val api: ApiService){
    suspend fun login(user:String, pass:String): Result<Unit> = try {
        val r = api.login(user, pass)
        val body = r.string()
        if(body.contains("chat.php", true)) Result.success(Unit) else Result.failure(Exception(if(body.length>400) body.take(400) else body.ifBlank{"Login gagal"}))
    }catch(e:Exception){ Result.failure(e) }

    suspend fun start(sessionId:String, message:String, mode:String="auto", files:List<MultipartBody.Part> = emptyList()): Result<String>{
        return try{
            val msgRB: RequestBody? = if(message.isNotBlank()) message.toRB() else null
            val r = api.startChat(msgRB, sessionId.toRB(), mode.toRB(), "0.7".toRB(), files)
            if(!r.job_id.isNullOrBlank()) Result.success(r.job_id) else Result.failure(Exception(r.error ?: "job_id kosong"))
        }catch(e:Exception){ Result.failure(e) }
    }
    suspend fun pollUntilDone(jobId:String): Result<StatusResp>{
        val deadline = System.currentTimeMillis() + Constants.JOB_TIMEOUT_MS
        while(System.currentTimeMillis() < deadline){
            try{
                val s = api.poll(jobId)
                if(s.status=="done") return Result.success(s)
                if(s.status=="error") return Result.failure(Exception(s.error ?: "error"))
                // queued/processing/processing_callback -> lanjut
            }catch(e:Exception){ /* transient, lanjut */ }
            delay(Constants.POLL_INTERVAL_MS)
        }
        return Result.failure(Exception("Timeout menunggu jawaban AI"))
    }
    suspend fun sessions(): List<SessionInfo> = emptyList() // diisi via chat.php?action=load impl nanti
}
