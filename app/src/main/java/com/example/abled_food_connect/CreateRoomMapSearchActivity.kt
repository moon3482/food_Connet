package com.example.abled_food_connect

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.abled_food_connect.data.Cluster.ClusterDataClass
import com.example.abled_food_connect.data.kakaoDataClass.Document
import com.example.abled_food_connect.data.kakaoDataClass.KakaoLocalSearch
import com.example.abled_food_connect.data.Cluster.ClusterMarkerData
import com.example.abled_food_connect.data.naverDataClass.NaverSearchLocal
import com.example.abled_food_connect.databinding.ActivityCreateRoomMapSearchBinding
import com.example.abled_food_connect.retrofit.MapSearch
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
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
    lateinit var markerList: ArrayList<Marker>
    var intentX: Double = 0.0
    var intentY: Double = 0.0
    lateinit var intentShopName: String
    lateinit var intentAddress: String
    lateinit var intentRoadAddress: String
    lateinit var cluster: TedNaverClustering<ClusterDataClass>
    lateinit var CLlist: ArrayList<ClusterDataClass>
    lateinit var infoWindow: InfoWindow
    lateinit var pickMarker: ClusterDataClass
    lateinit var array: ArrayList<ClusterMarkerData>


    companion object {
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        array = ArrayList()
        context = this
        pickMarker =
            ClusterDataClass(0.0, 0.0, Document("", "", "", "", "", "", "", "", "", "", "", ""))
        markerList = ArrayList()
        infoWindow = InfoWindow()
        CLlist = ArrayList()

        val fm = supportFragmentManager
        mapFragment = fm.findFragmentById(R.id.CreateRoomMapSearchMapView) as MapFragment?
            ?: MapFragment.newInstance()
                .also { fm.beginTransaction().add(R.id.CreateRoomMapSearchMapView, it).commit() }

        mapFragment.getMapAsync(this)
        intentShopName = ""




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

        binding.CreateRoomMapSearchSelectButton.setOnClickListener(View.OnClickListener {
            if (intentShopName.length != 0) {

                val intent = Intent(this, CreateRoomActivity::class.java)
                intent.putExtra("x", intentX)
                intent.putExtra("y", intentY)
                intent.putExtra("shopName", intentShopName)
                intent.putExtra("address", intentAddress)
                intent.putExtra("roadAddress", intentRoadAddress)

                setResult(RESULT_OK, intent)
                finish()
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("장소가 선택되지 않았습니다.")
                dialog.setPositiveButton("확인", null)
                dialog.show()
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

                    mapFragment.getMapAsync { naverMap ->
                        var builder = LatLngBounds.Builder()
                        if (CLlist.size > 0) {
                            CLlist.clear()
                            cluster.clearItems()

                        }
                        var x: Double = 0.0
                        var y: Double = 0.0
                        if (list?.documents?.size != 0) {
                            for (index in list?.documents!!.indices) {


                                x += documents!![index].x.toDouble()
                                y += documents[index].y.toDouble()
//                            val marker = Marker()
                                val position = LatLng(
                                    documents!![index].y.toDouble(),
                                    documents!![index].x.toDouble()
                                )
                                builder.include(position)
                                CLlist.add(
                                    ClusterDataClass(
                                        position,
                                        documents[index].placeName,
                                        documents[index]
                                    )
                                )
//                            marker.position = position
//                            marker.map = it
//                            val infoWindow = InfoWindow()
//                            infoWindow.adapter = object :
//                                InfoWindow.DefaultTextAdapter(this@CreateRoomMapSearchActivity) {
//                                override fun getText(p0: InfoWindow): CharSequence {
//                                    return documents[index].placeName
//                                }
//                            }
//
//                            infoWindow.open(marker)
//                            marker.onClickListener = object : Overlay.OnClickListener {
//                                override fun onClick(p0: Overlay): Boolean {
//                                    p0.map!!.moveCamera(
//                                        CameraUpdate.scrollTo(
//                                            LatLng(
//                                                documents!![index].y.toDouble(),
//                                                documents!![index].x.toDouble()
//                                            )
//                                        ).animate(CameraAnimation.Easing)
//                                    )
//                                    selectMarker.icon = MarkerIcons.GREEN
//                                    selectMarker = marker
//                                    selectMarker.icon = MarkerIcons.RED
//                                    selectMarker.map = it
//                                    intentY = documents[index].y.toDouble()
//                                    intentX = documents[index].x.toDouble()
//                                    intentShopName = documents[index].placeName
//                                    intentAddress = documents[index].addressName
//                                    intentRoadAddress = documents[index].roadAddressName
//
//                                    return true
//                                }
//                            }
//
//                            markerList.add(marker)
//                            Log.e("어레이 사이즈", markerList.size.toString())

                            }

                            cluster = TedNaverClustering.with<ClusterDataClass>(
                                this@CreateRoomMapSearchActivity,
                                naverMap
                            ).markerAddedListener { clusterItem, tedNaverMarker ->
                                if (clusterItem.status == 1) {
                                    array[0].marker = tedNaverMarker
                                    array[0].clustetdata = clusterItem
                                    tedNaverMarker.marker.icon = MarkerIcons.RED
                                }
                                val info = InfoWindow()
                                info.adapter = object :
                                    InfoWindow.DefaultTextAdapter(this@CreateRoomMapSearchActivity) {
                                    override fun getText(p0: InfoWindow): CharSequence {
                                        return clusterItem.name
                                    }
                                }
                                info.open(tedNaverMarker.marker)


                                tedNaverMarker.marker.setOnClickListener {
                                    if (array.size > 0) {
                                        for (index in array.indices) {
                                            array[index].clustetdata.status = 0
                                            array[index].marker.marker.icon = MarkerIcons.GREEN
                                            array.removeAt(index)
                                        }
                                    }

                                    clusterItem.status = 1
                                    tedNaverMarker.marker.icon = MarkerIcons.RED
                                    intentX = clusterItem.position.longitude
                                    intentY = clusterItem.position.latitude
                                    intentShopName = clusterItem.document.placeName
                                    intentRoadAddress = clusterItem.document.roadAddressName
                                    intentAddress = clusterItem.document.addressName
                                    array.add(ClusterMarkerData(clusterItem, tedNaverMarker))
                                    naverMap.moveCamera(
                                        CameraUpdate.scrollTo(clusterItem.position)
                                            .animate(CameraAnimation.Easing)
                                    )
                                    true
                                }
                            }.items(getItems(naverMap, CLlist)).minClusterSize(3).clusterBuckets(
                                intArrayOf(
                                    5,
                                    10,
                                    15,
                                    20,
                                    25,
                                    30,
                                    35,
                                    40,
                                    45,
                                    50,
                                    55,
                                    60,
                                    65,
                                    70,
                                    75,
                                    80,
                                    85,
                                    90,
                                    95,
                                    100
                                )
                            ).make()

                            val buildMap: LatLngBounds = builder.build()
                            if (documents != null) {
                                Log.e(
                                    "마커 로그 평균",
                                    "x : " + x / documents.size + "y : " + y / documents.size
                                )
                                naverMap.moveCamera(
                                    CameraUpdate.fitBounds(buildMap, 300)
                                        .animate(CameraAnimation.Easing)
                                )
                            }
                        }else{
                            val dialog = AlertDialog.Builder(this@CreateRoomMapSearchActivity)
                            dialog.setTitle("검색된 결과가 없습니다.")
                            dialog.setPositiveButton("확인", null)
                            dialog.show()
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



