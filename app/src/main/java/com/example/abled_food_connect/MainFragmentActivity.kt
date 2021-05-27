package com.example.abled_food_connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.abled_food_connect.fragments.*
import com.example.abled_food_connect.retrofit.RoomAPI
import com.example.abled_food_connect.databinding.ActivityMainFragmentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainFragmentActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainFragmentBinding.inflate(layoutInflater) }
    private lateinit var mainFragment: MainFragment
    private lateinit var reviewFragment: ReviewFragment
    private lateinit var rankingFragment: RankingFragment
    private lateinit var chatingFragment: ChatingFragment
    private lateinit var myPageFragment: MyPageFragment

    //태그 생성
    companion object obuserid {
        const val TAG: String = "홈 액티비티 로그"


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(TAG, "홈액티비티 onCreate()")


        //바텀네비게이션 클릭리스너 달기
        binding.bottomNav.setOnNavigationItemSelectedListener(
            onBottomOnNavigationItemSelectedListener
        )

        //툴바 생성 및 타이틀 이름
        setSupportActionBar(binding.maintoolbar)
        val tb = supportActionBar!!
        tb.setTitle("홈")

        //프래그먼트 인스턴스화
        mainFragment = MainFragment.newInstance()

        //첫화면에서 리뷰 플로팅 버튼 숨김
        binding.mainFragmentCreateReviewBtn.hide()

        //프래그먼트 매니저에 메인프래그먼트 등록
        supportFragmentManager.beginTransaction().setCustomAnimations(
            R.animator.fade_in,
            R.animator.fade_out,
            R.animator.fade_in,
            R.animator.fade_out
        ).add(R.id.view, mainFragment).commit()

        //방만들기 플로팅 버튼 클릭리스너
        binding.mainFragmentCreateRoomBtn.setOnClickListener {
            moveToCreateRoomActivity()
//            resistRoom()
//            test()

        }
        binding.mainFragmentCreateReviewBtn.setOnClickListener(
            View.OnClickListener
        {
            //바텀네비게이션 프래그먼트 셀렉트 리스너
            val nextIntent = Intent(this, ReviewWriting::class.java)
            startActivity(nextIntent)

        })
    }




    override fun onStart() {
        super.onStart()
        if(intent.hasExtra("review")){
            reviewFragment = ReviewFragment.newInstance()
            supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, reviewFragment)
                .commit()
            binding.bottomNav.selectedItemId = R.id.menu_review
        }else{  mainFragment = MainFragment.newInstance()
            supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, mainFragment)
                .commit()}

    }
    //바텀네비게이션 프래그먼트 셀렉트 리스너
    private val onBottomOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            showFloatingButtonVisible(it.itemId)
            when (it.itemId) {

                R.id.menu_home -> {
                    Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")

                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("홈")

                    mainFragment = MainFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, mainFragment)
                        .commit()
                }
                R.id.menu_review -> {
                    Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("리뷰")
                    binding.mainFragmentCreateRoomBtn.hide()
                    reviewFragment = ReviewFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, reviewFragment)
                        .commit()
                }
                R.id.menu_ranking -> {
                    Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("랭킹")
                    binding.mainFragmentCreateRoomBtn.hide()
                    rankingFragment = RankingFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, rankingFragment)
                        .commit()
                }
                R.id.menu_chat -> {
                    Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("채팅")
                    binding.mainFragmentCreateRoomBtn.hide()
                    chatingFragment = ChatingFragment.newInstance()
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, chatingFragment)
                        .commit()
                }
                R.id.menu_mypage -> {
                    Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")
                    myPageFragment = MyPageFragment.newInstance()
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("마이페이지")
                    binding.mainFragmentCreateRoomBtn.hide()
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.animator.fade_in,R.animator.fade_out,R.animator.fade_in,R.animator.fade_out).replace(R.id.view, myPageFragment)
                        .commit()
                }

            }

            true
        }

         //프래그먼트에 따라 플로팅버튼 보여주기
    private fun showFloatingButtonVisible(itemid: Int) {
        when (itemid) {
            R.id.menu_home -> {
                binding.mainFragmentCreateRoomBtn.show()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_review -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.show()
            }
            R.id.menu_ranking -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_chat -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_mypage -> {
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }

        }
    }

         /** 바텀네비게이션아이템 리스너 오버라이드 2021-05-17부로 코드중복으로 필요없음.
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        TODO("Not yet implemented")
//        val tb: Toolbar = findViewById(R.id.maintoolbar)
//        tb.visibility = View.VISIBLE
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//
//        when (item.itemId) {
//            R.id.menu_home -> {
//                Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")
//                val fragmentHoem = MainFragment()
//
//                transaction.replace(R.id.view, fragmentHoem, "home")
//            }
//            R.id.menu_review -> {
//                Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")
//
//            }
//            R.id.menu_ranking -> {
//                Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")
//
//            }
//            R.id.menu_chat -> {
//                Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")
//
//            }
//            R.id.menu_mypage -> {
//                Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")
//
//            }
//
//        }
//    }
*/

         /** test 방생성 코트 2021-24일부로 방생성 기능 구현으로 주석처리
//        방 등록 리퀘스트 메소드
//         private fun resistRoom() {
//        val gson: Gson = GsonBuilder()
//            .setLenient()
//            .create()
//
//        val retrofit =
//            Retrofit.Builder()
//                .baseUrl("http://3.37.36.188/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//
//        val server = retrofit.create(RoomAPI::class.java)
//
//        server.createRoom("하이",
//            "바이2222",
//            "5",
//            "2021-05-18",
//            "18:00:00",
//            "서울시관악구",
//            "빵집",
//            "빵집",
//            "male",
//            "22","33","호스트네임")
//            .enqueue(object :Callback<String>{
//            override fun onResponse(
//                call: Call<String>,
//                response: Response<String>
//            ) {
//                if(response.isSuccessful)
//                    Log.e("성공",response.body().toString())
//                else
//                    Log.e("실패",response.body().toString())
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//
//                }
//
//        })
    }*/
         fun moveToCreateRoomActivity(){
             val moveToCreateRoomActivityIntent = Intent(this@MainFragmentActivity,CreateRoomActivity::class.java)
             startActivity(moveToCreateRoomActivityIntent)
         }


}