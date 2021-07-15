package com.example.abled_food_connect

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.abled_food_connect.adapter.ChatAdapter
import com.example.abled_food_connect.adapter.ChatRoomUserListRCVAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityChatRoomBinding
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.ChatClient
import com.example.abled_food_connect.retrofit.MapSearch
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatRoomActivity : AppCompatActivity() {
    val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater) }
    val TAG = "그룹채팅 액티비티"
    val PERMISSIONS_REQUEST_CODE = 100

    private lateinit var chatClient: ChatClient
    private lateinit var userName: String
    private lateinit var chatroomRoomId: String
    private lateinit var chatroomHostName: String
    private lateinit var thumbnailImage: String
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var userList: ChatRoomUserListRCVAdapter
    private lateinit var gson: Gson

    private var pagenum: Int = 0
    private lateinit var snackbar: Snackbar
    private lateinit var snackbarTextView: TextView
    private lateinit var snackbarView: View
    private lateinit var hostName: String
    private lateinit var roomId: String
    private lateinit var vi: View

    var rcheight: Int = 0
    var last: Int = 0
    private lateinit var members: String
    private lateinit var notiIcon: ImageView
    private var requestPage: Boolean = true
    private var firstLoading: Boolean = true
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    companion object {
        lateinit var socket: Socket
    }

    val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.originalUri
            val uriFilePath = result.getUriFilePath(this) // optional usage
            socket.connect()

            timelimeCheck(uriFilePath)


        } else {
            // an error occurred
            val exception = result.error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.groupChatRoomToolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        val tb = supportActionBar!!
        tb.title = "그룹 채팅방"
        binding.chatDrawerLinear.setOnTouchListener { _, _ -> true }
        chatroomRoomId = intent.getStringExtra("roomId").toString()
        chatroomHostName = intent.getStringExtra("hostName").toString()
        userName = MainActivity.loginUserNickname.toString()
        thumbnailImage = MainActivity.userThumbnailImage.toString()
        Log.e("유져 정보", chatroomRoomId.toString() + userName)
        notiIcon = binding.chatRoomNewSubscriptionCircle
        gson = Gson()
//binding.groupChatRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
//
//
//}
        init()
        snackbar = Snackbar.make(binding.groupChatRecyclerView, "", Snackbar.LENGTH_INDEFINITE)
        snackbarView = snackbar.view

        if (chatroomHostName == MainActivity.loginUserNickname) {
            binding.chatRoomSubscription.visibility = View.VISIBLE
        }


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
        socket.connect()
        readMessage()
        hostSubscriptionCheck()
        joinMember()
        socket.emit(
            "read",
            gson.toJson(RoomData(userName, chatroomRoomId, MainActivity.user_table_id))
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause 호출")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop 호출")
        socket.emit(
            "left",
            gson.toJson(RoomData(userName, chatroomRoomId, MainActivity.user_table_id))
        )
        socket.disconnect()
    }

    override fun onDestroy() {

        super.onDestroy()
        Log.d(TAG, "onDestroy 호출")


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.group_chat_room_menu, menu)

        val notiIconView: MenuItem? = menu?.findItem(R.id.groupChatMenu)
        notiIconView?.actionView?.findViewById<ImageView>(R.id.chatNotifyDot)?.visibility =
            View.VISIBLE
        Log.e("노티버튼", notiIconView.toString())





        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.groupChatMenu -> {

                hostSubscriptionCheck()
                binding.chatDrawerLayout.openDrawer(Gravity.RIGHT)
                true

            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onBackPressed() {

        if (binding.chatDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.chatDrawerLayout.closeDrawer(Gravity.LEFT)
        } else {
            if (binding.chatDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                binding.chatDrawerLayout.closeDrawer(Gravity.RIGHT)
            } else {
                super.onBackPressed()
            }
        }
    }

    fun init() {
        roomInfoLoad()
        socket = IO.socket(getString(R.string.chat_socket_url))


        Log.d("SOCKET", "Connection success : " + socket.id())

        chatClient = ChatClient.getInstance()
        chatAdapter = ChatAdapter(this)


        binding.userListRCV.layoutManager = LinearLayoutManager(this)
        binding.groupChatRecyclerView.layoutManager = LinearLayoutManager(this)
        (binding.groupChatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd =
            true
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
                last = layoutManager.findLastCompletelyVisibleItemPosition()
                var lc = chatAdapter.arrayList.size - 1
                pagenum = layoutManager.itemCount - 1
                if (first == count && requestPage && layoutManager.itemCount > 31) {

                    messageLoad(pagenum)

                } else if (last >= lc && snackbar.isShown) {
                    snackbar.dismiss()
                }
            }
        })

        binding.imageButton3.setOnClickListener {
            binding.chatDrawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.groupChatMembersLocationButton.setOnClickListener {
            roomStateCheck()
//            checkPermissions()
//            val i = Intent()
//            i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//            val uri = Uri.fromParts("package", packageName, null)
//            i.data = uri
//            startActivity(i)
        }
        binding.chatRoomSubscription.setOnClickListener {
            val intent = Intent(this, JoinRoomSubscriptionActivity::class.java)
            intent.putExtra("roomId", chatroomRoomId)
            startActivity(intent)
        }

        binding.groupChatSendMessageButton.setOnClickListener {
            if (binding.groupChatInputMessageEditText.length() > 0) {
                binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                timelimeCheck()
            }
        }

        binding.groupChatExitRoomButton.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setMessage("정말로 방을 나가시겠습니까?")
            builder.setPositiveButton(
                "나가기"
            ) { dialog, which -> exitRoom() }
            builder.setNegativeButton("취소", null)
            builder.show()

        }
        binding.groupChatAddImageButton.setOnClickListener {
            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )
        }


        socket.on(
            Socket.EVENT_CONNECT,
            Emitter.Listener {
                socket.emit(
                    "enter",
                    gson.toJson(RoomData(userName, chatroomRoomId, MainActivity.user_table_id))
                )

            })


        socket.on(
            "update",
            Emitter.Listener {
                val data: MessageData = gson.fromJson(it[0].toString(), MessageData::class.java)
                Log.e("메세지 업데이트", "메시지 업데이트")
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
        socket.on("read", Emitter.Listener {
            Log.e("리드", "리드")
            readMessage()

        })

        socket.on(
            "outRoom",
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
                joinMember()

            })
        socket.on(
            "joinRoom",
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
                joinMember()

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
                        chatroomRoomId,
                        binding.groupChatInputMessageEditText.text.toString(),
                        thumbnailImage,
                        null,
                        members
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
            val simdate = SimpleDateFormat("a HH:mm")

            if (messageData.type == "ENTER" || messageData.type == "LEFT") {

            } else if (messageData.type == "IMAGE") {
                if (messageData.from == userName) {
                    val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                    val convertedCurrentDate = sdf.parse(messageData.sendTime)
                    val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)



                    chatAdapter.arrayList.add(

                        ChatItem(
                            messageData.from,
                            messageData.thumbnailImage,
                            messageData.content,
                            date,
                            ItemType.RIGHT_IMAGE_MESSAGE,
                            messageData.members
                        )
                    )

                } else {
                    val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                    val convertedCurrentDate = sdf.parse(messageData.sendTime)
                    val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                    chatAdapter.arrayList.add(

                        ChatItem(
                            messageData.from,
                            messageData.thumbnailImage,
                            messageData.content,
                            date,
                            ItemType.LEFT_IMAGE_MESSAGE,
                            messageData.members
                        )
                    )

                }
            } else if (messageData.type == "TIMELINE" || messageData.type == "JOINMEMBER" || messageData.type == "EXITROOM") {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)

//                when(true){
//                    chatAdapter.arrayList[chatAdapter.arrayList.size-1].name == messageData.from&& time.compareTo(
//                            time2
//                            ) == 0&&->{
//
//                        )
//                    }
//                    chatAdapter.arrayList[chatAdapter.arrayList.size-1].name ==
//                }
                chatAdapter.arrayList.add(
                    ChatItem(
                        messageData.from, messageData.thumbnailImage, messageData.content,
                        date, ItemType.CENTER_MESSAGE,
                        messageData.members, 0
                    )
                )

            } else if (messageData.from == userName) {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatAdapter.arrayList.add(
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.RIGHT_MESSAGE,
                        messageData.members
                    )
                )

            } else {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatAdapter.arrayList.add(
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.LEFT_MESSAGE,
                        messageData.members
                    )
                )

            }

        })

    }

    private fun loadChat(messageData: MessageData) {

        runOnUiThread(Runnable {
            if (messageData.type == "ENTER" || messageData.type == "LEFT") {

            } else if (messageData.type == "IMAGE") {
                if (messageData.from == userName) {
                    val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                    val convertedCurrentDate = sdf.parse(messageData.sendTime)
                    val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)



                    chatAdapter.arrayList.add(
                        0,
                        ChatItem(
                            messageData.from,
                            messageData.thumbnailImage,
                            messageData.content,
                            date,
                            ItemType.RIGHT_IMAGE_MESSAGE,
                            messageData.members
                        )
                    )

                } else {
                    val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                    val convertedCurrentDate = sdf.parse(messageData.sendTime)
                    val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                    chatAdapter.arrayList.add(
                        0,
                        ChatItem(
                            messageData.from,
                            messageData.thumbnailImage,
                            messageData.content,
                            date,
                            ItemType.LEFT_IMAGE_MESSAGE,
                            messageData.members
                        )
                    )

                }
            } else if (messageData.type == "TIMELINE" || messageData.type == "JOINMEMBER" || messageData.type == "EXITROOM") {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)
                chatAdapter.arrayList.add(
                    0,
                    ChatItem(
                        messageData.from, messageData.thumbnailImage, messageData.content,
                        date, ItemType.CENTER_MESSAGE,
                        messageData.members
                    )
                )
                chatAdapter.notifyItemInserted(0)

            } else if (messageData.from == userName) {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatAdapter.arrayList.add(
                    0,
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.RIGHT_MESSAGE,
                        messageData.members
                    )
                )
                chatAdapter.notifyItemInserted(0)

            } else {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatAdapter.arrayList.add(
                    0,
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.LEFT_MESSAGE,
                        messageData.members
                    )
                )
                chatAdapter.notifyItemInserted(0)

            }

        })

    }

    private fun ReadChat(messageData: MessageData) {

        runOnUiThread(Runnable {
            if (messageData.type == "ENTER" || messageData.type == "LEFT") {

            } else if (messageData.type == "IMAGE") {
                if (messageData.from == userName) {
                    val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                    val convertedCurrentDate = sdf.parse(messageData.sendTime)
                    val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                    chatAdapter.arrayList.add(
                        0,
                        ChatItem(
                            messageData.from,
                            messageData.thumbnailImage,
                            messageData.content,
                            date,
                            ItemType.RIGHT_IMAGE_MESSAGE,
                            messageData.members
                        )
                    )

                } else {
                    val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                    val convertedCurrentDate = sdf.parse(messageData.sendTime)
                    val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                    chatAdapter.arrayList.add(
                        0,
                        ChatItem(
                            messageData.from,
                            messageData.thumbnailImage,
                            messageData.content,
                            date,
                            ItemType.LEFT_IMAGE_MESSAGE,
                            messageData.members
                        )
                    )

                }
            } else if (messageData.type == "TIMELINE" || messageData.type == "JOINMEMBER" || messageData.type == "EXITROOM") {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)

                chatAdapter.arrayList.add(
                    0,
                    ChatItem(
                        messageData.from, messageData.thumbnailImage, messageData.content,
                        date, ItemType.CENTER_MESSAGE,
                        messageData.members
                    )
                )


            } else if (messageData.from == userName) {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)



                chatAdapter.arrayList.add(
                    0,
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.RIGHT_MESSAGE,
                        messageData.members
                    )
                )

            } else {
                val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss")
                val convertedCurrentDate = sdf.parse(messageData.sendTime)
                val date = SimpleDateFormat("a hh:mm").format(convertedCurrentDate)


                chatAdapter.arrayList.add(
                    0,
                    ChatItem(
                        messageData.from,
                        messageData.thumbnailImage,
                        messageData.content,
                        date,
                        ItemType.LEFT_MESSAGE,
                        messageData.members
                    )
                )

            }

        })

    }

    fun timelimeCheck(type: String? = null) {


        val retrofit =
            Retrofit.Builder()
                .baseUrl("http://52.78.107.230/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.timelineCheck("datetime", chatroomRoomId).enqueue(object : Callback<member> {
            override fun onResponse(call: Call<member>, response: Response<member>) {

                if (response.body()!!.dateline) {

                    members = response.body()!!.members
                    timeLineadd()
                    if (type == null) {
                        sendMessage()
                    } else {
                        ImageUpload(type)
                    }
                } else {

                    members = response.body()!!.members

                    if (type == null) {
                        sendMessage()
                    } else {
                        ImageUpload(type)
                    }
                }
            }

            override fun onFailure(call: Call<member>, t: Throwable) {

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
                    chatroomRoomId,
                    "SERVER", "SERVER",
                    "SERVER", members
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
        server.pagination(chatroomRoomId, pagenum, MainActivity.user_table_id)
            .enqueue(object : Callback<paginationData> {
                override fun onResponse(
                    call: Call<paginationData>,
                    response: Response<paginationData>
                ) {
                    if (response.body() != null) {
                        Log.e("로드 실행", "로드")
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
                            Log.e("로드 메세지끝", requestPage.toString())
                        }
                    }

                }

                override fun onFailure(call: Call<paginationData>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun messageRead(pagenum: Int) {
        val retrofit =
            Retrofit.Builder()
                .baseUrl("http://52.78.107.230/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.paginationRead(chatroomRoomId, pagenum, MainActivity.user_table_id)
            .enqueue(object : Callback<paginationData> {
                override fun onResponse(
                    call: Call<paginationData>,
                    response: Response<paginationData>
                ) {

                    chatAdapter.arrayList.clear()

                    if (response.body() != null) {
                        val list: paginationData? = response.body()
                        if (list!!.success) {
                            Log.e("로드메시지서버", "로드메시지서버")
                            for (index in list.ChatLogList.indices) {
                                ReadChat(list.ChatLogList[index])


                            }
                            chatAdapter.notifyDataSetChanged()
                            if (firstLoading) {
                                binding.groupChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                                firstLoading = false
                            }
                        } else {
                            requestPage = false
                            Log.e("로드 메세지끝", requestPage.toString())
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

    private fun joinMember() {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java).joinRoomMember(chatroomRoomId)
            .enqueue(object : Callback<ArrayList<LoadRoomUsers>> {
                override fun onResponse(
                    call: Call<ArrayList<LoadRoomUsers>>,
                    response: Response<ArrayList<LoadRoomUsers>>
                ) {

                    val users: ArrayList<LoadRoomUsers>? = response.body()
                    if (users != null) {
                        userList =
                            ChatRoomUserListRCVAdapter(this@ChatRoomActivity, users, hostName)
                        binding.userListRCV.adapter = userList
                        userList.notifyDataSetChanged()


                    }
                }

                override fun onFailure(call: Call<ArrayList<LoadRoomUsers>>, t: Throwable) {

                }
            })
    }

    private fun hostSubscriptionCheck() {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java).hostSubscriptionCheck(chatroomRoomId)
            .enqueue(object : Callback<ChatRoomSubscriptionResult> {
                override fun onResponse(
                    call: Call<ChatRoomSubscriptionResult>,
                    response: Response<ChatRoomSubscriptionResult>
                ) {
                    val list: ChatRoomSubscriptionResult? = response.body()
                    if (list!!.success && chatroomHostName == MainActivity.loginUserNickname) {
                        for (item in list.userList) {
                            if (item.status == 0) {
                                binding.chatRoomNewSubscriptionCircle.visibility = View.VISIBLE
                                notiIcon.visibility = View.VISIBLE
                            } else {
                                binding.chatRoomNewSubscriptionCircle.visibility = View.INVISIBLE
                                notiIcon.visibility = View.GONE
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<ChatRoomSubscriptionResult>, t: Throwable) {

                }
            })
    }

    fun getMapImage(x: Double?, y: Double?, placeName: String?, address: String) {
        var w = 400
        var h = 400
        var center = "126.978082,37.565577"
        var place: String? = null
        var marker: String? = null
        if (x != null && y != null) {
            center = "$x $y"
            place = "|label:$placeName"
            marker =
                "type:t${place}|size:mid|pos:$x $y|viewSizeRatio:2.0"
        }


        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java).getStaticMap(
            "kqfai8b97u",
            "NyaUzYcb3IWf1GKPNFDTYJTHIg9SUNtciSstiv5m",
            w,
            h,
            14,
            "basic",
            marker,
            center,
            2
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val input: InputStream = response.body()!!.byteStream()
//                val bufferedInputStream = BufferedInputStream(input)
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
                binding.RoomInfoMapImageView.setImageBitmap(bitmap)
                binding.RoomInfoMapImageView.setOnClickListener {


                    val url =
                        "nmap://place?lat=${y}&lng=${x}&name=${placeName}\n\n${address}&appname=${packageName}"
//                    val url = "nmap://search?query=${placeName}&appname=${packageName}"

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)

                    val list = packageManager.queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                    if (list == null || list.isEmpty()) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://map.naver.com/?query=$address")
                            )
                        )
                    } else {
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StaticMap", "실패")
            }
        })


    }

    fun roomInfoLoad() {
        roomId = intent.getStringExtra("roomId")!!
        val title = intent.getStringExtra("title")
        val info = intent.getStringExtra("info")
        hostName = intent.getStringExtra("hostName")!!
        val address = intent.getStringExtra("address")!!
        val date = intent.getStringExtra("date")
        val shopName = intent.getStringExtra("shopName")
        val roomStatus = intent.getDoubleExtra("roomStatus", 0.0);
        val nowNumOfPeople = intent.getStringExtra("nowNumOfPeople")
        val numOfPeople = intent.getStringExtra("numOfPeople")
        val keyWords = intent.getStringExtra("keyWords")
        val imageUrl = intent.getStringExtra("imageUrl")
        val join = intent.getStringExtra("join")
        val mapX = intent.getDoubleExtra("mapX", 0.0)
        val mapY = intent.getDoubleExtra("mapY", 0.0)

        if (roomStatus > 5) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            binding.RoomInformationStatus.text = "모집중"
        } else if (roomStatus > 0.9) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            val text: String = getString(R.string.room_status_imminent_time)
            binding.RoomInformationStatus.text =
                String.format(text, Math.round(roomStatus).toInt())

        } else if (roomStatus < 0.9 && roomStatus > 0.0) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline_imminent)
            binding.RoomInformationStatus.text = "임박"

        } else if (roomStatus < 0) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            binding.RoomInformationStatus.text = "마감"
        }

        binding.RoomInfomationDate.text = date
        binding.RoomInfoShopName.text = shopName
        binding.RoomInformationCategoryTitleTextview.text = title
        binding.RoomInformationCategoryIntroduceTextview.text = info
        binding.RoomInformationCategoryNumOfPeopleTextview.text =
            "${nowNumOfPeople}/${numOfPeople}명"
        binding.RoomInformationCategoryAddressTextview.text = address

        getMapImage(mapX, mapY, shopName, address)
        joinMember()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var check_result = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[2]
                    )
                ) {
                    Toast.makeText(
                        this,
                        "권한 설정이 거부되었습니다.\n앱을 사용하시려면 다시 실행해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n설정에서 권한을 허용해야 합니다..", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun exitRoom() {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
            .exitRoom(
                chatroomRoomId,
                MainActivity.user_table_id.toString(),
                MainActivity.loginUserNickname
            )
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.body() == "true") {
                        socket.emit(
                            "outRoom", gson.toJson(
                                MessageData(
                                    "EXITROOM",
                                    "EXITROOM",
                                    chatroomRoomId,
                                    MainActivity.loginUserNickname, "SERVER",
                                    "SERVER", members
                                )
                            )
                        )

                        onBackPressed()
                        Handler().postDelayed(Runnable { onBackPressed() }, 500)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })
    }

    private fun roomStateCheck() {

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java).roomStatusTime(roomId)
            .enqueue(object : Callback<Double> {
                override fun onResponse(call: Call<Double>, response: Response<Double>) {
                    if (response.body()!! < 1.0 && response.body()!! > 0.0) {
                        val intent =
                            Intent(this@ChatRoomActivity, GroupChatLocationMapActivity::class.java)
                        intent.putExtra("roomId", roomId)
                        startActivity(intent)
                    } else if (response.body()!! < 0.0) {
                        var builder = AlertDialog.Builder(this@ChatRoomActivity)
                        builder.setMessage("마감시간 이후에 위치를 조회 할 수 없습니다.")
                        builder.setPositiveButton(
                            "확인", null
                        )
                        builder.show()
                    } else {
                        var builder = AlertDialog.Builder(this@ChatRoomActivity)
                        builder.setMessage("약속시간 1시간 전부터 위치를 멤버의 위치를 조회 할 수 있습니다.")
                        builder.setPositiveButton(
                            "확인", null
                        )
                        builder.show()
                    }
                }

                override fun onFailure(call: Call<Double>, t: Throwable) {

                }

            })

    }

    fun dpToPxHeight(valueInDp: Float): Float {
        val metrics: DisplayMetrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    fun getKeyboardHeight(targetView: View, rootView: View): Int {
        var originHeight = -1

        if (targetView.getHeight() > originHeight) {
            originHeight = targetView.getHeight();
        }

        val visibleFrameSize = Rect();
        rootView.getWindowVisibleDisplayFrame(visibleFrameSize);

        val visibleFrameHeight = visibleFrameSize.bottom - visibleFrameSize.top;
        val keyboardHeight = originHeight - visibleFrameHeight;

        return keyboardHeight;
    }

    fun readMessage() {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.readMessage(chatroomRoomId, MainActivity.user_table_id)
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.body() == "true") {
                        val layoutManager =
                            LinearLayoutManager::class.java.cast(binding.groupChatRecyclerView.layoutManager)
                        pagenum = layoutManager.itemCount
                        messageRead(pagenum)
                    }


                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                }
            })
    }

    fun ImageUpload(imageUri: String?) {

//        val uriPathHelper = UserRegisterActivity.URIPathHelper()
//        var filePath = imageUri?.let { uriPathHelper.getPath(this, it) }

        //creating a file
        val file = File(imageUri)

        //이미지의 확장자를 구한다.
        val extension = MimeTypeMap.getFileExtensionFromUrl(imageUri.toString())


        var fileName = MainActivity.user_table_id.toString() + "." + extension
        var requestBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        var body: MultipartBody.Part =
            MultipartBody.Part.createFormData("uploaded_file", fileName, requestBody)


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(createOkHttpClient())
                .build()

        //creating our api

        var server = retrofit.create(RoomAPI::class.java)


        server.groupChatImageSend(body, roomId, MainActivity.user_table_id, userName)
            .enqueue(object : Callback<ChatImageSendingData> {
                override fun onFailure(call: Call<ChatImageSendingData>, t: Throwable) {
                    t.message?.let { Log.d("레트로핏 결과1", it) }
                }

                override fun onResponse(
                    call: Call<ChatImageSendingData>,
                    response: Response<ChatImageSendingData>
                ) {
                    if (response?.isSuccessful) {
                        Log.d("이미지를 업로드했습니다", "" + response?.body().toString())

                        var items: ChatImageSendingData = response.body()!!
                        if (items != null) {
                            if (items.success) {
                                if (!socket.connected()) {
                                    socket.connect()
                                    socket.emit(
                                        "newMessage", gson.toJson(
                                            MessageData(
                                                "IMAGE",
                                                userName,
                                                chatroomRoomId,
                                                items.ImageName,
                                                thumbnailImage,
                                                null,
                                                members
                                            )
                                        )
                                    )
                                } else {
                                    socket.emit(
                                        "newMessage", gson.toJson(
                                            MessageData(
                                                "IMAGE",
                                                userName,
                                                chatroomRoomId,
                                                items.ImageName,
                                                thumbnailImage,
                                                null,
                                                members
                                            )
                                        )
                                    )
                                }
                            }
                        }


                        //내가 보내는 것이다.
                        //dm_log_tb_id,sendtime,message_check는 서버에서 처리할 것이다.


                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "Some error occurred...",
                            Toast.LENGTH_LONG
                        ).show();
                        Log.d("이미지업로드실패", "" + response?.body().toString())
                    }
                }
            })
    }
}