package com.sidajaya.chatv4.ui
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidajaya.chatv4.data.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
data class UiMsg(val role:String, val text:String, val pending:Boolean=false)
class ChatViewModel(private val repo: ChatRepository): ViewModel(){
    var messages by mutableStateOf(listOf<UiMsg>()); private set
    var sessionId by mutableStateOf("sess_${System.currentTimeMillis()}"); private set
    var mode by mutableStateOf("auto"); private set
    var loading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null)
    fun setMode(m:String){ mode=m }
    fun newChat(){ sessionId="sess_${System.currentTimeMillis()}"; messages=emptyList(); error=null }
    fun send(text:String, files:List<MultipartBody.Part> = emptyList()){
        if(text.isBlank() && files.isEmpty()) return
        messages = messages + UiMsg("user", text.ifBlank{"[file]"}, false) + UiMsg("assistant","...", true)
        loading=true; error=null
        viewModelScope.launch{
            val r = repo.start(sessionId, text, mode, files)
            if(r.isFailure){ val e=r.exceptionOrNull()?.message?:"gagal"; messages=messages.dropLast(1)+UiMsg("assistant","Error: $e"); loading=false; return@launch }
            val jobId = r.getOrNull()!!
            val p = repo.pollUntilDone(jobId)
            messages = messages.dropLast(1)
            if(p.isSuccess){ messages = messages + UiMsg("assistant", p.getOrNull()?.reply ?: "(kosong)") } else { messages = messages + UiMsg("assistant","Gagal: ${p.exceptionOrNull()?.message}") }
            loading=false
        }
    }
}
