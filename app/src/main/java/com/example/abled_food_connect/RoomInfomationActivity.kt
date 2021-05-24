package com.example.abled_food_connect

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.abled_food_connect.databinding.ActivityRoomInfomationBinding

class RoomInfomationActivity : AppCompatActivity() {
    val binding by lazy { ActivityRoomInfomationBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = binding.root
        setContentView(view)

        binding.RankingCircleView.borderWidth = 20
        binding.RankingCircleView.borderColor = getColor(R.color.app_theme_color)

    }
}