package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.UserProfileBadgeListRvAdapter
import com.example.abled_food_connect.data.UserProfileBadgeListData
import com.example.abled_food_connect.data.UserProfileBadgeListDataItem
import com.example.abled_food_connect.databinding.ActivityUserProfileBadgeListBinding
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class UserProfileBadgeListActivity : AppCompatActivity() {

    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileBadgeListBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    lateinit var userNicName : String

    var user_tb_id = 0


    lateinit var userProfileBadgeListRv : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_badge_list)


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileBadgeListBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "뱃지보기"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if(intent.getStringExtra("user_nicname") != null){
            userNicName = intent.getStringExtra("user_nicname")!!
            user_tb_id = intent.getIntExtra("user_tb_id",0)
            Log.d("userNicName", userNicName)
        }


        binding.topNoticeTv.text = "${userNicName}님의 뱃지 리스트"


        userProfileBadgeListRv = binding.userProfileBadgeListRv
        //rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userProfileBadgeListRv.layoutManager = GridLayoutManager(this,3)
        userProfileBadgeListRv.setHasFixedSize(true)



        if(intent.getStringExtra("user_nicname") != null){
            userNicName = intent.getStringExtra("user_nicname")!!
            user_tb_id = intent.getIntExtra("user_tb_id",0)
            badgeListLoading(user_tb_id,userNicName)
        }


    }


    fun badgeListLoading(user_tb_id:Int , user_nic_name : String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileBadgeListDataGetInterface::class.java)


        val data_get = api.user_profile_badge_list_data_get(user_tb_id,user_nic_name)


        data_get.enqueue(object : Callback<UserProfileBadgeListData> {
            override fun onResponse(
                call: Call<UserProfileBadgeListData>,
                response: Response<UserProfileBadgeListData>
            ) {
                Log.d("뱃지리스트", "뱃지 컨텐츠 : ${response.raw()}")
                Log.d("뱃지리스트", "뱃지 컨텐츠 : ${response.body().toString()}")

                var items : UserProfileBadgeListData? =  response.body()
                var badgeArrayList = ArrayList<UserProfileBadgeListDataItem>()

                badgeArrayList = items!!.badgeList as ArrayList<UserProfileBadgeListDataItem>

                val mAdapter =  UserProfileBadgeListRvAdapter(badgeArrayList)
                userProfileBadgeListRv.adapter = mAdapter




            }

            override fun onFailure(call: Call<UserProfileBadgeListData>, t: Throwable) {
                Log.d("뱃지리스트", "실패 : $t")
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