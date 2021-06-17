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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileModifyActivity

class MyPageFragment:Fragment() {
    companion object{
        const val TAG : String = "마이페이지 프래그먼트 로그"
        fun newInstance(): MyPageFragment{
            return MyPageFragment()
        }
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
            builder.setIcon(R.mipmap.ic_launcher)

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


}