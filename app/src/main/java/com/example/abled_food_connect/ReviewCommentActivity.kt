package com.example.abled_food_connect

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.abled_food_connect.adapter.ReviewChildPageCommenRvAdapter
import com.example.abled_food_connect.adapter.ReviewParentPageCommentRvAdapter
import com.example.abled_food_connect.adapter.Review_Detail_ViewPagerAdapter
import com.example.abled_food_connect.data.*
import com.example.abled_food_connect.databinding.ActivityReviewCommentBinding
import com.example.abled_food_connect.fragments.ReviewFragment
import com.example.abled_food_connect.retrofit.API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewCommentActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityReviewCommentBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    private var review_id : Int = 0
    private var room_id : Int = 0




    private lateinit var comment_content : String
    private var comment_class : Int = 0
    private var sendTargetUserTable_id : Int = 0
    private lateinit var sendTargetUserNicName : String


    //리뷰 작성자 이름 - 댓글 목록에서 댓글 작성자와 리뷰 작성자가 일치하면 닉네임 옆에 [작성자]라는 표시가 뜬다
    private lateinit var writerNicname : String
    var WriterUserTbId : Int = 0


    //코멘트 리사이클러뷰
    lateinit var review_comment_Child_rv_adapter: ReviewParentPageCommentRvAdapter
    var comment_ArrayList = ArrayList<ReviewParentPageCommentGetDataItem>()
    lateinit var reviewCommentRv : RecyclerView


    //하단 댓글 edittext창


    lateinit var writingCommentEt : EditText

    //부모로 등록되는 댓글인지, 자식으로 등록되는 댓글인지 구별
    //이 페이지에서는 무조건 부모로 등록되므로 0으로 설정
    var childOrParent : Int = 0


    //부모는 -1을 넘겨주고, 서버에서 그룹넘버를 부여받는다.
    //groupNum 몇번째 부모에 속해있는 자식 코멘트인가.
    //db에 저장될때 자식 코멘트는 부모와 동일한 groupNum을 가진다.
    var groupNum : Int = -1

    var clicked_review_btn = 0


    // 어떤 부모댓글을 클릭했는지 저장하는 변수
    var whatParentPosition = -1

    // 그 부모댓글의 리뷰id
    var what_parent_review_id = 0

    // comment_tb_id
    var what_parent_comment_tb_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_comment)

        //키보드가 화면 안가리게함
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityReviewCommentBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        //인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 tv_message -> tvMessage 로 자동 변환 되었습니다.


        setSupportActionBar(binding.Toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.Toolbar.title = "리뷰"
        //툴바에 백버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        review_id = intent.getIntExtra("review_id",0)
        //리뷰 정보를 가져온다

        //댓글버튼을 누른 것인지 아닌지 체크
        //댓글버튼을 누르고 들어왔다면, 엑티비티 입장시 댓글화면으로 이동함.
        clicked_review_btn = intent.getIntExtra("clicked_review_btn",0)
        reviewContentLoading(review_id)




        binding.contentCommentBtn.setOnClickListener(View.OnClickListener {
            writingCommentEt.requestFocus()
            binding.contentCommentBtn.hideKeyboard()
            binding.contentCommentBtn.showKeyboard()
        })



        binding.reviewDotBtn.setOnClickListener(View.OnClickListener {


            var pop = PopupMenu(this,binding.reviewDotBtn)

            menuInflater.inflate(R.menu.review_content_3_dot_menu, pop.menu)

            // 2. 람다식으로 처리
            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.reviewDeleteBtn ->{
                        var builder = AlertDialog.Builder(this)
                        builder.setTitle("리뷰삭제")
                        builder.setMessage("리뷰를 삭제하시겠습니까?")

                        // 버튼 클릭시에 무슨 작업을 할 것인가!
                        var listener = object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                when (p1) {
                                    DialogInterface.BUTTON_POSITIVE ->{
                                        Log.d("TAG", "닫기버튼 클릭")
                                    }

                                    DialogInterface.BUTTON_NEGATIVE ->{

                                        reviewDeleteBtnClick(review_id,room_id)

                                    }

                                }
                            }
                        }

                        builder.setPositiveButton("취소", listener)
                        builder.setNegativeButton("삭제", listener)

                        builder.show()
                    }
                }
                false
            }
            pop.show()


        })










        //리사이클러뷰
        reviewCommentRv = binding.reviewCommentRv
        reviewCommentRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        reviewCommentRv.setHasFixedSize(false)


        //리사이클러뷰 구분선
        val dividerItemDecoration =
            DividerItemDecoration(reviewCommentRv.context, LinearLayoutManager(this).orientation)

        reviewCommentRv.addItemDecoration(dividerItemDecoration)




        //하단 댓글 코멘트 남기는 창
        writingCommentEt= binding.writingCommentEt


        //댓글작성버튼 클릭
        binding.sendCommentBtn.setOnClickListener(View.OnClickListener {
            comment_content = binding.writingCommentEt.text.toString()

            binding.writingCommentEt.setText(binding.writingCommentEt.text.toString().replace(" ",""))
            if(binding.writingCommentEt.text.toString().length>0){


                //부모로 등록되는 댓글인지, 자식으로 등록되는 댓글인지 구별
                //이 페이지에서는 무조건 부모로 등록되므로 0으로 설정
                childOrParent = 0

                CommentWritingBtnClick(review_id,comment_content,childOrParent,sendTargetUserTable_id,sendTargetUserNicName,groupNum)



                //작성버튼을 누르면 키보드를 안보이게 한다
                binding.sendCommentBtn.hideKeyboard()


                //댓글을 달면 댓글이없어요 라고 적혀있는 텍스트뷰를 안보이게 처리한다.
                binding.noCommentTv.visibility = View.GONE
                //댓글 내용을 입력했던 Edittext를 비워준다.
                binding.writingCommentEt.setText(null)


            }else{
                Toast.makeText(applicationContext, "답글을 입력해주세요.", Toast.LENGTH_LONG).show()
            }


        })


    }

    override fun onResume() {
        super.onResume()

        if(whatParentPosition != -1){
            clickedCommentDataLoading()
        }
    }

    //댓글 목록 불러오기
    fun ParentCommentLoading(review_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewParentPageCommentListGet::class.java)
        val reviewParentCommentListGet = api.reviewParentPageCommentListCalling(review_id)
        reviewParentCommentListGet.enqueue(object : Callback<ReviewParentPageCommentGetData> {
            override fun onResponse(
                call: Call<ReviewParentPageCommentGetData>,
                response: Response<ReviewParentPageCommentGetData>
            ) {

                if(response.body() != null) {
                    val reviewParentPageCommentGetData: ReviewParentPageCommentGetData = response.body()!!
                    var isSuccess: Boolean = reviewParentPageCommentGetData.success

                    comment_ArrayList = reviewParentPageCommentGetData.commentList as ArrayList<ReviewParentPageCommentGetDataItem>



                    Log.d(ReviewFragment.TAG, "목록불러와 ${reviewParentPageCommentGetData.commentList}")
                    Log.d(ReviewFragment.TAG, "목록불러와 : ${isSuccess}")

                    review_comment_Child_rv_adapter =  ReviewParentPageCommentRvAdapter(comment_ArrayList,writerNicname)
                    review_comment_Child_rv_adapter.notifyDataSetChanged()

                    //클릭리스너 등록
                    review_comment_Child_rv_adapter.setItemClickListener( object : ReviewParentPageCommentRvAdapter.ItemClickListener{

                        override fun onClick(view: View, position: Int, review_id :Int ,comment_tb_id : Int) {
                            whatParentPosition = position
                            what_parent_review_id = review_id
                            what_parent_comment_tb_id = comment_tb_id

                            var totalCommentCount = comment_ArrayList.size
                            Log.d("어래이사이즈", totalCommentCount.toString())


                            for(i in 0..comment_ArrayList.size-1) {
                                totalCommentCount += comment_ArrayList.get(i).comment_class_child_count
                                Log.d("있으면 출력", totalCommentCount.toString())
                            }

                            Log.d("나오면좋겠다", totalCommentCount.toString())

                            //댓글 개수
                            binding.contentCommentCountTv.text = totalCommentCount.toString()
                        }
                    })



                    reviewCommentRv.adapter = review_comment_Child_rv_adapter





                    //댓글 클릭시 댓클창 상단으로 이동
                    if(clicked_review_btn == 1){
                        Handler().postDelayed(Runnable {
                            //댓글엑티비티에오면 스크롤을 댓글창이 보이게 맞춰준다.
                            binding.nestedScroll.smoothScrollBy(0,binding.reviewCommentRv.top)
                        }, 500)
                    }else{
                        Handler().postDelayed(Runnable {
                            //댓글엑티비티에오면 스크롤을 댓글창이 보이게 맞춰준다.
                            binding.nestedScroll.smoothScrollBy(0,binding.contentViewPager2.top)
                        }, 500)
                    }





                }


            }

            override fun onFailure(call: Call<ReviewParentPageCommentGetData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }








    //리뷰작성버튼클릭
    fun CommentWritingBtnClick(review_id:Int,comment:String,comment_class:Int,sendTargetUserTable_id:Int,sendTargetUserNicName:String,groupNum:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewParentPageCommentWriting::class.java)
        val review_Like_Btn_Click = api.reviewParentPageCommentWritingSend(review_id,MainActivity.user_table_id,comment,comment_class,sendTargetUserTable_id,sendTargetUserNicName,groupNum)
        review_Like_Btn_Click.enqueue(object : Callback<ReviewParentPageCommentGetData> {
            override fun onResponse(
                call: Call<ReviewParentPageCommentGetData>,
                response: Response<ReviewParentPageCommentGetData>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val reviewParentPageCommentGetData: ReviewParentPageCommentGetData = response.body()!!
                    var isSuccess: Boolean = reviewParentPageCommentGetData.success
                    var how_many_like_count = reviewParentPageCommentGetData.comment_count

                    comment_ArrayList = reviewParentPageCommentGetData.commentList as ArrayList<ReviewParentPageCommentGetDataItem>


                    binding.contentCommentCountTv.text = how_many_like_count

                    Log.d(ReviewFragment.TAG, "목록불러와 ${reviewParentPageCommentGetData.commentList}")
                    Log.d(ReviewFragment.TAG, "목록불러와 : ${isSuccess}")

                    review_comment_Child_rv_adapter =  ReviewParentPageCommentRvAdapter(comment_ArrayList,writerNicname)
                    review_comment_Child_rv_adapter.notifyDataSetChanged()

                    //클릭리스너 등록
                    review_comment_Child_rv_adapter.setItemClickListener( object : ReviewParentPageCommentRvAdapter.ItemClickListener{

                        override fun onClick(view: View, position: Int, review_id :Int ,comment_tb_id : Int) {
                            whatParentPosition = position
                            what_parent_review_id = review_id
                            what_parent_comment_tb_id = comment_tb_id

                            var totalCommentCount = comment_ArrayList.size
                            Log.d("어래이사이즈", totalCommentCount.toString())


                            for(i in 0..comment_ArrayList.size-1) {
                                totalCommentCount += comment_ArrayList.get(i).comment_class_child_count
                                Log.d("있으면 출력", totalCommentCount.toString())
                            }

                            Log.d("나오면좋겠다", totalCommentCount.toString())

                            //댓글 개수
                            binding.contentCommentCountTv.text = totalCommentCount.toString()
                        }
                    })

                    reviewCommentRv.adapter = review_comment_Child_rv_adapter







                    Handler().postDelayed(Runnable {
                        //댓글엑티비티에오면 스크롤을 댓글창이 보이게 맞춰준다.
                        binding.nestedScroll.scrollTo(0, reviewCommentRv.bottom)
                    }, 500)




                }


            }

            override fun onFailure(call: Call<ReviewParentPageCommentGetData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    fun ReviewLikeBtnClick(what_click_review_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
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

                    binding.contentLikeCountTv.text = how_many_like_count.toString()

                    if(heart_making == true){
                        binding.contentHeartIv.setColorFilter(Color.parseColor("#77ff0000"))
                        Log.d(ReviewFragment.TAG, "트루 : ${heart_making}")


                    }else if(heart_making == false){
                        binding.contentHeartIv.setColorFilter(Color.parseColor("#55111111"))
                        Log.d(ReviewFragment.TAG, "false : ${heart_making}")


                    }


                }


            }

            override fun onFailure(call: Call<ReviewLikeBtnClickData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }



    fun reviewContentLoading(review_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewCommentReviewContentGet::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val review_comment_review_content_get = api.review_comment_review_content_get_interface(review_id,MainActivity.user_table_id)


        review_comment_review_content_get.enqueue(object : Callback<ReviewDetailViewRvData> {
            override fun onResponse(
                call: Call<ReviewDetailViewRvData>,
                response: Response<ReviewDetailViewRvData>
            ) {
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "리뷰 컨텐츠 : ${response.body().toString()}")

                var items : ReviewDetailViewRvData? =  response.body()


                Log.d(ReviewFragment.TAG, "리뷰 코멘트 성공 : ${items!!.roomList}")


                room_id = items!!.roomList.get(0).room_tb_id

                //작성자 프로필
                Glide.with(applicationContext)
                    .load(getString(R.string.http_request_base_url)+items!!.roomList.get(0).profile_image)
                    .circleCrop()
                    .into(binding.contentProfileDetailIv)


                //작성자 유저테이블 id
                WriterUserTbId = items!!.roomList.get(0).writer_user_tb_id
                //댓글 작성시 누구에게 보내는지 알려주기 위함
                sendTargetUserTable_id = items!!.roomList.get(0).writer_user_tb_id

                binding.contentProfileDetailIv.setOnClickListener(View.OnClickListener {
                    var toMoveUserProfileActivity : Intent = Intent(applicationContext, UserProfileActivity::class.java)
                    toMoveUserProfileActivity.putExtra("writer_user_tb_id", WriterUserTbId)
                    startActivity(
                        toMoveUserProfileActivity,
                        null
                    )
                })

                if(MainActivity.user_table_id == items!!.roomList.get(0).writer_user_tb_id){
                    binding.reviewDotBtn.visibility = View.VISIBLE
                }



                //작성자 닉네임
                binding.contentNicNameDetailTv.text = items!!.roomList.get(0).writer_nicname



                binding.contentNicNameDetailTv.setOnClickListener(View.OnClickListener {
                    var toMoveUserProfileActivity : Intent = Intent(applicationContext, UserProfileActivity::class.java)
                    toMoveUserProfileActivity.putExtra("writer_user_tb_id", WriterUserTbId)
                    startActivity(
                        toMoveUserProfileActivity,
                        null
                    )
                })

                //댓글 작성시 누구에게 보내는지 알려주기 위함
                sendTargetUserNicName = items!!.roomList.get(0).writer_nicname

                writerNicname = items!!.roomList.get(0).writer_nicname

                //리뷰 작성일
                binding.contentWritingDateDetailTv.text = items!!.roomList.get(0).reporting_date

                //레스토랑 주소
                binding.contentRestaurantAddressDetailTv.text = items!!.roomList.get(0).restaurant_address

                //레스토랑 이름
                binding.contentRestaurantNameDetailTv.text = items!!.roomList.get(0).restaurant_name


                //리뷰 이미지 (뷰페이저 및 인디케이터)

                var imagesList = mutableListOf<String>()

                imagesList.add(items!!.roomList.get(0).review_picture_0)
                if(items!!.roomList.get(0).review_picture_1 !=""){
                    imagesList.add(items!!.roomList.get(0).review_picture_1)
                }

                if(items!!.roomList.get(0).review_picture_2 !=""){
                    imagesList.add(items!!.roomList.get(0).review_picture_2)
                }

                binding.contentViewPager2.adapter = Review_Detail_ViewPagerAdapter(imagesList)
                binding.contentViewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                binding.contentIndicator.setViewPager(binding.contentViewPager2)
                if(imagesList.size>1) {
                    binding.contentIndicator.visibility = View.VISIBLE
                    Log.d("보여준다", imagesList.size.toString())

                }

                if(imagesList.size==1) {
                    binding.contentIndicator.visibility = View.GONE
                    Log.d("보여준다", imagesList.size.toString())

                }

                //별점 삭제
//                //맛 평가 별점
//                binding.contentRatingStarTasteDetailTv.text = items!!.roomList.get(0).rating_star_taste.toString()
//
//                //서비스 평가 별점
//                binding.contentRatingStarServiceDetailTv.text = items!!.roomList.get(0).rating_star_service.toString()
//
//                //위생 평가 별점
//                binding.contentRatingStarCleanDetailTv.text = items!!.roomList.get(0).rating_star_clean.toString()
//
//                //인테리어 평가 별점
//                binding.contentRatingStarInteriorDetailTv.text = items!!.roomList.get(0).rating_star_interior.toString()


                //리뷰 작성 내용
                binding.contentReviewDescriptionTv.text =  items!!.roomList.get(0).review_description


                //좋아요 리니어 레이아웃 좋아요 버튼을 클릭
                binding.contentLikeBtn.setOnClickListener(View.OnClickListener {
                    ReviewLikeBtnClick(review_id)
                })

                //좋아요 개수
                binding.contentLikeCountTv.text = items!!.roomList.get(0).like_count

                //좋아요 하트 변경

                if(items!!.roomList.get(0).heart_making == true) {
                    binding.contentHeartIv.setColorFilter(Color.parseColor("#77ff0000"))

                }else{
                    binding.contentHeartIv.setColorFilter(Color.parseColor("#55111111"))

                }


                //댓글 개수
                binding.contentCommentCountTv.text = items!!.roomList.get(0).comment_count

                //댓글 없을때 문구 처리
                if(items!!.roomList.get(0).comment_count.toInt() != 0){
                    binding.noCommentTv.visibility = View.GONE
                }




                //댓글목록을 가져온다
                ParentCommentLoading(review_id)


            }

            override fun onFailure(call: Call<ReviewDetailViewRvData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }



    fun reviewDeleteBtnClick(what_click_review_tb_id:Int,room_id: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.reviewDeleteBtn::class.java)
        val review_Like_Btn_Click = api.review_delete_btn_click(MainActivity.user_table_id,what_click_review_tb_id,room_id)


        review_Like_Btn_Click.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.d(ReviewFragment.TAG, "성공 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "성공 : ${response.body().toString()}")

                if(response.body() != null) {
                    val returnString: String = response.body()!!

                    if(returnString =="true"){
                        Toast.makeText(applicationContext, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }else{
                        Log.d("false", "false")
                    }


                }


            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }


    fun clickedCommentDataLoading(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.clickedCommentDataLoadingInterface::class.java)
        val data_load = api.clicked_comment_data_loading(what_parent_review_id,what_parent_comment_tb_id)


        data_load.enqueue(object : Callback<ReviewParentPageCommentGetData> {
            override fun onResponse(
                call: Call<ReviewParentPageCommentGetData>,
                response: Response<ReviewParentPageCommentGetData>
            ) {
                Log.d(ReviewFragment.TAG, "데이터로드 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "데이터로드 : ${response.body().toString()}")

                if(response.body() != null) {
                    val Item: ReviewParentPageCommentGetData = response.body()!!


                    var ItemArray: ArrayList<ReviewParentPageCommentGetDataItem>
                    ItemArray = Item.commentList as ArrayList<ReviewParentPageCommentGetDataItem>


                    if(ItemArray[0].deleteCheck ==1){
                        comment_ArrayList.removeAt(whatParentPosition)
                        reviewCommentRv.adapter?.notifyItemRemoved(whatParentPosition)
                        reviewCommentRv.adapter?.notifyItemRangeChanged(whatParentPosition,comment_ArrayList.size)
                    }else{
                        comment_ArrayList.set(whatParentPosition,ItemArray[0])
                        reviewCommentRv.adapter?.notifyItemChanged(whatParentPosition)
                    }

                    var totalCommentCount = comment_ArrayList.size

                    for(i in 0..comment_ArrayList.size-1) {
                        totalCommentCount += comment_ArrayList.get(i).comment_class_child_count
                    }

                    //댓글 개수
                    binding.contentCommentCountTv.text = totalCommentCount.toString()

                    Log.d("totalCommentCount", totalCommentCount.toString())

                    //댓글 없을때 문구 처리
                    if(totalCommentCount != 0){
                        binding.noCommentTv.visibility = View.GONE
                    }else{
                        binding.noCommentTv.visibility = View.VISIBLE
                    }




                    whatParentPosition = -1
                }


            }

            override fun onFailure(call: Call<ReviewParentPageCommentGetData>, t: Throwable) {
                Log.d(ReviewFragment.TAG, "실패 : $t")
            }
        })
    }




    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {

            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }





}