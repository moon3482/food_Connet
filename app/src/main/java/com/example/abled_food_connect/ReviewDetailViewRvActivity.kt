package com.example.abled_food_connect


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.adapter.ReviewDetailViewRvAdapter
import com.example.abled_food_connect.data.ReviewDetailViewRvData
import com.example.abled_food_connect.data.ReviewDetailViewRvDataItem
import com.example.abled_food_connect.data.ReviewLikeBtnClickData
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.interfaces.ReviewDetailRvInterface
import com.example.abled_food_connect.retrofit.API
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
            reviewDbLoading(review_id.toString())
        }


        //리사이클러뷰
        detail_rv = findViewById<RecyclerView>(R.id.review_Detail_rv)
        detail_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        detail_rv.setHasFixedSize(true)


        Log.d("테이블id", MainActivity.user_table_id.toString())
        Log.d("아이디", MainActivity.loginUserId)


        //리사이클러뷰 구분선
        val dividerItemDecoration =
            DividerItemDecoration(detail_rv.context, LinearLayoutManager(this).orientation)

        detail_rv.addItemDecoration(dividerItemDecoration)



    }



    fun reviewDbLoading(review_id:String){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            var review_id = data?.getIntExtra("review_id",0)!!
            var review_detail_view_rv_position = data?.getIntExtra("review_detail_view_rv_position",0)!!
            var review_detail_view_like_count = data?.getStringExtra("review_detail_view_like_count")!!
            var review_detail_view_comment_count = data?.getStringExtra("review_detail_view_comment_count")!!
            var review_detail_view_like_btn_click_check = data?.getBooleanExtra("review_detail_view_like_btn_click_check",false)!!
            Log.d("돌아옴", review_detail_view_rv_position.toString())
            Log.d("돌아옴", review_id.toString())

            DetailRv_arrayList.get(review_detail_view_rv_position).like_count = review_detail_view_like_count
            DetailRv_arrayList.get(review_detail_view_rv_position).comment_count = review_detail_view_comment_count
            DetailRv_arrayList.get(review_detail_view_rv_position).heart_making = review_detail_view_like_btn_click_check

            mAdapter =  ReviewDetailViewRvAdapter(DetailRv_arrayList)
            mAdapter.notifyItemChanged(review_detail_view_rv_position)
            detail_rv.adapter = mAdapter

        }
    }




}