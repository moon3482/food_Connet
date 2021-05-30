package com.example.abled_food_connect

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.abled_food_connect.data.naverDataClass.NaverSearchLocal
import com.example.abled_food_connect.databinding.ActivityCreateRoomMapSearchBinding
import com.example.abled_food_connect.retrofit.NaverMapSearch
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CreateRoomMapSearchActivity : AppCompatActivity(),OnMapReadyCallback {

    private val binding by lazy { ActivityCreateRoomMapSearchBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.CreateRoomMapSearchMapView) as MapFragment?
            ?: MapFragment.newInstance().also { fm.beginTransaction().add(R.id.CreateRoomMapSearchMapView,it).commit() }

        mapFragment.getMapAsync(this)



        binding.searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                search()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }

    override fun onMapReady(p0: NaverMap) {


    }
    fun search(){

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(NaverMapSearch::class.java)
        server.mapSearch(getString(R.string.naver_open_api_id),getString(R.string.naver_open_api_secret),"관악구 김밥천국",5,1).enqueue(object :Callback<NaverSearchLocal>{
            override fun onResponse(
                call: Call<NaverSearchLocal>,
                response: Response<NaverSearchLocal>
            ) {
                val list:NaverSearchLocal? = response.body()!!
                Log.e("지도검색", list?.items.toString())


            }

            override fun onFailure(call: Call<NaverSearchLocal>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


    }
    /**
     * Retrofit.Builder Client 옵션 메소드
     */
    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
}