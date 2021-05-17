package com.example.abled_food_connect.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.Adapter.MainFragmentAdapter
import com.example.abled_food_connect.Data.MainFragmentItemData
import com.example.abled_food_connect.R


class MainFragment : Fragment() {
    private val mainFragmentListArray: ArrayList<MainFragmentItemData> = ArrayList()
    lateinit var recyclerView: RecyclerView

    companion object {
        const val TAG: String = "홈 프래그먼트 로그"
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "메인프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragments, container, false)
        mainFragmentListArray.add(MainFragmentItemData("제목","정보",0, "","","","","",0,0))
        mainFragmentListArray.add(MainFragmentItemData("제목1","정보1",0, "","","","","",0,0))
        mainFragmentListArray.add(MainFragmentItemData("제목2","정보2",0, "","","","","",0,0))
        mainFragmentListArray.add(MainFragmentItemData("제목3","정보3",0, "","","","","",0,0))
        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","",0,0))



        recyclerView = view.findViewById(R.id.mainRcv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MainFragmentAdapter(requireContext(),mainFragmentListArray)
        return view
    }
}
