package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.data.Items
import com.example.abled_food_connect.data.ReviewFragmentItemData
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
    lateinit var recyclerView: RecyclerView
    lateinit var textView: TextView
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

        textView = view.findViewById(R.id.whatTv)

        textView.setText("tt")



        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.36.188/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ReviewFragRvUsingInterface::class.java)
        val callGetSearchNews = api.review_frag_rv_using_interface()


        callGetSearchNews.enqueue(object : Callback<ReviewFragmentItemData> {
            override fun onResponse(
                call: Call<ReviewFragmentItemData>,
                response: Response<ReviewFragmentItemData>
            ) {
                Log.d(TAG, "성공 : ${response.raw()}")
                Log.d(TAG, "성공 : ${response.body().toString()}")

                var items : ReviewFragmentItemData? =  response.body()


                Log.d(TAG, "성공 : ${items!!.roomList}")


                var a : List<Items> = items!!.roomList

                for(i in a.indices){
                    println(a.get(i).title);
                }



            }

            override fun onFailure(call: Call<ReviewFragmentItemData>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })


        return view
    }



}