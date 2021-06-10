package com.example.abled_food_connect

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.databinding.ActivityUserProfileBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserProfileBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    private var clicked_user_tb_id : Int = 0
    private lateinit var clicked_user_NicName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_user_profile)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserProfileBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.


        setSupportActionBar(binding.userProfileToolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.userProfileToolbar.title = "프로필"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //어떤 유저를 선택했는지 이전엑티비티에서 유저 테이블 아이디를 받아온다.
        clicked_user_tb_id = intent.getIntExtra("writer_user_tb_id",0)

        //유저 정보를 가져온다.
        userProfileLoading(clicked_user_tb_id)



        binding.toMoveDirectMessageActivityBtn.setOnClickListener(View.OnClickListener {
            var toDirectMessageActivity : Intent = Intent(applicationContext, DirectMessageActivity::class.java)
            toDirectMessageActivity.putExtra("writer_user_tb_id", clicked_user_tb_id)
            toDirectMessageActivity.putExtra("clicked_user_NicName", clicked_user_NicName)
            ContextCompat.startActivity(applicationContext, toDirectMessageActivity, null)
        })


        binding.toMoveWrittenReviewListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileClickedReviewGridListActivity : Intent = Intent(applicationContext, UserProfileClickedReviewGridListActivity::class.java)
            toUserProfileClickedReviewGridListActivity.putExtra("writer_user_tb_id", clicked_user_tb_id)
            ContextCompat.startActivity(applicationContext, toUserProfileClickedReviewGridListActivity, null)
        })

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


                //작성자 프로필
                Glide.with(applicationContext)
                    .load(getString(R.string.http_request_base_url)+items!!.profile_image)
                    .circleCrop()
                    .into(binding.userProfileIv)

                binding.userProfileNicNameTv.text = items!!.nick_name
                clicked_user_NicName = items!!.nick_name

                if(items!!.introduction == null) {
                    binding.userProfileIntroductionTv.text = "안녕하세요. ${items!!.nick_name}입니다."
                }

                binding.reviewTitleAndReviewCountTv.text= "작성한 리뷰 ${items.review_count}개"

            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
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