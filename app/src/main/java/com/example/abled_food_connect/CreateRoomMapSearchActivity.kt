package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.example.abled_food_connect.databinding.ActivityCreateRoomMapSearchBinding
import net.daum.mf.map.api.MapView


class CreateRoomMapSearchActivity : AppCompatActivity() {
    lateinit var map : MapView
    lateinit var mapview :ViewGroup
    private val binding by lazy { ActivityCreateRoomMapSearchBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        map = MapView(this@CreateRoomMapSearchActivity)
        mapview = binding.CreateRoomMapSearchMapView as ViewGroup
        mapview.addView(map)
    }
}