package com.example.abled_food_connect


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.ReviewDetailViewRvAdapter
import com.example.abled_food_connect.data.ReviewDetailViewRvData
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.interfaces.ReviewDetailRvInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewDetailViewRvActivity : AppCompatActivity() {



    //리사이클러뷰 어래이리스트
    private lateinit var DetailRv_arrayList : ArrayList<ReviewDetailViewRvDataItem>

    //리사이클러뷰
    lateinit var detail_rv : RecyclerView


    //리사이클러뷰 어댑터
    lateinit var mAdapter : ReviewDetailViewRvAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail_view_rv)

        var review_id = intent.getStringExtra("review_id")

        if (review_id != null) {
            Log.d("review_id", review_id)
            reviewDbLoading(review_id)
        }

        detail_rv = findViewById<RecyclerView>(R.id.review_Detail_rv)
        detail_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        detail_rv.setHasFixedSize(true)


        Log.d("테이블id", MainActivity.user_table_id.toString())
        Log.d("아이디", MainActivity.loginUserId)

        val dividerItemDecoration =
            DividerItemDecoration(detail_rv.context, LinearLayoutManager(this).orientation)

        detail_rv.addItemDecoration(dividerItemDecoration)


    }



    fun reviewDbLoading(review_id:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.37.36.188/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ReviewDetailRvInterface::class.java)
        val review_Detail_rv_using = api.review_Detail_rv_using_interface(review_id,MainActivity.user_table_id)


        review_Detail_rv_using.enqueue(object : Callback<ReviewDetailViewRvData> {
            override fun onResponse(
                call: Call<ReviewDetailViewRvData>,
                response: Response<ReviewDetailViewRvData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                var items : ReviewDetailViewRvData? =  response.body()


                Log.d(ReviewFragment.TAG, "성공 : ${items!!.roomList}")


                DetailRv_arrayList = items!!.roomList as ArrayList<ReviewDetailViewRvDataItem>

                for(i in DetailRv_arrayList.indices){
                    println(DetailRv_arrayList.get(i).review_picture_0);
                }



                mAdapter =  ReviewDetailViewRvAdapter(DetailRv_arrayList)
                mAdapter.notifyDataSetChanged()
                detail_rv.adapter = mAdapter



            }

            override fun onFailure(call: Call<ReviewDetailViewRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


}