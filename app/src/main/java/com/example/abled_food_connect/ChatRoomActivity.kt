package com.example.abled_food_connect

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.ChatAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityChatRoomBinding
import com.example.abled_food_connect.retrofit.ChatClient
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    private lateinit var thumbnailImage: String
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var gson: Gson
    private lateinit var chatList: ArrayList<ChatItem>
    private var pagenum: Int = 0
    private lateinit var snackbar: Snackbar
    private lateinit var snackbarTextView: TextView
    private lateinit var snackbarView: View
    private var requestPage: Boolean = true
    private var firstLoading: Boolean = true
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
        Log.e("유져 정보", roomId.toString() + userName)
        chatList = ArrayList()
        gson = Gson()
        init()
        snackbar = Snackbar.make(binding.ChatRoomCoordinator, "", Snackbar.LENGTH_INDEFINITE)
        snackbarView = snackbar.view
        messageLoad(pagenum)

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
        binding.groupChatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
//                var first = layoutManager.findFirstCompletelyVisibleItemPosition()
//                val count = 0
//                pagenum = layoutManager.itemCount-1
//                if (first == count) {
//
//                    messageLoad(pagenum)
//
//                }
            }


            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                var first = layoutManager.findFirstCompletelyVisibleItemPosition()
                val count = 0
                var last = layoutManager.findLastCompletelyVisibleItemPosition()
                var lc = layoutManager.itemCount - 1
                pagenum = layoutManager.itemCount - 1
                if (first == count&&requestPage) {

                    messageLoad(pagenum)

                } else if (last >= lc && snackbar.isShown) {
                    snackbar.dismiss()
                }
            }
        })

        binding.groupChatSendMessageButton.setOnClickListener {
            if (binding.groupChatInputMessageEditText.length() > 0) {
                binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                timelimeCheck()
            }
        }
        socket.connect()
        socket.on(
            Socket.EVENT_CONNECT,
            Emitter.Listener { socket.emit("enter", gson.toJson(RoomData(userName, roomId))) })


        socket.on(
            "update",
            Emitter.Listener {
                val data: MessageData = gson.fromJson(it[0].toString(), MessageData::class.java)

                val layoutManager =
                    LinearLayoutManager::class.java.cast(binding.groupChatRecyclerView.layoutManager)
                var last = layoutManager.findLastCompletelyVisibleItemPosition()
                var lc = layoutManager.itemCount - 1
                if (last < lc) {
                    addChat(data)
                    onSnackbar(data)

                } else {
                    addChat(data)
                    runOnUiThread {

                        binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }

                }

            })


    }

    private fun sendMessage() {
        if (socket.connected()) {
            socket.emit(
                "newMessage",
                gson.toJson(
                    MessageData(
                        "MESSAGE",
                        userName,
                        roomId,
                        binding.groupChatInputMessageEditText.text.toString(), thumbnailImage
                    )
                )
            )
//        chatList.add(
//            ChatItem(
//                userName,
//                thumbnailImage,
//                binding.groupChatInputMessageEditText.text.toString(),
//                toDate(System.currentTimeMillis()),
//                ItemType.RIGHT_MESSAGE
//            )
//        )

            binding.groupChatInputMessageEditText.setText("")
        } else {
            Log.e("연결안됨", "연결안됨")
        }
    }

    private fun toDate(long: Long): String {
        return SimpleDateFormat("a hh:mm").format(Date(long))
    }

    private fun addChat(messageData: MessageData) {
        runOnUiThread(Runnable {
            if (messageData.type == "ENTER" || messageData.type == "LEFT") {

            } else if (messageData.type == "IMAGE") {

            } else if (messageData.type == "TIMELINE") {
                chatList.add(
                    ChatItem(
                        messageData.from, messageData.thumbnailImage, messageData.content,
                        messageData.sendTime!!, ItemType.CENTER_MESSAGE
                    )
                )

            } else {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatList.add(
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.LEFT_MESSAGE
                    )
                )
            }

        })

    }

    private fun loadChat(messageData: MessageData) {
        runOnUiThread(Runnable {
            if (messageData.type == "ENTER" || messageData.type == "LEFT") {

            } else if (messageData.type == "IMAGE") {

            } else if (messageData.type == "TIMELINE") {
                chatList.add(
                    0,
                    ChatItem(
                        messageData.from, messageData.thumbnailImage, messageData.content,
                        messageData.sendTime!!, ItemType.CENTER_MESSAGE
                    )
                )
                chatAdapter.notifyItemInserted(0)

            } else {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatList.add(
                    0,
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.LEFT_MESSAGE
                    )
                )
                chatAdapter.notifyItemInserted(0)
            }

        })

    }

    fun timelimeCheck() {


        val retrofit =
            Retrofit.Builder()
                .baseUrl("http://52.78.107.230/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.timelineCheck("datetime").enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.body() == "true") {
                    timeLineadd()
                    sendMessage()
                } else {
                    sendMessage()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    private fun timeLineadd() {
        socket.emit(
            "TIMELINE",
            gson.toJson(
                MessageData(
                    "TIMELINE",
                    "TIMELINE",
                    roomId,
                    "SERVER", "SERVER",
                    "SERVER"
                )
            )
        )
    }

    private fun messageLoad(pagenum: Int) {
        val retrofit =
            Retrofit.Builder()
                .baseUrl("http://52.78.107.230/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.pagination(roomId, pagenum).enqueue(object : Callback<paginationData> {
            override fun onResponse(
                call: Call<paginationData>,
                response: Response<paginationData>
            ) {
                if (response.body() != null) {
                    val list: paginationData? = response.body()
                    if (list!!.success) {
                        for (index in list.ChatLogList.indices) {
                            loadChat(list.ChatLogList[index])

                        }
                        if (firstLoading) {
                            binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                            firstLoading = false
                        }
                    } else {
                        requestPage = false
                        Log.e("로드 메세지끝", requestPage.toString() )
                    }
                }

            }

            override fun onFailure(call: Call<paginationData>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun onSnackbar(messageData: MessageData) {

        if (snackbar.isShown) {
            runOnUiThread {
                snackbarTextView =
                    snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                snackbarTextView.maxLines = 1
                snackbarTextView.text = "${messageData.from} : ${messageData.content}"
                snackbarView.isClickable = true
                snackbarView.isFocusable = true
                snackbarView.setOnClickListener {
                    snackbar.dismiss()
                    binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        } else {
            runOnUiThread {
                snackbarTextView =
                    snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                snackbarTextView.maxLines = 1
                snackbarTextView.text = "${messageData.from} : ${messageData.content}"
                snackbarView.isClickable = true
                snackbarView.isFocusable = true
                snackbarView.setOnClickListener {
                    snackbar.dismiss()
                    binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
                snackbar.show()
            }
        }
    }
}