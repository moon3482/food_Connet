package com.example.abled_food_connect.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileModifyActivity
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageFragment:Fragment() {


    lateinit var userProfileIv : ImageView
    lateinit var userProfileNicNameTv : TextView
    lateinit var userProfileIntroductionTv : TextView



    companion object{
        const val TAG : String = "마이페이지 프래그먼트 로그"
        fun newInstance(): MyPageFragment{
            return MyPageFragment()
        }
    }

    override fun onResume() {
        super.onResume()

        //작성자 프로필
        Glide.with(userProfileIv.context)
            .load(getString(R.string.http_request_base_url)+MainActivity.userThumbnailImage)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(userProfileIv)

        userProfileNicNameTv.text = MainActivity.loginUserNickname
        //유저 정보를 DB에서 가져온다.
        userProfileLoading(MainActivity.user_table_id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"마이페이지 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"마이페이지 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mypage_fragments, container, false)


        userProfileIv = view.findViewById<ImageView>(R.id.userProfileIv)
        userProfileNicNameTv = view.findViewById<TextView>(R.id.userProfileNicNameTv)
        userProfileIntroductionTv = view.findViewById<TextView>(R.id.userProfileIntroductionTv)




        val toUserProfileModifyActivityBtn = view.findViewById<TextView>(R.id.toUserProfileModifyActivityBtn)

        toUserProfileModifyActivityBtn.setOnClickListener{
            val intent = Intent(context, UserProfileModifyActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = view.findViewById<LinearLayout>(R.id.logoutBtn)
        // 로그아웃
        logoutBtn.setOnClickListener {
            var builder = AlertDialog.Builder(logoutBtn.context)
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃 하시겠습니까?")


            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->


                            activity?.let{

                                logout()

                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else { intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                activity!!.finish()


                            }

                        DialogInterface.BUTTON_NEGATIVE ->
                            Log.d(TAG, "로그아웃 취소")
                    }
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)


            builder.show()
        }




        return view

    }


    fun logout(){
        val pref = activity?.getSharedPreferences("pref_user_data",0)
        val edit = pref?.edit()
        if (edit != null) {
            edit.putBoolean("login_check",false)
            edit.apply()//저장완료
        }


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
                Log.d(ReviewFragment.TAG, "프로필 정보 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "프로필 정보 : ${response.body().toString()}")

                var items : UserProfileData? =  response.body()







                if(items!!.introduction == null||items!!.introduction.length<1){
                    userProfileIntroductionTv.text = "자기소개가 없습니다."
                }else{
                    userProfileIntroductionTv.text = items!!.introduction
                }





            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d("유저정보가져오기실패?", "실패 : $t")
            }
        })
    }


}