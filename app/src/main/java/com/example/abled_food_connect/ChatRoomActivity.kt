package com.example.abled_food_connect

import android.media.ThumbnailUtils
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abled_food_connect.adapter.ChatAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityChatRoomBinding
import com.example.abled_food_connect.retrofit.ChatClient
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatRoomActivity : AppCompatActivity() {
    val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater) }
    val TAG = "그룹채팅 액티비티"
    private lateinit var socket: Socket
    private lateinit var chatClient: ChatClient
    private lateinit var userName: String
    private lateinit var roomId: String
    private lateinit var thumbnailImage :String
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var gson: Gson
    private lateinit var chatList: ArrayList<ChatItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.groupChatRoomToolbar)
        val tb = supportActionBar!!
        tb.title = "그룹 채팅방"
        binding.chatDrawerLinear.setOnTouchListener { _, _ -> true }
        roomId = intent.getStringExtra("roomId").toString()
        userName = MainActivity.loginUserNickname.toString()
        thumbnailImage = MainActivity.userThumbnailImage.toString()
        Log.e("유져 정보", roomId.toString()+userName)
        chatList = ArrayList()
        gson = Gson()
        init()

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart 호출")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onStart 호출")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume 호출")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause 호출")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop 호출")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy 호출")
        socket.emit("left", gson.toJson(RoomData(userName, roomId)))
        socket.disconnect()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_chat_room_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.groupChatMenu -> {

                Toast.makeText(this, "버튼 눌림", Toast.LENGTH_SHORT).show()
                binding.chatDrawerLayout.openDrawer(Gravity.RIGHT)
                true

            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onBackPressed() {

        if (binding.chatDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.chatDrawerLayout.closeDrawer(Gravity.RIGHT)
        } else {
            super.onBackPressed()
        }
    }

    fun init() {

        socket = IO.socket(getString(R.string.chat_socket_url))
        Log.d("SOCKET", "Connection success : " + socket.id())

        chatClient = ChatClient.getInstance()
        chatAdapter = ChatAdapter(this, chatList)
        binding.groupChatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groupChatRecyclerView.adapter = chatAdapter

        binding.groupChatSendMessageButton.setOnClickListener { sendMessage() }
        socket.connect()
        socket.on(
            Socket.EVENT_CONNECT,
            Emitter.Listener { socket.emit("enter", gson.toJson(RoomData(userName, roomId))) })
        socket.on(
            "update",
            Emitter.Listener {
                val data: MessageData = gson.fromJson(it[0].toString(), MessageData::class.java)
                addChat(data)
            })


    }

    private fun sendMessage() {
        socket.emit(
            "newMessage",
            gson.toJson(
                MessageData(
                    "MESSAGE",
                    userName,
                    roomId,
                    binding.groupChatInputMessageEditText.text.toString(),thumbnailImage,
                    System.currentTimeMillis()
                )
            )
        )
        chatList.add(
            ChatItem(
                userName,
                thumbnailImage,
                binding.groupChatInputMessageEditText.text.toString(),
                toDate(System.currentTimeMillis()),
                ItemType.RIGHT_MESSAGE
            )
        )
        binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        binding.groupChatInputMessageEditText.setText("")
    }

    private fun toDate(long: Long): String {
        return SimpleDateFormat("a hh:mm").format(Date(long))
    }

    private fun addChat(messageData: MessageData) {
        runOnUiThread(Runnable {
            if (messageData.type == "ENTER" || messageData.type == "LEFT") {

            } else if (messageData.type == "IMAGE") {

            } else {
                chatList.add(
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        toDate(messageData.sendTime),
                        ItemType.LEFT_MESSAGE
                    )
                )
            }
            binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        })

    }
}