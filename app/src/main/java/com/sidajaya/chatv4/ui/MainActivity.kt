package com.sidajaya.chatv4.ui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidajaya.chatv4.data.*
import com.sidajaya.chatv4.util.Constants
class MainActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val sm = SessionManager(applicationContext)
        setContent{
            MaterialTheme(colorScheme = darkColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF8FB4FF))){
                var logged by remember{ mutableStateOf(false) }
                if(!logged) LoginScreen{ logged=true } else ChatScreen(sm)
            }
        }
    }
}
@Composable fun LoginScreen(onOk:()->Unit){
    var u by remember{ mutableStateOf("") }; var p by remember{ mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center){
        Text("Sidajaya AI", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value=u, onValueChange={u=it}, label={Text("Email atau nama akun")}, modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value=p, onValueChange={p=it}, label={Text("Password")}, modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick={ onOk() }, modifier=Modifier.fillMaxWidth()){ Text("Masuk") }
        Text("Pakai akun yang sama di ai.sidajaya.shop", style=MaterialTheme.typography.bodySmall)
    }
}
@Composable fun ChatScreen(sm: SessionManager){
    val repo = remember{ val api = ApiFactory(sm).create(sm.baseUrl()); ChatRepository(api) }
    val vm: ChatViewModel = remember{ ChatViewModel(repo) }
    var input by remember{ mutableStateOf("") }
    Column(Modifier.fillMaxSize()){
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)){
            Constants.MODES.forEach{ m -> FilterChip(selected = vm.mode==m, onClick={ vm.setMode(m) }, label={Text(m)}) }
            Spacer(Modifier.weight(1f))
            TextButton(onClick={ vm.newChat() }){ Text("Baru") }
        }
        LazyColumn(Modifier.weight(1f).padding(horizontal=12.dp)){
            items(vm.messages){ msg ->
                Card(Modifier.fillMaxWidth().padding(vertical=4.dp), colors = CardDefaults.cardColors(containerColor = if(msg.role=="user") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)){
                    Text(msg.text, modifier=Modifier.padding(12.dp))
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)){
            OutlinedTextField(value=input, onValueChange={input=it}, modifier=Modifier.weight(1f), placeholder={Text("Tulis pesan...")}, enabled=!vm.loading)
            Button(onClick={ val t=input; input=""; vm.send(t) }, enabled=!vm.loading){ Text("Kirim") }
        }
        vm.error?.let{ Text(it, color=MaterialTheme.colorScheme.error, modifier=Modifier.padding(8.dp)) }
    }
}
