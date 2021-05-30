package com.example.abled_food_connect

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.abled_food_connect.data.ClusterDataClass
import com.example.abled_food_connect.data.kakaoDataClass.KakaoLocalSearch
import com.example.abled_food_connect.data.naverDataClass.NaverSearchLocal
import com.example.abled_food_connect.databinding.ActivityCreateRoomMapSearchBinding
import com.example.abled_food_connect.retrofit.MapSearch
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ted.gun0912.clustering.naver.TedNaverClustering


class CreateRoomMapSearchActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var mapFragment: MapFragment
    private val binding by lazy { ActivityCreateRoomMapSearchBinding.inflate(layoutInflater) }
    lateinit var naverMap: NaverMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)


        val fm = supportFragmentManager
        mapFragment = fm.findFragmentById(R.id.CreateRoomMapSearchMapView) as MapFragment?
            ?: MapFragment.newInstance()
                .also { fm.beginTransaction().add(R.id.CreateRoomMapSearchMapView, it).commit() }

        mapFragment.getMapAsync(this)





        binding.searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    mapFragment.getMapAsync(this@CreateRoomMapSearchActivity)
                    kakaoSearch(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }

    override fun onMapReady(p0: NaverMap) {


    }

    fun search(query: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java)
        server.naverMapSearch(
            getString(R.string.naver_open_api_id),
            getString(R.string.naver_open_api_secret),
            "관악구 김밥천국",
            5,
            1
        ).enqueue(object : Callback<NaverSearchLocal> {
            override fun onResponse(
                call: Call<NaverSearchLocal>,
                response: Response<NaverSearchLocal>
            ) {
                val list: NaverSearchLocal? = response.body()!!
                Log.e("지도검색", list?.items.toString())


            }

            override fun onFailure(call: Call<NaverSearchLocal>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


    }

    fun kakaoSearch(query: String) {
        var mapFragment: MapFragment
        val fm = supportFragmentManager
        mapFragment = fm.findFragmentById(R.id.CreateRoomMapSearchMapView) as MapFragment?
            ?: MapFragment.newInstance()
                .also { fm.beginTransaction().add(R.id.CreateRoomMapSearchMapView, it).commit() }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java)
        server.kakaoMapSearch(getString(R.string.kakao_rest_api_key), query.toString())
            .enqueue(object : Callback<KakaoLocalSearch> {
                override fun onResponse(
                    call: Call<KakaoLocalSearch>,
                    response: Response<KakaoLocalSearch>
                ) {
                    val list: KakaoLocalSearch? = response.body()!!
                    val documents = list?.documents
                    var markerList: ArrayList<ClusterDataClass>
                    mapFragment.getMapAsync {


                        var x: Double = 0.0
                        var y: Double = 0.0
                        markerList = ArrayList()
                        for (index in list?.documents!!.indices) {


                            x += documents!![index].x.toDouble()
                            y += documents[index].y.toDouble()
                            val marker = ClusterDataClass(

                                documents!![index].y.toDouble(),
                                documents!![index].x.toDouble(),
                                documents[index].placeName,
                                documents[index].placeName
                            )
                            markerList.add(marker)


                        }
                        TedNaverClustering.with<ClusterDataClass>(
                            this@CreateRoomMapSearchActivity,
                            it
                        )
                            .customMarker { clusterItem ->
                          val marker = Marker(clusterItem.position)
                                marker.apply {
                                    this.icon = MarkerIcons.GREEN

                                }




                            }.markerClickListener {


                            }
//
//
//                        }.customCluster {
//                            TextView(this@CreateRoomMapSearchActivity).apply {
//                                setBackgroundColor(
//                                    Color.GREEN
//                                )
//                                text = "${it.size}"
//                                setPadding(10, 10, 10, 10)
//                            }
//                        }.clusterText { "테스트" }
                            .items(getItems(it, markerList)).make()

                        if (documents != null) {
                            Log.e(
                                "마커 로그 평균",
                                "x : " + x / documents.size + "y : " + y / documents.size
                            )
                            it.moveCamera(
                                CameraUpdate.scrollTo(
                                    LatLng(
                                        y / documents.size,
                                        x / documents.size
                                    )
                                )
                            )
                        }
                    }

                }

                override fun onFailure(call: Call<KakaoLocalSearch>, t: Throwable) {
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

    private fun getItems(
        naverMap: NaverMap,
        list: ArrayList<ClusterDataClass>
    ): ArrayList<ClusterDataClass> {
        val bounds = naverMap.contentBounds
        return list
    }
}



