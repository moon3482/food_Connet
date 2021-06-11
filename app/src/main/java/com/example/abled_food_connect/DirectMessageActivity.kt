package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.DirectMessageRvAdapter
import com.example.abled_food_connect.data.DirectMessageData
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.databinding.ActivityDirectMessageBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import com.google.gson.GsonBuilder
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException

class DirectMessageActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityDirectMessageBinding? = null
    
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    //NodeJS 통신을 위한 전역변수 설정
    lateinit var mySocket : Socket
    lateinit var roomNumber : String
    var messageText : String = ""


    //내정보


    private var MyUserTableId : Int = MainActivity.user_table_id
    private var MyUserNicName : String = MainActivity.loginUserNickname
    private lateinit var MyProfileImage : String



    //상대정보
    //프로필 클릭한 상대의 유저 테이블 아이디
    //방 이름 및 DB 저장시 사용한다.
    private var clicked_user_tb_id : Int = 0
    private lateinit var clicked_user_NicName : String


    //gson
    var gson = GsonBuilder().create()


    //채팅 리사이클러뷰
    lateinit var directMessageRvAdapter: DirectMessageRvAdapter
    val direct_message_data_Arraylist = ArrayList<DirectMessageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_direct_message)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityDirectMessageBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 tv_message -> tvMessage 로 자동 변환 되었습니다.




        //어떤 상대를 클릭했는지 인텐트로 받아온다.
        clicked_user_tb_id = intent.getIntExtra("writer_user_tb_id",0)
        clicked_user_NicName = intent.getStringExtra("clicked_user_NicName")!!
        Log.d("상대의 id는?", clicked_user_tb_id.toString())


        //툴바
        setSupportActionBar(binding.directMessageToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.directMessageToolbar.title = "${clicked_user_NicName}님과의 대화"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //내 프로필 이미지 가져오기
        userProfileLoading(MainActivity.user_table_id)





        //방이름은 '유저테이블아이디+"and"+유저테이블아이디' 형식이다.
        //테이블 아이디 순서는 작은 것이 앞쪽에 오도록 한다.

        if(clicked_user_tb_id < MyUserTableId  ){
            roomNumber = clicked_user_tb_id.toString()+"and"+ MyUserTableId.toString()
        }else{
            roomNumber = MyUserTableId.toString() +"and"+ clicked_user_tb_id.toString()
        }



        try {
            mySocket = IO.socket("http://52.78.107.230:3000")
            //소켓이 접속할 uri을 설정한다.
        }catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        mySocket.connect() // 소켓연결


        //on은 서버로부터 받는 동작 수행.
        //emit은 보내는 동작수행
        mySocket.on(Socket.EVENT_CONNECT, onConnect)
        // 첫 연결이 되면 onConnect 메서드가 실행된다
        // onConnect는 닉네임과 방번호를 서버로 전달한다.


        //서버로부터 메시지를 받는다. "message_from_server"을 키값으로 사용한다.
        mySocket.on("message_from_server", fromServerMessage_Get)




        //채팅 리사이클러뷰




//        direct_message_data_Arraylist.apply {
//            add(DirectMessageData("11",81,"11nic","http://52.78.107.230/images/profile_image/sD44E7xt7IXL4HcmgV09OPMI1oH2.png","텍스트","Text"))
//            add(DirectMessageData("22",83,"22nic","http://52.78.107.230/review/upload/20210525_093837itemphoto0.jpg","텍스트","Text"))
//            add(DirectMessageData("33",81,"33nic","http://52.78.107.230/images/profile_image/sD44E7xt7IXL4HcmgV09OPMI1oH2.png","http://52.78.107.230/images/profile_image/sD44E7xt7IXL4HcmgV09OPMI1oH2.png","Image"))
//            add(DirectMessageData("44",83,"44nic","http://52.78.107.230/review/upload/20210525_093837itemphoto0.jpg","http://52.78.107.230/review/upload/20210525_093837itemphoto0.jpg","Image"))
//        }
//
//        directMessageRvAdapter =  DirectMessageRvAdapter(direct_message_data_Arraylist)
//        directMessageRvAdapter.notifyDataSetChanged()
//        rv_profile.adapter = directMessageRvAdapter





        //서버로 메세지를 전달한다. 메시지는 [방번호]@[아이디:메시지텍스트] 의 형태이다. @는 구분자이다.
        binding.button.setOnClickListener {
//            val msg = roomNumber+"@"+userNicname+":"+binding.editText.text.toString()
//            messageText = messageText + userNicname+":"+binding.editText.text.toString()+"\n"

            var g1 = DirectMessageData(roomNumber,MyUserTableId,MyUserNicName,MyProfileImage,binding.editText.text.toString(),"Text")
            var msg_json = gson.toJson(g1)

            direct_message_data_Arraylist.add(DirectMessageData(roomNumber,MyUserTableId,MyUserNicName,MyProfileImage,binding.editText.text.toString(),"Text"))

            mySocket.emit("message_from_client", msg_json)
            binding.editText.setText("")



            Log.d("어레이뭐있냐", direct_message_data_Arraylist.toString())
            runOnUiThread( {
                directMessageRvAdapter =  DirectMessageRvAdapter(direct_message_data_Arraylist)
                directMessageRvAdapter.notifyDataSetChanged()
                binding.directMessageChatListRv.adapter = directMessageRvAdapter
                binding.directMessageChatListRv.scrollToPosition(direct_message_data_Arraylist.size-1);
            });
        }




    }

    val onConnect: Emitter.Listener = Emitter.Listener {
        // onConnect는 닉네임과 방번호를 서버로 전달한다.
        //서버 측에서는 이 username을 whoIsON Array 에 추가를 할 것입니다.
        mySocket.emit("login", MyUserNicName)
        mySocket.emit("user_room_number", roomNumber)
        Log.d("Tag", "Socket is connected with ${MyUserNicName+roomNumber}")
    }

    val fromServerMessage_Get: Emitter.Listener = Emitter.Listener {
        //서버에서 도착한 메시지 받기.
        Log.d("Tag",  it[0].toString())

        val data = it[0] as JSONObject
        Log.d("Tag",  data.getString("roomName"))

        var roomName = data.getString("roomName")
        var user_tb_id = data.getInt("user_tb_id")
        var userNickname = data.getString("userNicName")
        var userProfileImage = data.getString("userProfileImage")
        var message = data.getString("message")
        var TextOrImage = data.getString("TextOrImage")


        direct_message_data_Arraylist.add(DirectMessageData(roomName,user_tb_id,userNickname,"http://52.78.107.230/"+userProfileImage,message,TextOrImage))

        //direct_message_data_Arraylist.apply {
//            add(DirectMessageData("11",81,"11nic","http://52.78.107.230/images/profile_image/sD44E7xt7IXL4HcmgV09OPMI1oH2.png","텍스트","Text"))
//            add(DirectMessageData("22",83,"22nic","http://52.78.107.230/review/upload/20210525_093837itemphoto0.jpg","텍스트","Text"))
//            add(DirectMessageData("33",81,"33nic","http://52.78.107.230/images/profile_image/sD44E7xt7IXL4HcmgV09OPMI1oH2.png","http://52.78.107.230/images/profile_image/sD44E7xt7IXL4HcmgV09OPMI1oH2.png","Image"))
//            add(DirectMessageData("44",83,"44nic","http://52.78.107.230/review/upload/20210525_093837itemphoto0.jpg","http://52.78.107.230/review/upload/20210525_093837itemphoto0.jpg","Image"))
//        }
//
//        directMessageRvAdapter =  DirectMessageRvAdapter(direct_message_data_Arraylist)
//        directMessageRvAdapter.notifyDataSetChanged()
//        rv_profile.adapter = directMessageRvAdapter



        runOnUiThread( {
            directMessageRvAdapter =  DirectMessageRvAdapter(direct_message_data_Arraylist)
            directMessageRvAdapter.notifyDataSetChanged()
            binding.directMessageChatListRv.adapter = directMessageRvAdapter
            binding.directMessageChatListRv.scrollToPosition(direct_message_data_Arraylist.size-1);
        });
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        mySocket.disconnect()
    }


    fun userProfileLoading(user_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileDataInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val user_profile_data_get = api.user_profile_data_get(user_tb_id)


        user_profile_data_get.enqueue(object : Callback<UserProfileData> {
            override fun onResponse(
                call: Call<UserProfileData>,
                response: Response<UserProfileData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : UserProfileData? =  response.body()


                //로그인한 유저의 프로필 이미지 가져오기

                MyProfileImage = items!!.profile_image

//                //작성자 프로필
//                Glide.with(applicationContext)
//                    .load(getString(R.string.http_request_base_url)+items!!.profile_image)
//                    .circleCrop()
//                    .into(binding.userProfileIv)
//
//                binding.userProfileNicNameTv.text = items!!.nick_name
//
//                if(items!!.introduction == null) {
//                    binding.userProfileIntroductionTv.text = "안녕하세요. ${items!!.nick_name}입니다."
//                }
//
//                binding.reviewTitleAndReviewCountTv.text= "작성한 리뷰 ${items.review_count}개"

            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }
}