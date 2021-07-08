package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.ChattingFragmentDmRvAdapter
import com.example.abled_food_connect.adapter.RankingFragmentRvAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RankingFragment:Fragment() {

    var rankingFragmentRvDataArrayList = ArrayList<RankingFragmentRvDataItem>()
    lateinit var rankingFragmentRv : RecyclerView

    companion object{
        const val TAG : String = "랭킹 프래그먼트 로그"
        fun newInstance(): RankingFragment{
            return RankingFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"랭킹프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"랭킹프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.ranking_fragments, container, false)


        rankingFragmentRv = view.findViewById(R.id.rankingFragmentRv)


        rankingFragmentRv.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rankingFragmentRv.setHasFixedSize(false)


//        val rankingArrayList = arrayListOf(
//            RankingFragmentRvDataItem(1,"1시즌",83,"닉네임1","이미지주소",3000,1,"골드","티어이미지"),
//            RankingFragmentRvDataItem(1,"1시즌",83,"닉네임2","이미지주소",3000,1,"골드","티어이미지"),
//            RankingFragmentRvDataItem(1,"1시즌",83,"닉네임3","이미지주소",3000,1,"골드","티어이미지"),
//            RankingFragmentRvDataItem(1,"1시즌",83,"닉네임4","이미지주소",3000,1,"골드","티어이미지")
//        )
//
//        val mAdapter =  RankingFragmentRvAdapter(rankingArrayList)
//        rankingFragmentRv.adapter = mAdapter


        totalPointListLoading()


        return view
    }




    fun totalPointListLoading(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.rankingFragmentRvTotalPointListGetInterface::class.java)

        //채팅내역을 가져온다
        val dm_message_list_get = api.total_point_list_get(MainActivity.user_table_id)


        dm_message_list_get.enqueue(object : Callback<RankingFragmentRvData> {
            override fun onResponse(
                call: Call<RankingFragmentRvData>,
                response: Response<RankingFragmentRvData>
            ) {
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "DM_ROOM 조회결과 : ${response.body().toString()}")

                var items : RankingFragmentRvData? =  response.body()




                if(!rankingFragmentRvDataArrayList.isEmpty()){
                    Log.d("RankingFragment.TAG", "실행됨")
                    rankingFragmentRvDataArrayList.clear()
                }


                rankingFragmentRvDataArrayList = items!!.rankingList as ArrayList<RankingFragmentRvDataItem>

                rankingFragmentRvDataArrayList.add(0,RankingFragmentRvDataItem(0,"",0,"유저","","포인트","순위","티어","",1))


                Log.d("목록나와라", rankingFragmentRvDataArrayList.toString())




                val mAdapter =  RankingFragmentRvAdapter(rankingFragmentRvDataArrayList)
                rankingFragmentRv.adapter = mAdapter





            }

            override fun onFailure(call: Call<RankingFragmentRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }
}