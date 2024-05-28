import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Message(val sender: String, val content: String, val createdAt: String)

class PrivateMessageHandler(
    private val mSocket: Socket,
    private val coroutineScope: CoroutineScope,
    private val isoDateFormat: SimpleDateFormat,
    private val displayDateFormat: SimpleDateFormat
) {
    val messages = mutableStateListOf<Message>()

    init {
        mSocket.on("privateMessage", Emitter.Listener { args ->
            println("Received private message event")
            handlePrivateMessage(args)
        })
        mSocket.on("privateConversation", Emitter.Listener { args ->
            println("Received private conversation event")
            handleOldPrivateMessages(args)
        })
    }

    fun sendPrivateMessage(message: String, recipient: String, sender: String) {
        mSocket.emit("sendPrivateMessage", JSONObject().apply {
            put("content", message)
            put("recipient", recipient)
            put("sender", sender)
        })
    }

    private fun handlePrivateMessage(args: Array<Any>) {
        val data = args[0] as JSONObject
        try {
            val sender = data.getString("sender")
            val content = data.getString("content")
            val createdAt = data.getString("createdAt")
            val date = isoDateFormat.parse(createdAt)
            val formattedDate = displayDateFormat.format(date)
            println("Received private message from $sender: $content at $formattedDate") // Logging received message
            coroutineScope.launch {
                messages.add(Message(sender, content, formattedDate))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchConversation(sender: String, recipient: String) {
        mSocket.emit("fetchPrivateConversation", JSONObject().apply {
            put("sender", sender)
            put("recipient", recipient)
        })
    }

    private fun handleOldPrivateMessages(args: Array<Any>) {
        val oldMessages = args[0] as JSONArray
        val oldMsgsList = mutableListOf<Message>()
        for (i in 0 until oldMessages.length()) {
            val data = oldMessages.getJSONObject(i)
            val sender = data.getString("sender")
            val content = data.getString("content")
            val createdAt = data.getString("createdAt")
            val date = isoDateFormat.parse(createdAt)
            val formattedDate = displayDateFormat.format(date)
            oldMsgsList.add(Message(sender, content, formattedDate))
        }
        coroutineScope.launch {
            messages.addAll(oldMsgsList)
        }
    }

    @Composable
    fun PrivateChatScreen(sender: String, recipient: String) {
        var message by remember { mutableStateOf("") }

        LaunchedEffect(recipient) {
            fetchConversation(sender, recipient)
        }

        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()) {
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                sendPrivateMessage(message, recipient, sender)
                println("Send Private Message: $message to $recipient") // Add logging for debugging
                message = ""
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Send")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(messages) { msg ->
                    PrivateMessageCard(msg)
                }
            }
        }
    }
}


@Composable
fun PrivateMessageCard(msg: Message) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = msg.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row {
                Text(
                    text = "${msg.sender}: " ,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = msg.content,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
