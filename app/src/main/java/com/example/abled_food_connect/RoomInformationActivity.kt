package com.example.abled_food_connect


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.abled_food_connect.databinding.ActivityRoomInformationBinding
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.MapSearch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

class RoomInformationActivity : AppCompatActivity() {
    val binding by lazy { ActivityRoomInformationBinding.inflate(layoutInflater) }
    private var mapClick = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = binding.root
        setContentView(view)
        val intent = intent
        val roomId = intent.getStringExtra("roomId")
        val title = intent.getStringExtra("title")
        val info = intent.getStringExtra("info")
        val hostName = intent.getStringExtra("hostName")
        val address = intent.getStringExtra("address")!!
        val date = intent.getStringExtra("date")
        val shopName = intent.getStringExtra("shopName")
        val roomStatus = intent.getDoubleExtra("roomStatus", 0.0);
        val nowNumOfPeople = intent.getStringExtra("nowNumOfPeople")
        val numOfPeople = intent.getStringExtra("numOfPeople")
        val keyWords = intent.getStringExtra("keyWords")
        val imageUrl = intent.getStringExtra("imageUrl")
        val join = intent.getStringExtra("join")
        val mapX = intent.getDoubleExtra("mapX", 0.0)
        val mapY = intent.getDoubleExtra("mapY", 0.0)
        if (join == "0") {
            binding.RoomInfoSubscriptionRoomBtn.visibility = View.VISIBLE
            binding.RoomInfoJoinRoomBtn.visibility = View.GONE
        } else {
            binding.RoomInfoSubscriptionRoomBtn.visibility = View.GONE
            binding.RoomInfoJoinRoomBtn.visibility = View.VISIBLE
        }

        if (roomStatus > 5) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            binding.RoomInformationStatus.text = "모집중"
        } else if (roomStatus > 0.9) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            val text: String = getString(R.string.room_status_imminent_time)
            binding.RoomInformationStatus.text =
                String.format(text, Math.round(roomStatus).toInt())

        } else if (roomStatus < 0.9 && roomStatus > 0.0) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline_imminent)
            binding.RoomInformationStatus.text = "임박"

        } else if (roomStatus < 0) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            binding.RoomInformationStatus.text = "마감"
            binding.RoomInfoSubscriptionRoomBtn.isEnabled = false
        }
        getMapImage(mapX, mapY, shopName, address)
        binding.RankingCircleView.load(getString(R.string.http_request_base_url) + imageUrl)
        binding.RoomInformationCategoryTitleTextview.text = title
        binding.RoomInfomationDate.text = date
        binding.RoomInformationCategoryIntroduceTextview.text = info
        binding.RoomInformationCategoryAddressTextview.text = address
        binding.RoomInformationCategoryNumOfPeopleTextview.text = "$nowNumOfPeople/${numOfPeople}명"
        binding.RoomInfoHostIdTextView.text = hostName
        binding.RoomInfoShopName.text = shopName


        binding.RankingCircleView.borderWidth = 20
//        binding.RankingGold.visibility = View.VISIBLE
        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)
//        binding.RankingCircleView.borderColor = Color.parseColor("#ffcd00")

        binding.RoomInfoJoinRoomBtn.setOnClickListener(View.OnClickListener {
//            val join = API()
//            join.joinRoom(this, roomId.toString(), MainActivity.loginUserNickname)
            mapClick = false
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("roomId", roomId)
            startActivity(intent)


        })


    }

    override fun onStop() {
        super.onStop()
        if (mapClick) {
        } else {
            finish()
        }
    }

    fun getMapImage(x: Double?, y: Double?, placeName: String?, address: String) {
        var w = 400
        var h = 400
        var center = "126.978082,37.565577"
        var place: String? = null
        var marker: String? = null
        if (x != null && y != null) {
            center = "$x $y"
            place = "|label:$placeName"
            marker =
                "type:t${place}|size:mid|pos:$x $y|viewSizeRatio:2.0"
        }


        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java).getStaticMap(
            "kqfai8b97u",
            "NyaUzYcb3IWf1GKPNFDTYJTHIg9SUNtciSstiv5m",
            w,
            h,
            14,
            "basic",
            marker,
            center,
            2
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val input: InputStream = response.body()!!.byteStream()
//                val bufferedInputStream = BufferedInputStream(input)
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
                binding.RoomInfoMapImageView.setImageBitmap(bitmap)
                binding.RoomInfoMapImageView.setOnClickListener {
                    mapClick = true
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("http://map.naver.com/?query=$address")
                    startActivity(i)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StaticMap", "실패")
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