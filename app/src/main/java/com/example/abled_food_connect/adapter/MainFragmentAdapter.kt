package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.ChatRoomActivity
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.RoomInformationActivity
import com.example.abled_food_connect.data.JoinRoomCheck
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainFragmentAdapter(val context: Context, private val list: ArrayList<MainFragmentItemData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CustomHolder(
            LayoutInflater.from(context).inflate(R.layout.main_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val maindata: MainFragmentItemData = list[position]
        val testholder: CustomHolder = holder as CustomHolder
        testholder.roomStatus.text = maindata.title
        testholder.shopName.text = maindata.info
        testholder.roomStatus
        if (maindata.roomStatus > 5) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            testholder.roomStatus.text = "모집중"
        } else if (maindata.roomStatus > 0.9) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            val text: String = context.getString(R.string.room_status_imminent_time)
            testholder.roomStatus.text =
                String.format(text, Math.round(maindata.roomStatus).toInt())

        } else if (maindata.roomStatus < 0.9 && maindata.roomStatus > 0.0) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            testholder.roomStatus.text = "임박"

        } else if (maindata.roomStatus < 0) {
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            testholder.roomStatus.text = "마감"
        }
        if (maindata.gender.equals("male")) {
            testholder.gender.setImageResource(R.drawable.ic_male)
        } else if (maindata.gender == "female") {
            testholder.gender.setImageResource(R.drawable.ic_female)
        } else {
            testholder.gender.setImageResource(R.drawable.ic_maleandfemale)
        }

        if (maindata.maximumAge == maindata.minimumAge) {
            testholder.roomAge.text = maindata.maximumAge.toString()
        } else {
            val text: String = context.getString(R.string.limit_age_badge)
            testholder.roomAge.text = String.format(text, maindata.minimumAge, maindata.maximumAge)
        }
        testholder.shopName.text = maindata.placeName
        testholder.roomTitle.text = maindata.title
        testholder.roomNumberOfPeople.text = "${maindata.nowNumOfPeople.toString()}/${(maindata.numOfPeople).toString()}명"
        testholder.roomDateTime.text = maindata.date
        val splitAddress = maindata.address.toString().split("구")
        val splitAddress2 = splitAddress[0].split(" ")
        var location:String = ""
        for (index in splitAddress2.indices){
            location += splitAddress2[index]+">"
        }
        location = location.substring(0,location.length-1)+"구"
        testholder.roomLocation.text = location
        testholder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                    joinRoomCheckMethod(maindata,location)

            }
        })

    }

    override fun getItemCount(): Int {

        return list.size
    }


    class CustomHolder(view: View) : RecyclerView.ViewHolder(view) {
        var roomStatus: TextView = view.findViewById(R.id.tvRoomStatus)
        var shopName: TextView = view.findViewById(R.id.tvShopName)
        var gender: ImageView = view.findViewById(R.id.ivGender)
        var roomTitle: TextView = view.findViewById(R.id.tvRoomTitle)
        var roomDateTime: TextView = view.findViewById(R.id.tvRoomDateTime)
        var roomLocation: TextView = view.findViewById(R.id.tvRoomLocation)
        var roomNumberOfPeople: TextView = view.findViewById(R.id.tvRoomNumberOfPeople)
        var roomAge: TextView = view.findViewById(R.id.tvAge)


    }
    fun joinRoomCheckMethod(mainData:MainFragmentItemData,addressParse:String){

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.joinRoomCheck(mainData.roomId,MainActivity.loginUserNickname,mainData.hostName).enqueue(object : Callback<JoinRoomCheck>{
            override fun onResponse(call: Call<JoinRoomCheck>, response: Response<JoinRoomCheck>) {
                    val joinRoomCheck = response.body()
                if(joinRoomCheck!!.success){
                    val intent = Intent(context, RoomInformationActivity::class.java)
                    intent.putExtra("roomId",mainData.roomId)
                    intent.putExtra("title",mainData.title)
                    intent.putExtra("info",mainData.info)
                    intent.putExtra("hostName",mainData.hostName)
                    intent.putExtra("address",addressParse)
                    intent.putExtra("date",mainData.date)
                    intent.putExtra("shopName",mainData.shopName)
                    intent.putExtra("roomStatus",mainData.roomStatus)
                    intent.putExtra("numOfPeople",mainData.numOfPeople.toString())
                    intent.putExtra("keyWords",mainData.keyWords)
                    intent.putExtra("imageUrl",joinRoomCheck.imageUrl)
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context, ChatRoomActivity::class.java)
                    intent.putExtra("roomId",mainData.roomId)
                    context.startActivity(intent)

                }
            }

            override fun onFailure(call: Call<JoinRoomCheck>, t: Throwable) {

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

}