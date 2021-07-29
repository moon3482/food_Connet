package com.example.abled_food_connect

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abled_food_connect.adapter.MainFragmentAdapter
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.databinding.ActivityRoomSearchBinding
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RoomSearchActivity : AppCompatActivity() {
    lateinit var binding: ActivityRoomSearchBinding
    lateinit var mAdapter: MainFragmentAdapter
    lateinit var mArrayList: ArrayList<MainFragmentItemData>
    lateinit var searchType: String
    private lateinit var imm: InputMethodManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mArrayList = ArrayList()

        setSupportActionBar(binding.RoomSearchToolbar)
        val tb = supportActionBar!!
        tb.title = "방 검색"

        binding.RoomSearchRCV.layoutManager = LinearLayoutManager(this)


        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val searchTypeArray: Array<String> = resources.getStringArray(R.array.roomSearchArray)
        binding.RoomSearchSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            searchTypeArray
        )

        binding.RoomSearchSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    searchType = searchTypeArray[position]
                    Log.e("룸서치액티비티", searchType)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        binding.RoomSearchSpinner.setSelection(0)

        binding.RoomSearchEditText.setOnClickListener {
            binding.RoomSearchEditText.onActionViewExpanded()
        }
        binding.RoomSearchEditText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    roomSearch(searchType, query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
        binding.RoomSearchEditText.onActionViewExpanded()

    }

    private fun roomSearch(type: String, content: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        retrofit.create(RoomAPI::class.java).roomSearch(type, content)
            .enqueue(object : Callback<LoadingRoom> {
                override fun onResponse(
                    call: Call<LoadingRoom>,
                    response: Response<LoadingRoom>
                ) {
                    mArrayList.clear()
                    val list: LoadingRoom = response.body()!!
                    mArrayList.addAll(list.roomList)
                    mAdapter = MainFragmentAdapter(this@RoomSearchActivity, mArrayList)
                    binding.RoomSearchRCV.adapter = mAdapter
                    mAdapter.filter.filter(null)
                    Log.e("리스트사이즈", mArrayList.size.toString())
                    Log.e("리스트사이즈", mAdapter.unList.size.toString())

                }

                override fun onFailure(call: Call<LoadingRoom>, t: Throwable) {

                }
            })

    }

    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
}