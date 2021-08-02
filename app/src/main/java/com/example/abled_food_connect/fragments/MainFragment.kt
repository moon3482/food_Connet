package com.example.abled_food_connect.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.RoomSearchActivity
import com.example.abled_food_connect.adapter.MainFragmentAdapter
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainFragment : Fragment() {
    private var mainFragmentListArray: ArrayList<MainFragmentItemData> = ArrayList()
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: MainFragmentAdapter
    lateinit var hideRoom: LinearLayout
    lateinit var checkImage: ImageView
    private var check: Boolean = false
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var refreshTextView:SwipeRefreshLayout

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
        val pref = requireContext().getSharedPreferences("pref_user_data", 0)
        MainActivity.user_table_id = pref.getInt("user_table_id", 0)
        MainActivity.loginUserId = pref.getString("loginUserId", "")!!

        hideRoom = view.findViewById(R.id.hideJoinRoom)
        checkImage = view.findViewById(R.id.hideRoomCheck)
        swipeRefresh = view.findViewById(R.id.mainFragmentSwipeRefresh)
        refreshTextView = view.findViewById(R.id.mainFragmentSwipeRefreshTextView)
        recyclerView = view.findViewById(R.id.mainRcv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager(this.context).orientation
            )
        )
        swipeRefresh.setOnRefreshListener {
            load()

        }
        refreshTextView.setOnRefreshListener {
            load()
        }
        hideRoom.setOnClickListener {
            check = when (check) {
                false -> {
                    recyclerViewAdapter.filter.filter(MainActivity.user_table_id.toString())
                    checkImage.setImageResource(R.drawable.ic_baseline_check_circle_24)
                    true
                }
                else -> {
                    recyclerViewAdapter.filter.filter(null)
                    checkImage.setImageResource(R.drawable.ic_baseline_noncheck_circle_24)
                    false
                }


            }


        }

        setHasOptionsMenu(true)
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
        load()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.room_search,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.toolbarSearchButton -> {
                activity.let {
                    val intent = Intent(context,RoomSearchActivity::class.java)
                    startActivity(intent)
                }
            }
            else->{

            }
        }
        return super.onOptionsItemSelected(item)
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
                    mainFragmentListArray = list.roomList
                    recyclerViewAdapter =
                        MainFragmentAdapter(requireContext(),this@MainFragment, mainFragmentListArray)
                    recyclerView.adapter = recyclerViewAdapter
                    swipeRefresh.isRefreshing = false
                    refreshTextView.isRefreshing = false
                    if(check){
                        recyclerViewAdapter.filter.filter(MainActivity.user_table_id.toString())
                    }else{
                        recyclerViewAdapter.filter.filter(null)
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
