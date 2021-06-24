package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.adapter.MeetingUserEvaluationRvAdapter
import com.example.abled_food_connect.adapter.ReviewParentPageCommentRvAdapter
import com.example.abled_food_connect.data.ChattingFragmentDmRvDataItem
import com.example.abled_food_connect.data.MeetingEvaluationUserListRvData
import com.example.abled_food_connect.data.MeetingEvaluationUserListRvDataItem
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.databinding.ActivityMeetingUserEvaluationBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MeetingUserEvaluationActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityMeetingUserEvaluationBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private var room_id : Int = 0

    lateinit var meetingEndUserListRv :RecyclerView

    var userList =  ArrayList<MeetingEvaluationUserListRvDataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_user_evaluation)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityMeetingUserEvaluationBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "모임평가"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        meetingEndUserListRv = binding.meetingEndUserListRv
        meetingEndUserListRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        meetingEndUserListRv.setHasFixedSize(true)


        room_id = intent.getIntExtra("room_id", 0)

        Log.d("room_id", room_id.toString())

        MeetingEnduserLoading(room_id)

    }


    fun MeetingEnduserLoading(room_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.MeetingEvaluationUserListRvInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val user_profile_data_get = api.meeting_evaluation_user_list_get(room_id)


        user_profile_data_get.enqueue(object : Callback<MeetingEvaluationUserListRvData> {
            override fun onResponse(
                call: Call<MeetingEvaluationUserListRvData>,
                response: Response<MeetingEvaluationUserListRvData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : MeetingEvaluationUserListRvData? =  response.body()

                var dbGetUserList = items!!.userList as ArrayList<MeetingEvaluationUserListRvDataItem>


                //호스트 게스트 정렬 호스트인 경우 맨 위에 뜰 수 있게 한다.
                dbGetUserList.sortWith(object: Comparator<MeetingEvaluationUserListRvDataItem>{
                    override fun compare(p1: MeetingEvaluationUserListRvDataItem, p2: MeetingEvaluationUserListRvDataItem): Int = when {
                        p1.is_host > p2.is_host -> -1
                        p1.is_host == p2.is_host -> 0
                        else -> 1
                    }
                })

                userList.clear()

                for(i in 0..dbGetUserList.size - 1){
                    if(dbGetUserList.get(i).user_nickname!=MainActivity.loginUserNickname){
                        userList.add(dbGetUserList.get(i))
                    }
                }




               var mAdapter =  MeetingUserEvaluationRvAdapter(userList)
                mAdapter.notifyDataSetChanged()
                meetingEndUserListRv.adapter = mAdapter



            }

            override fun onFailure(call: Call<MeetingEvaluationUserListRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
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
}