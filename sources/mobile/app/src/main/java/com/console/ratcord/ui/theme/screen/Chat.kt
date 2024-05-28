import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.compose.ui.text.font.FontWeight


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
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (roomName != null) {
                        sendGroupMessage(mSocket, message, roomName, username)
                    }
                    println("Send Message: $message to Room: $roomName") // Add logging for debugging
                    message = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
            ) {
                items(messages) { msg ->
                    MessageCard(msg = msg)
                }
            }
        }
    }
}


@Composable
fun MessageCard(msg: String) {
    // Assuming the message format is "sender [date]: content"
    val parts = msg.split(": ", limit = 2)
    val senderAndDate = parts[0]
    val content = if (parts.size > 1) parts[1] else ""

    val senderAndDateParts = senderAndDate.split(" [", "]")
    val sender = senderAndDateParts[0]
    val date = if (senderAndDateParts.size > 1) senderAndDateParts[1] else ""

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row {
                Text(
                    text = "$sender: ",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
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

fun handleGroupMessage(
    args: Array<Any>,
    coroutineScope: CoroutineScope,
    messages: SnapshotStateList<String>,
    isoDateFormat: SimpleDateFormat,
    displayDateFormat: SimpleDateFormat
) {
    val data = args[0] as JSONObject
    try {
        val sender = data.getString("sender")
        val content = data.getString("content")
        val createdAt = data.getString("createdAt")
        val date = isoDateFormat.parse(createdAt)
        val formattedDate = displayDateFormat.format(date)
        println("Received message from $sender: $content at $formattedDate") // Logging received message
        coroutineScope.launch {
            messages.add("$sender [$formattedDate]: $content")
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun handleOldMessages(
    args: Array<Any>,
    coroutineScope: CoroutineScope,
    messages: SnapshotStateList<String>,
    isoDateFormat: SimpleDateFormat,
    displayDateFormat: SimpleDateFormat
) {
    val oldMessages = args[0] as JSONArray
    val oldMsgsList = mutableListOf<String>()
    for (i in 0 until oldMessages.length()) {
        val data = oldMessages.getJSONObject(i)
        val sender = data.getString("sender")
        val content = data.getString("content")
        val createdAt = data.getString("createdAt")
        val date = isoDateFormat.parse(createdAt)
        val formattedDate = displayDateFormat.format(date)
        oldMsgsList.add("$sender [$formattedDate]: $content")
    }
    coroutineScope.launch {
        messages.addAll(oldMsgsList)
    }
}
