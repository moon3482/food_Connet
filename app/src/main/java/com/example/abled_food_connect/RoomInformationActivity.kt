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

        binding.RankingCircleView.borderWidth = 20
        binding.RankingGold.visibility = View.VISIBLE
//        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)
        binding.RankingCircleView.borderColor = Color.parseColor("#ffcd00")


    }
}