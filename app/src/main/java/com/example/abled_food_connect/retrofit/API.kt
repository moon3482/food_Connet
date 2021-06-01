package com.example.abled_food_connect.retrofit


import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.widget.Toast
import com.example.abled_food_connect.ChatActivity
import com.example.abled_food_connect.R
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
    interface reviewWriting{
        // 리뷰작성하기 보내기
        @Multipart
        @POST("review/review_writing.php")
        fun review_Writing_Request(
            //@Part itemphoto: ArrayList<MultipartBody.Part>,
            @Part itemphoto: List<MultipartBody.Part>,
            @Part("room_tb_id") room_tb_id: Int,
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

    interface reviewLikeBtnClick{
        // 좋아요 버튼 클릭
        @Multipart
        @POST("review/review_like_btn_click.php")
        fun review_Like_Btn_Click(
            @Part("what_click_review_tb_id") what_click_review_tb_id: Int,
            @Part("my_user_tb_id") my_user_tb_id: Int,
            @Part("my_user_tb_user_id") my_user_tb_user_id: String,
            ): Call<ReviewLikeBtnClickData>
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