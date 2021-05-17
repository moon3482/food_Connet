package com.example.abled_food_connect.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.Adapter.MainFragmentAdapter
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.R

class ReviewFragment:Fragment() {
    private val reviewFragmentListArray: ArrayList<MainFragmentItemData> = ArrayList()
    lateinit var recyclerView: RecyclerView
    companion object{
        const val TAG : String = "리뷰 프래그먼트 로그"
        fun newInstance(): ReviewFragment{
            return ReviewFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"리뷰프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"리뷰프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_fragment, container, false)
        reviewFragmentListArray.add(MainFragmentItemData("제목","정보",0, "","","","","","male",0,20,"나야",0))
        reviewFragmentListArray.add(MainFragmentItemData("제목1","정보1",0, "","","","","","male",0,20,"나야",1))
        reviewFragmentListArray.add(MainFragmentItemData("제목2","정보2",0, "","","","","","male",0,20,"나야",2))
        reviewFragmentListArray.add(MainFragmentItemData("제목3","정보3",0, "","","","","","male",0,20,"나야",6))
        reviewFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",9))


        recyclerView = view.findViewById(R.id.reviewRcv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MainFragmentAdapter(requireContext(),reviewFragmentListArray)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context,LinearLayoutManager(this.context).orientation))
        return view
    }
}