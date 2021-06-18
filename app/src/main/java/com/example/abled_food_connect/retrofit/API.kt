package com.example.abled_food_connect.retrofit


import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.abled_food_connect.ChatActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.*
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class API {
    interface foodCoonnectCreateRoom {
        @POST("createRoom.php")
        fun createRoom(
            @Query("title") title:String,
            @Query("info") info:String,
            @Query("numOfPeople")numOfPeople:Int,
            @Query("date") date:String,
            @Query("address") address:String,
            @Query("shopName") shopName:String,
            @Query("keyWords") keyWords:String,
            @Query("gender") gender:String,
            @Query("minimumAge") minimumAge:Int,
            @Query("maximumAge") maximumAge:Int
        ):Call<String>
    }
    //회원가입시 닉네임이 중복되는지 확인한다.
    interface nicNameCheck{
        @FormUrlEncoded
        @POST("user_info/nicname_duplicate_check.php")
        fun checkNicName(
            @Field("nick_name") nick_name:String ):Call<String>
    }


    //프로필 수정 - 원본이미지와 압축된 이미지를 보낸다.
    interface UserProfileModifyStringChange_interface {
        @Multipart
        @POST("user_info/user_profile_just_String_modify.php")
        fun user_profile_just_String_modify_Request(
            @Part("user_tb_id") user_tb_id: Int,
            @Part("nic_name") nic_name: String,
            @Part("introduction") introduction: String
        ): Call<String>

    }


    //프로필 수정 - 원본이미지와 압축된 이미지, 아이디, 자기소개를 보낸다.
    interface UserProfileModifyImageChange_interface {
        @Multipart
        @POST("user_info/user_profile_modify.php")
        fun user_profile_modify_Request(
            @Part imageFile : MultipartBody.Part,
            @Part thumbnail_imageFile : MultipartBody.Part,
            @Part("user_tb_id") user_tb_id: Int,
            @Part("nic_name") nic_name: String,
            @Part("introduction") introduction: String
        ): Call<String>

    }



    interface reviewWriting{
        // 리뷰작성하기 보내기
        @Multipart
        @POST("review/review_writing.php")
        fun review_Writing_Request(
            //@Part itemphoto: ArrayList<MultipartBody.Part>,
            @Part itemphoto: List<MultipartBody.Part>,
            @Part("room_tb_id") room_tb_id: Int,
            @Part("writer_user_tb_id") writer_user_tb_id: Int,
            @Part("writer_uid") writer_uid: String,
            @Part("writer_nicname") writer_nicname: String,
            @Part("restaurant_address") restaurant_address: String,
            @Part("restaurant_name") restaurant_name: String,
            @Part("reporting_date") reporting_date: String,
            @Part("appointment_day") appointment_day: String,
            @Part("appointment_time") appointment_time: String,
            @Part("review_description") review_description: String,
            @Part("rating_star_taste") rating_star_taste: Int,
            @Part("rating_star_service") rating_star_service: Int,
            @Part("rating_star_clean") rating_star_clean: Int,
            @Part("rating_star_interior") rating_star_interior: Int): Call<String>
    }


    //리뷰에서 코멘트 버튼을 눌렀을때, 리뷰내역 가져오기

    interface reviewCommentReviewContentGet {

        @Multipart
        @POST("review/review_comment_activity_review_content_get.php")
        fun review_comment_review_content_get_interface(
            @Part("review_id") review_id: Int,
            //사용자 id는 좋아요 정보를 조회할때 사용한다.
            @Part("user_tb_id") user_tb_id: Int
        ): Call<ReviewDetailViewRvData>

    }

    interface reviewLikeBtnClick{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("review/review_like_btn_click.php")
        fun review_Like_Btn_Click(
            @Part("what_click_review_tb_id") what_click_review_tb_id: Int,
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("my_user_tb_user_id") my_user_tb_user_id: String
            ): Call<ReviewLikeBtnClickData>
    }


    interface reviewParentPageCommentListGet{
        // 부모 페이지 에서 댓글 목록 가져오기
        @Multipart
        @POST("review/review_parent_page_comment_list_get.php")
        fun reviewParentPageCommentListCalling(
            @Part("review_id") review_id: Int
        ): Call<ReviewParentPageCommentGetData>
    }

    interface reviewParentPageCommentWriting{
        // 부모페이지에서 댓글 작성
        @Multipart
        @POST("review/review_parent_page_comment_writing.php")
        fun reviewParentPageCommentWritingSend(
            @Part("review_id") review_id: Int,
            @Part("writing_user_id") writing_user_id: Int,
            @Part("comment_content") comment: String,
            @Part("comment_class") comment_class: Int,
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("sendTargetUserNicName") sendTargetUserNicName: String,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewParentPageCommentGetData>
    }



    interface reviewChildPageCommentListGet{
        // 댓글 목록 가져오기
        @Multipart
        @POST("review/review_child_page_comment_list_get.php")
        fun reviewChildPageCommentListGetCalling(
            @Part("review_id") review_id: Int,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewChildPageCommentGetData>
    }


    interface reviewChildPageCommentWriting{
        // 자식 댓글(코멘트)작성
        @Multipart
        @POST("review/review_child_page_comment_writing.php")
        fun review_child_page_comment_send_btn_click(
            @Part("review_id") review_id: Int,
            @Part("writing_user_id") writing_user_id: Int,
            @Part("comment_content") comment: String,
            @Part("comment_class") comment_class: Int,
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("sendTargetUserNicName") sendTargetUserNicName: String,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewChildPageCommentGetData>
    }


    interface reviewDetailViewLikeAndCommentCountCheck{
        // 리뷰 목록에서 댓글을 작성했다가 다시 뒤로 왔을때, 리뷰개수나 댓글개수가 변화가 있을 경우 올라가야함.
        @Multipart
        @POST("review/review_detail_view_like_and_commentcount_check.php")
        fun reviewDetailViewLikeAndCommentCountCheckGetCalling(
            @Part("review_tb_id") review_id: Int,
            @Part("user_tb_id") user_tb_id: Int
        ): Call<ReviewDetailViewLikeAndCommentCountCheckData>
    }


    interface reviewPageCommentWriting{
        // 리뷰보다가 댓글(코멘트)작성
        @Multipart
        @POST("review/review_comment_writing.php")
        fun review_comment_send_btn_click(
            @Part("review_id") review_id: Int,
            @Part("writing_user_id") writing_user_id: Int,
            @Part("comment_content") comment: String,
            @Part("comment_class") comment_class: Int,
            @Part("sendTargetUserTable_id") sendTargetUserTable_id: Int,
            @Part("sendTargetUserNicName") sendTargetUserNicName: String,
            @Part("groupNum") groupNum: Int
        ): Call<ReviewChildPageCommentGetData>
    }



    interface chatImageSend_interface {
        // 프로필 이미지 보내기
        @Multipart
        @POST("chat/DirectMessageImageSending.php")
        fun chat_image_send_interface_Request(
            @Part imageFile : MultipartBody.Part
        ): Call<ChatImageSendingData>

    }


    interface getChattingList_Interface{
        // UserProfileActivity에서 유저정보 가져오기
        @Multipart
        @POST("chat/direct_message_list_get.php")
        fun direct_message_list_get(
            @Part("roomName") roomName: String
        ): Call<DirectMessageNodeServerSendData>
    }



    interface dmRoomJoinCheck_Interface{
        // UserProfileActivity에서 유저정보 가져오기
        @Multipart
        @POST("chat/dm_room_join_check.php")
        fun dm_room_join_check(
            @Part("dm_room_name") dm_room_name: String,
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("your_user_tb_id") your_user_tb_id: Int
        ): Call<String>
    }

    interface DMChattingListGet_Interface{
        @Multipart
        @POST("chat/dm_chatting_list_get.php")
        fun dm_room_join_check(
            @Part("my_user_tb_id") my_user_tb_id: Int
        ): Call<ChattingFragmentDmRvData>
    }



    interface UserProfileDataInterface{
        // UserProfileActivity에서 유저정보 가져오기
        @Multipart
        @POST("user_info/user_profile_data_get.php")
        fun user_profile_data_get(
            @Part("user_tb_id") user_tb_id: Int
        ): Call<UserProfileData>
    }


    interface UserProfileClickedReviewGridListRvInterface {

        @Multipart
        @POST("review/user_profile_clicked_review_grid_rv_list_get.php")
        fun user_profile_clicked_review_grid_list_get(
            @Part("clicked_user_tb_id") clicked_user_tb_id: String,
            @Part("my_user_tb_id") my_user_tb_id: Int
        ): Call<ReviewDetailViewRvData>

    }



    fun joinRoom(context:Context,roomId:String,userId:String){

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.joinRoom(roomId,userId).enqueue(object: Callback<joinRoomClass>{
            override fun onResponse(call: Call<joinRoomClass>, response: Response<joinRoomClass>) {
                val success : joinRoomClass = response.body()!!
                if (success.success){
                    val intent = Intent(context,ChatActivity::class.java)
                    context.startActivity(intent)

                }else{
                    Toast.makeText(context,"입장 실패",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<joinRoomClass>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()


    }
    data class joinRoomClass(
        @SerializedName("success")
        val success: Boolean
    )
}