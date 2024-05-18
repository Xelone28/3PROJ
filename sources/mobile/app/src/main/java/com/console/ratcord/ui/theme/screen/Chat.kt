import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(username: String, roomName: String? = null, privateRecipient: String? = null) {
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()
    val isoDateFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) }
    val displayDateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.getDefault()) }

    val mSocket = remember { IO.socket("http://10.0.2.2:4000") }
    val privateMessageHandler = remember {
        PrivateMessageHandler(mSocket, coroutineScope, isoDateFormat, displayDateFormat)
    }

    LaunchedEffect(Unit) {
        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT) {
            println("Socket connected")
            mSocket.emit("joinRoom", JSONObject().apply { put("room", roomName) })
            println("Joined Room: $roomName")
        }
        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            println("Socket connection error: ${args.joinToString()}")
        }
        mSocket.on("message", Emitter.Listener { args ->
            handleGroupMessage(args, coroutineScope, messages, isoDateFormat, displayDateFormat)
        })
        mSocket.on("oldMessages", Emitter.Listener { args ->
            handleOldMessages(args, coroutineScope, messages, isoDateFormat, displayDateFormat)
        })
    }

    if (privateRecipient != null) {
        privateMessageHandler.PrivateChatScreen(username, privateRecipient)
    } else {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (roomName != null) {
                    sendGroupMessage(mSocket, message, roomName, username)
                }
                println("Send Message: $message to Room: $roomName") // Add logging for debugging
                message = ""
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Send")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(messages) { msg ->
                    Text(msg)
                }
            }
        }
    }
}

fun sendGroupMessage(mSocket: Socket, message: String, roomName: String, username: String) {
    mSocket.emit("sendGroupMessage", JSONObject().apply {
        put("content", message)
        put("room", roomName)
        put("sender", username)
    })
}

fun handleGroupMessage(args: Array<Any>, coroutineScope: CoroutineScope, messages: SnapshotStateList<String>, isoDateFormat: SimpleDateFormat, displayDateFormat: SimpleDateFormat) {
    val data = args[0] as JSONObject
    try {
        val sender = data.getString("sender")
        val content = data.getString("content")
        val createdAt = data.getString("createdAt")
        val date = isoDateFormat.parse(createdAt)
        val formattedDate = displayDateFormat.format(date)
        println("Received message from $sender: $content at $formattedDate") // Logging received message
        coroutineScope.launch {
            messages.add("$sender [$formattedDate] : $content")
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun handleOldMessages(args: Array<Any>, coroutineScope: CoroutineScope, messages: SnapshotStateList<String>, isoDateFormat: SimpleDateFormat, displayDateFormat: SimpleDateFormat) {
    val oldMessages = args[0] as JSONArray
    val oldMsgsList = mutableListOf<String>()
    for (i in 0 until oldMessages.length()) {
        val data = oldMessages.getJSONObject(i)
        val sender = data.getString("sender")
        val content = data.getString("content")
        val createdAt = data.getString("createdAt")
        val date = isoDateFormat.parse(createdAt)
        val formattedDate = displayDateFormat.format(date)
        oldMsgsList.add("$sender: $content ($formattedDate)")
    }
    coroutineScope.launch {
        messages.addAll(oldMsgsList)
    }
}
