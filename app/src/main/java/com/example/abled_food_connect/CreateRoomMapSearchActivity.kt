package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SearchView
import com.example.abled_food_connect.databinding.ActivityCreateRoomMapSearchBinding
import net.daum.mf.map.api.MapView


class CreateRoomMapSearchActivity : AppCompatActivity() {
    lateinit var map: MapView
    lateinit var mapview: ViewGroup
    private val binding by lazy { ActivityCreateRoomMapSearchBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        binding.searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        map = MapView(this@CreateRoomMapSearchActivity)
        mapview = binding.CreateRoomMapSearchMapView as ViewGroup
        mapview.addView(map)
    }
}