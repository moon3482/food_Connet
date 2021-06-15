package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abled_food_connect.R

class ChatGroupFragment : Fragment() {

    companion object {
        const val TAG: String = "그룹채팅 프래그먼트 로그"
        fun newInstance(): ChatGroupFragment {
            return ChatGroupFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChatGroupFragment.TAG,"그룹채팅 프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ChatGroupFragment.TAG,"그룹채팅 프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_group, container, false)



        return view
    }

}