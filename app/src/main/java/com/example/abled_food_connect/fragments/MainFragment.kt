package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.MainFragmentActivity
import com.example.abled_food_connect.adapter.MainFragmentAdapter
import com.example.abled_food_connect.R
import com.example.abled_food_connect.retrofit.RoomAPI
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.internal.notify
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainFragment : Fragment() {
    private var mainFragmentListArray: ArrayList<MainFragmentItemData> = ArrayList()
    lateinit var recyclerView: RecyclerView

    companion object {
        const val TAG: String = "홈 프래그먼트 로그"
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onCreate()")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "메인프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "메인프래그먼트 onCreateView()")
        val view = inflater.inflate(R.layout.main_fragment, container, false)
//        mainFragmentListArray.add(MainFragmentItemData("제목","정보",0, "","","","","","male",0,20,"나야",0))
//        mainFragmentListArray.add(MainFragmentItemData("제목1","정보1",0, "","","","","","female",0,20,"나야",1))
//        mainFragmentListArray.add(MainFragmentItemData("제목2","정보2",0, "","","","","","any",0,20,"나야",2))
//        mainFragmentListArray.add(MainFragmentItemData("제목3","정보3",0, "","","","","","male",0,20,"나야",3))
//        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",6))
//        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",6))
//        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",6))
//        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",6))
//        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",6))
//        mainFragmentListArray.add(MainFragmentItemData("제목4","정보4",0, "","","","","","male",0,20,"나야",6))


        recyclerView = view.findViewById(R.id.mainRcv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MainFragmentAdapter(requireContext(), mainFragmentListArray)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager(this.context).orientation
            )
        )
        load()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onActivityCreated()")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "메인프래그먼트 onViewCreated()")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "메인프래그먼트 onStart()")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "메인프래그먼트 onResume()")


    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "메인프래그먼트 onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "메인프래그먼트 onStop()")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "메인프래그먼트 onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "메인프래그먼트 onDestroy()")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "메인프래그먼트 onDetach()")
    }

    fun load() {

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.loadingRoomGet(MainActivity.loginUserId)
            .enqueue(object : Callback<LoadingRoom> {
                override fun onResponse(
                    call: Call<LoadingRoom>,
                    response: Response<LoadingRoom>
                ) {

                    val list: LoadingRoom = response.body()!!
                    val array: ArrayList<MainFragmentItemData> = list.roomList
                    for (i in 0 until array.size) {
                        mainFragmentListArray.add(array.get(i))
                        recyclerView.adapter!!.notifyDataSetChanged()

                    }


                }

                override fun onFailure(call: Call<LoadingRoom>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "통신실패:" + t.printStackTrace(),
                        Toast.LENGTH_SHORT
                    ).show()
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
