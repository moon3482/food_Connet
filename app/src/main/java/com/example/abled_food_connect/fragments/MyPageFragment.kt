package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abled_food_connect.R

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
        return inflater.inflate(R.layout.mypage_fragments, container, false)
    }
}