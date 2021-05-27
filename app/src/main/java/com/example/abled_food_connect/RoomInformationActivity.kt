package com.example.abled_food_connect


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.abled_food_connect.databinding.ActivityRoomInformationBinding

class RoomInformationActivity : AppCompatActivity() {
    val binding by lazy { ActivityRoomInformationBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = binding.root
        setContentView(view)
        val intent = intent
        val title = intent.getStringExtra("title")
        val info = intent.getStringExtra("info")
        val hostName = intent.getStringExtra("hostName")
        val address = intent.getStringExtra("address")
        val date = intent.getStringExtra("date")
        val shopName = intent.getStringExtra("shopName")
        val roomStatus = intent.getDoubleExtra("roomStatus",0.0);
        val numOfPeople = intent.getStringExtra("numOfPeople")
        val keyWords = intent.getStringExtra("keyWords")


        binding.RankingCircleView.borderWidth = 20
        binding.RankingGold.visibility = View.VISIBLE
//        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)
        binding.RankingCircleView.borderColor = Color.parseColor("#ffcd00")


    }

}