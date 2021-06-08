package com.example.abled_food_connect.adapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.ReviewCommentActivity
import com.example.abled_food_connect.ReviewDetailViewRvActivity
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import com.google.gson.Gson
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewDetailViewRvAdapter (var ReviewDetailList: ArrayList<ReviewDetailViewRvDataItem>) : RecyclerView.Adapter<ReviewDetailViewRvAdapter.CustromViewHolder>(){



    //클릭리스너

    //클릭 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, DetailViewPosition: Int, Review_id : Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustromViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_detail_view_item,parent,false)
        return CustromViewHolder(view)

    }

    override fun getItemCount(): Int {
        return ReviewDetailList.size
    }

    override fun onBindViewHolder(holder: CustromViewHolder, position: Int) {




        //프로필 이미지
        Glide.with(holder.profileDetailIv.context)
            .load(holder.profileDetailIv.context.getString(R.string.http_request_base_url)+ReviewDetailList.get(position).profile_image)
            .circleCrop()
            .into(holder.profileDetailIv)

        //닉네임
       holder.nicNameDetailTv.text = ReviewDetailList.get(position).writer_nicname
        //리뷰 작성일
        holder.writingDateDetailTv.text = ReviewDetailList.get(position).reporting_date
        //레스토랑 주소
        holder.restaurantAddressDetailTv.text = ReviewDetailList.get(position).restaurant_address
        //레스토랑 이름
        holder.restaurantNameDetailTv.text = ReviewDetailList.get(position).restaurant_name
        //리뷰 후기 사진 이미지 스위쳐
        //holder.reviewPictureIs



        var imagesList = mutableListOf<String>()
        imagesList.clear()

        imagesList.add(ReviewDetailList.get(position).review_picture_0)
        if(ReviewDetailList.get(position).review_picture_1 !=""){
            imagesList.add(ReviewDetailList.get(position).review_picture_1)
        }

        if(ReviewDetailList.get(position).review_picture_2 !=""){
            imagesList.add(ReviewDetailList.get(position).review_picture_2)
        }

        holder.view_pager2.adapter = Review_Detail_ViewPagerAdapter(imagesList)
        holder.view_pager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        holder.indicator.setViewPager(holder.view_pager2)
        if(imagesList.size>1) {
            holder.indicator.visibility = View.VISIBLE
            Log.d("보여준다", imagesList.size.toString())

        }

        if(imagesList.size==1) {
            holder.indicator.visibility = View.GONE
            Log.d("보여준다", imagesList.size.toString())

        }

//        별점 삭제
//        //맛 평가 별점
//        holder.ratingStarTasteDetailTv.text = ReviewDetailList.get(position).rating_star_taste.toString()
//
//        //서비스 평가 별점
//        holder.ratingStarServiceDetailTv.text = ReviewDetailList.get(position).rating_star_service.toString()
//
//        //위생 평가 별점
//        holder.ratingStarCleanDetailTv.text = ReviewDetailList.get(position).rating_star_clean.toString()
//
//        //인테리어 평가 별점
//        holder.ratingStarInteriorDetailTv.text =ReviewDetailList.get(position).rating_star_interior.toString()

        //리뷰 작성 내용
        holder.reviewDescriptionTv.text = ReviewDetailList.get(position).review_description


        //좋아요 리니어 레이아웃 좋아요 버튼을 클릭
        holder.likeBtn.setOnClickListener(View.OnClickListener {
            RvAdapterReviewLikeBtnClick(ReviewDetailList.get(position).review_id,position,holder.likeBtn.context)

            Log.d("무엇", ReviewDetailList.get(position).review_id.toString()+MainActivity.user_table_id+MainActivity.loginUserId)
        })

        //좋아요 개수
        holder.likeCountTv.text = ReviewDetailList.get(position).like_count

        //좋아요 하트 변경

        if(ReviewDetailList.get(position).heart_making == true) {
            holder.heartIv.setColorFilter(Color.parseColor("#55ff0000"))
        }else{
            holder.heartIv.setColorFilter(Color.parseColor("#55111111"))
        }


        //댓글 개수
        holder.commentCountTv.text = ReviewDetailList.get(position).comment_count

        //ReviewCommentActivity
        holder.commentBtn.setOnClickListener(View.OnClickListener {


            itemClickListner.onClick(it,position,ReviewDetailList.get(position).review_id)

            var toMoveCommentActivity : Intent = Intent(holder.commentBtn.context, ReviewCommentActivity::class.java)
            toMoveCommentActivity.putExtra("review_id", ReviewDetailList.get(position).review_id)
            startActivity(holder.commentBtn.context as Activity,toMoveCommentActivity,null)


        })





    }

    fun addItem(prof: ReviewDetailViewRvDataItem){

        ReviewDetailList.add(prof)
        notifyDataSetChanged()

    }




    class CustromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //프로필 이미지
        val profileDetailIv = itemView.findViewById<ImageView>(R.id.profileDetailIv)
        //닉네임
        val nicNameDetailTv = itemView.findViewById<TextView>(R.id.nicNameDetailTv)
        //리뷰 작성일
        val writingDateDetailTv = itemView.findViewById<TextView>(R.id.writingDateDetailTv)
        //레스토랑 주소
        val restaurantAddressDetailTv = itemView.findViewById<TextView>(R.id.restaurantAddressDetailTv)
        //레스토랑 이름
        val restaurantNameDetailTv = itemView.findViewById<TextView>(R.id.restaurantNameDetailTv)
        //리뷰 후기 사진 이미지 스위쳐
        val view_pager2 = itemView.findViewById<ViewPager2>(R.id.view_pager2)


        val indicator = itemView.findViewById<CircleIndicator3>(R.id.indicator)




