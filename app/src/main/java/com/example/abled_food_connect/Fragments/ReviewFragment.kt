package com.example.abled_food_connect.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.Data.Items
import com.example.abled_food_connect.Data.MainFragmentItemData
import com.example.abled_food_connect.Data.ReviewFragmentItemData
import com.example.abled_food_connect.Interfaces.ReviewFragRvUsingInterface
import com.example.abled_food_connect.R
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


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





//        reviewFragmentListArray.add(MainFragmentItemData("제목","정보",0, "","","","","",0,0))
//        reviewFragmentListArray.add(MainFragmentItemData("제목1","정보1",0, "","","","","",0,0))
//        reviewFragmentListArray.add(MainFragmentItemData("제목2","정보2",0, "","","","","",0,0))
//        reviewFragmentListArray.add(MainFragmentItemData("제목3","정보3",0, "","","","","",0,0))
//        reviewFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","",0,0))
//
//
//
//        recyclerView = view.findViewById(R.id.reviewRcv) as RecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = MainFragmentAdapter(requireContext(),reviewFragmentListArray)
        return view
    }


//    object ApiClient {
//        private const val BASE_URL = "http://3.37.36.188/"
//        private var retrofit: Retrofit? = null
//        val apiClient: Retrofit?
//            get() {
//                val gson = GsonBuilder()
//                    .setLenient()
//                    .create()
//                if (retrofit == null) {
//                    retrofit = Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .addConverterFactory(ScalarsConverterFactory.create())
//                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .build()
//                }
//                return retrofit
//            }
//    }

//    private fun getRvList() {
//        val apiInterface: ReviewFragRvUsingInterface = apiClient!!.create(ReviewFragRvUsingInterface::class.java)
//        val call: Call<List<ReviewFragRvUsingInterface.profile>> = apiInterface.review_frag_rv_using_interface()
//        call.enqueue(object : Callback<List<ReviewFragRvUsingInterface.profile>?> {
//
//            override fun onResponse(
//                call: Call<ReviewFragRvUsingInterface.profile?>,
//                response: Response<List<ReviewFragRvUsingInterface.profile>?>
//            ) {
//                if (response.isSuccessful() && response.body() != null) {
//                    val getted_name: String = response.body().toString()
//                    Log.e(
//                        "getNameHobby()",
//                        "서버에서 이름 : getted_name"
//                    )
//                }
//            }
//
//            override fun onFailure(call: Call<List<ReviewFragRvUsingInterface.profile>?>, t: Throwable) {
//                Log.e("getNameHobby()", "에러 : " + t.message)
//            }
//        })
//    }
}