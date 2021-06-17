package com.example.abled_food_connect.fragments

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.data.ReviewFragmentLoadingData
import com.example.abled_food_connect.data.ReviewFragmentLodingDataItem
import com.example.abled_food_connect.adapter.ReviewFragmentGridViewAdapter
import com.example.abled_food_connect.interfaces.ReviewFragRvUsingInterface
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.MainFragmentItemData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ReviewFragment:Fragment() {
    private val reviewFragmentListArray: ArrayList<MainFragmentItemData> = ArrayList()


    //리사이클러뷰 어래이리스트
    private lateinit var gridView_arrayList : ArrayList<ReviewFragmentLodingDataItem>

    //리사이클러뷰
    lateinit var rv : RecyclerView


    //그리드뷰 어댑터
    lateinit var mAdapter : ReviewFragmentGridViewAdapter


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



        reviewDbLoading()




        rv = view.findViewById<RecyclerView>(R.id.rv)
        //rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = GridLayoutManager(context,3)


        //리사이클러뷰 구분선


        rv.addItemDecoration(HorizontalItemDecorator(5))
        rv.addItemDecoration(VerticalItemDecorator(5))


        rv.setHasFixedSize(true)





        return view
    }


    fun reviewDbLoading(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ReviewFragRvUsingInterface::class.java)
        val callGetSearchNews = api.review_frag_rv_using_interface()


        callGetSearchNews.enqueue(object : Callback<ReviewFragmentLoadingData> {
            override fun onResponse(
                call: Call<ReviewFragmentLoadingData>,
                response: Response<ReviewFragmentLoadingData>
            ) {
                Log.d(TAG, "성공 : ${response.raw()}")
                Log.d(TAG, "성공 : ${response.body().toString()}")

                var items : ReviewFragmentLoadingData? =  response.body()


                Log.d(TAG, "성공 : ${items!!.roomList}")


                gridView_arrayList = items!!.roomList as ArrayList<ReviewFragmentLodingDataItem>



                mAdapter =  ReviewFragmentGridViewAdapter(gridView_arrayList)
                rv.adapter = mAdapter



            }

            override fun onFailure(call: Call<ReviewFragmentLoadingData>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })
    }

    class HorizontalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

        @Override
        override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.left = divHeight
            outRect.right = divHeight
        }
    }

    class VerticalItemDecorator(private val divHeight : Int) : RecyclerView.ItemDecoration() {

        @Override
        override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = divHeight
            outRect.bottom = divHeight
        }
    }



}