//        별점 삭제
//        //맛 평가 별점
//        val ratingStarTasteDetailTv = itemView.findViewById<TextView>(R.id.ratingStarTasteDetailTv)
//
//        //서비스 평가 별점
//        val ratingStarServiceDetailTv = itemView.findViewById<TextView>(R.id.ratingStarServiceDetailTv)
//
//        //위생 평가 별점
//        val ratingStarCleanDetailTv = itemView.findViewById<TextView>(R.id.ratingStarCleanDetailTv)
//
//        //인테리어 평가 별점
//        val ratingStarInteriorDetailTv = itemView.findViewById<TextView>(R.id.ratingStarInteriorDetailTv)

        //리뷰 작성 내용
        val reviewDescriptionTv = itemView.findViewById<TextView>(R.id.reviewDescriptionTv)

        //좋아요 리니어레이아웃
        val likeBtn = itemView.findViewById<LinearLayout>(R.id.likeBtn)

        //좋아요 수
        val likeCountTv = itemView.findViewById<TextView>(R.id.likeCountTv)

        //좋아요 하트
        val heartIv = itemView.findViewById<ImageView>(R.id.heartIv)

        //댓글 수
        val commentCountTv = itemView.findViewById<TextView>(R.id.commentCountTv)

        //댓글 버튼
        val commentBtn = itemView.findViewById<LinearLayout>(R.id.commentBtn)

    }





    fun RvAdapterReviewLikeBtnClick(what_click_review_tb_id:Int,position : Int, context: Context){
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewLikeBtnClick::class.java)
        val review_Like_Btn_Click = api.review_Like_Btn_Click(what_click_review_tb_id,MainActivity.user_table_id,MainActivity.loginUserId)


        review_Like_Btn_Click.enqueue(object : Callback<ReviewLikeBtnClickData> {
            override fun onResponse(
                call: Call<ReviewLikeBtnClickData>,
                response: Response<ReviewLikeBtnClickData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val ReviewLikeBtnClickData: ReviewLikeBtnClickData = response.body()!!

                    var heart_making = ReviewLikeBtnClickData.heart_making
                    var how_many_like_count: Int = ReviewLikeBtnClickData.how_many_like_count
                    var isSuccess: Boolean = ReviewLikeBtnClickData.success

                    Log.d(ReviewFragment.TAG, "성공 현재 카운트 개수 : ${how_many_like_count}")
                    Log.d(ReviewFragment.TAG, "성공 : ${isSuccess}")

                    ReviewDetailList.get(position).like_count = how_many_like_count.toString()

                    if(heart_making == true){
                        ReviewDetailList.get(position).heart_making = true
                        Log.d(ReviewFragment.TAG, "트루 : ${heart_making}")
                    }else if(heart_making == false){
                        ReviewDetailList.get(position).heart_making = false
                        Log.d(ReviewFragment.TAG, "false : ${heart_making}")
                    }

                    notifyItemChanged(position)


                }


            }

            override fun onFailure(call: Call<ReviewLikeBtnClickData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }

}
