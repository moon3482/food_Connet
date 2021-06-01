package com.example.abled_food_connect


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.abled_food_connect.databinding.ActivityRoomInformationBinding
import com.example.abled_food_connect.retrofit.API

class RoomInformationActivity : AppCompatActivity() {
    val binding by lazy { ActivityRoomInformationBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = binding.root
        setContentView(view)
        val intent = intent
        val roomId = intent.getStringExtra("roomId")
        val title = intent.getStringExtra("title")
        val info = intent.getStringExtra("info")
        val hostName = intent.getStringExtra("hostName")
        val address = intent.getStringExtra("address")
        val date = intent.getStringExtra("date")
        val shopName = intent.getStringExtra("shopName")
        val roomStatus = intent.getDoubleExtra("roomStatus", 0.0);
        val numOfPeople = intent.getStringExtra("numOfPeople")
        val keyWords = intent.getStringExtra("keyWords")
        val imageUrl = intent.getStringExtra("imageUrl")


        if (roomStatus > 5) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            binding.RoomInformationStatus.text = "모집중"
        } else if (roomStatus > 0.9) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            val text: String = getString(R.string.room_status_imminent_time)
            binding.RoomInformationStatus.text =
                String.format(text, Math.round(roomStatus).toInt())

        } else if (roomStatus < 0.9 && roomStatus > 0.0) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            binding.RoomInformationStatus.text = "임박"

        } else if (roomStatus < 0) {
            binding.RoomInformationStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            binding.RoomInformationStatus.text = "마감"
            binding.RoomInfoJoinRoomBtn.isEnabled = false
        }
        binding.RankingCircleView.load(getString(R.string.http_request_base_url) + imageUrl)
        binding.RoomInformationCategoryTitleTextview.text = title
        binding.RoomInfomationDate.text = date
        binding.RoomInformationCategoryIntroduceTextview.text = info
        binding.RoomInformationCategoryAddressTextview.text = address
        binding.RoomInformationCategoryNumOfPeopleTextview.text = numOfPeople + "명"
        binding.RoomInfoHostIdTextView.text = hostName
        binding.RoomInfoShopName.text = shopName


        binding.RankingCircleView.borderWidth = 20
//        binding.RankingGold.visibility = View.VISIBLE
        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)
//        binding.RankingCircleView.borderColor = Color.parseColor("#ffcd00")

        binding.RoomInfoJoinRoomBtn.setOnClickListener(View.OnClickListener {
            val join = API()
            join.joinRoom(this, roomId.toString(), MainActivity.loginUserNickname)

        })


    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}