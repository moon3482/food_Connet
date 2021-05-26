package com.example.abled_food_connect


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.abled_food_connect.databinding.ActivityRoomInformationBinding

class RoomInformationActivity : AppCompatActivity() {
    val binding by lazy { ActivityRoomInformationBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = binding.root
        setContentView(view)

        binding.RankingCircleView.borderWidth = 20
        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)

    }
}