package com.example.abled_food_connect

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.lujun.androidtagview.TagView
import com.example.abled_food_connect.array.Age
import com.example.abled_food_connect.array.MaximumAge
import com.example.abled_food_connect.array.MinimumAge
import com.example.abled_food_connect.array.NumOfPeople
import com.example.abled_food_connect.databinding.ActivityCreateRoomActivityBinding
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.MapSearch
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import net.daum.android.map.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateRoomActivity : AppCompatActivity() {
    lateinit var mapFragment: MapFragment
    val binding by lazy { ActivityCreateRoomActivityBinding.inflate(layoutInflater) }
    private var genderMaleSelected: Boolean = false
    private var genderFemaleSelected: Boolean = false
    private var genderAnySelected: Boolean = false
    private val SEARCHMAPRESULTCODE = 110
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION


    )
    private val ACCESS_FINE_LOCATION = 1000
    private var x: Double? = null
    private var y: Double? = null
    /*?????? ?????????*/val PERMISSIONS_REQUEST_CODE = 100
    val BACKGROUND_PERMISSIONS_REQUEST_CODE = 1110
    var tagArray: ArrayList<String> = ArrayList()
    lateinit var marker: Marker
    lateinit var placeName: String
    lateinit var address: String
    lateinit var roadAddress: String
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        marker = Marker()
        /*????????? ??? API*/
//        map = MapView(this)
//        mapview = binding.mapView
//        mapview.addView(map)
        /*????????? ??? ?????????*/
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput
        val maximum = binding.maximumAgeTextView
        val minimum = binding.minimumAgeTextView
        placeName = String()
        context = this

        /*???????????? ????????? ??????*/
        maximum.setAdapter(setAdapter(Age()))
        minimum.setAdapter(setAdapter(Age()))
        numOfPeople.setAdapter(setAdapter(NumOfPeople()))

        onClickListenerGroup()



        getMapImage(null, null, null)

    }


    override fun onStart() {
        super.onStart()
        if (checkLocationService()) {
            // GPS??? ???????????? ??????
            permissionCheck()
        } else {
            // GPS??? ???????????? ??????
            Toast.makeText(this, "GPS??? ????????????", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
//        checkBackgroundLocationPermissionAPI30(PERMISSIONS_REQUEST_CODE)

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()


    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /*


    ????????? ??????



    */

    /**
     * ????????? ?????????
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var check_result = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]

                    )
                ) {
                    Toast.makeText(
                        this,
                        "?????? ????????? ?????????????????????.\n?????? ?????????????????? ?????? ??????????????????.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {

                    Toast.makeText(this, "?????? ????????? ?????????????????????.\n???????????? ????????? ???????????? ?????????..", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

//        if (requestCode == ACCESS_FINE_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // ?????? ?????? ??? ????????? (?????? ??????)
//                Toast.makeText(this, "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show()
//
//            } else {
//                // ?????? ?????? ??? ????????? (?????? ?????? or ?????????)
//                Toast.makeText(this, "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show()
//                permissionCheck()
//            }
//        }
    }

    /**
     * ???????????? ????????? ??????
     * */
    private fun checkPermissions() {
        //?????????????????? ?????? ???????????? ?????? ??????(?????????)??? ????????? ????????? ?????? ?????????
        var rejectedPermissionList = ArrayList<String>()

        //????????? ??????????????? ????????? ??????????????? ?????? ????????? ???????????? ??????
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //?????? ????????? ????????? rejectedPermissionList??? ??????
                rejectedPermissionList.add(permission)
            }
        }
        //????????? ???????????? ?????????...
        if (rejectedPermissionList.isNotEmpty()) {
            //?????? ??????!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(
                this,
                rejectedPermissionList.toArray(array),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }


    /**
     * ?????? ?????????
     * */
    private fun setAdapter(Age: Age): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, Age.numArray())

    }

    private fun setAdapter(age: MinimumAge, num: Int): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, age.numArray(num))

    }

    private fun setAdapter(age: MaximumAge, num: Int): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, age.numArray(num))

    }

    /**
     * ???????????? ?????????
     * */
    private fun setAdapter(NumOfPeople: NumOfPeople): ArrayAdapter<Int> {


        return ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            NumOfPeople.numArray(3)
        )

    }

    /**
     *?????? ?????? ?????? ?????????
     */
    private fun inputCheck(): Boolean {

        if (binding.CreateRoomActivityRoomTitleInput.length() == 0) {
            Toast.makeText(this, "??? ????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, 0)
            binding.CreateRoomTitleInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityRoomInformationInput.length() == 0) {
            Toast.makeText(this, "??? ????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomTitleInputLayout.bottom)
            binding.CreateRoomInformationInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityNumOfPeopleInput.length() == 0) {
            Toast.makeText(this, "?????? ???????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomInformationInputLayout.bottom)
            binding.CreateRoomActivityNumOfPeopleInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityDateInput.length() == 0) {
            Toast.makeText(this, "????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityDateInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityTimeInput.length() == 0) {
            Toast.makeText(this, "????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityTimeInput.requestFocus()
            return false
        } else if (!timeCompare("${binding.CreateRoomActivityDateInput.text.toString()} ${binding.CreateRoomActivityTimeInput.text.toString()}")) {
            Toast.makeText(this, "????????? ????????? ?????? ????????? ???????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)

            return false
        } else if (placeName.isEmpty()) {
            Toast.makeText(this, "?????? ????????? ?????? ??????????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityTimeInput.bottom)
            return false
        } else if (!genderMaleSelected && !genderFemaleSelected && !genderAnySelected) {
            Toast.makeText(this, "?????? ?????? ??????????????????", Toast.LENGTH_LONG).show()
            return false
        } else if (binding.minimumAgeTextView.length() == 0) {
            Toast.makeText(this, "??????????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.minimumAgeTextView.requestFocus()
            return false

        } else if (binding.maximumAgeTextView.length() == 0) {
            Toast.makeText(this, "??????????????? ??????????????????", Toast.LENGTH_LONG).show()
            binding.maximumAgeTextView.requestFocus()
            return false

        } else if (binding.maximumAgeTextView.text.toString()
                .toInt() < binding.minimumAgeTextView.text.toString().toInt()
        ) {
            Toast.makeText(this, "??????????????? ???????????? ?????? ?????? ??? ????????????.", Toast.LENGTH_LONG).show()
            binding.maximumAgeTextView.requestFocus()
            return false
        } else {
            return true
        }
    }


    /**
     * ?????? ?????? ?????? ?????????
     */
    private fun limitGender(gender: String): Boolean {

        when (gender) {
            "male" -> {
                binding.CreateRoomActivityMaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_male",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityFemaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_female_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityGenderAnyImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_maleandfemale_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                genderFemaleSelected = false
                genderAnySelected = false
                return true
            }
            "female" -> {
                binding.CreateRoomActivityMaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_male_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityFemaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_female",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityGenderAnyImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_maleandfemale_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                genderMaleSelected = false
                genderAnySelected = false
                return true
            }
            else -> {
                binding.CreateRoomActivityMaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_male_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityFemaleImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_female_not_focus",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                binding.CreateRoomActivityGenderAnyImageView.setImageResource(
                    resources.getIdentifier(
                        "ic_maleandfemale",
                        "drawable",
                        "com.example.abled_food_connect"
                    )
                )
                genderFemaleSelected = false
                genderMaleSelected = false
                return true
            }
        }
    }

    /**
     * ????????? ????????? ????????? ????????????
     */
    private fun createRoom() {
        val tile = binding.CreateRoomActivityRoomTitleInput.text.toString()
        val info = binding.CreateRoomActivityRoomInformationInput.text.toString()
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput.text.toString()
        val date = binding.CreateRoomActivityDateInput.text.toString()
        val time = binding.CreateRoomActivityTimeInput.text.toString()
        val address = address
        val roadAddress = roadAddress
        val placeName = placeName
        val shopName = placeName
        val keyWord = tagArray.toString()
        var gender: String = ""
        var mapx = x?.toDouble()
        var mapy = y?.toDouble()

        if (genderMaleSelected) {
            gender = "male"
        } else if (genderFemaleSelected) {
            gender = "female"
        } else if (genderAnySelected) {
            gender = "any"
        }

        val minAge = binding.minimumAgeTextView.text.toString()
        val maxAge = binding.maximumAgeTextView.text.toString()
        val hostName = MainActivity.loginUserNickname

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.createRoom(
            MainActivity.user_table_id.toString(),
            tile,
            info,
            numOfPeople,
            date,
            time,
            address,
            roadAddress,
            placeName,
            shopName,
            keyWord,
            gender,
            minAge,
            maxAge,
            hostName,
            MainActivity.user_table_id,
            mapx.toString(),
            mapy.toString()
        )
            .enqueue(object : Callback<API.createRoomHost> {
                override fun onResponse(
                    call: Call<API.createRoomHost>,
                    response: Response<API.createRoomHost>
                ) {

                    val room: API.createRoomHost? = response.body()

                    if (room!!.success) {
                        Toast.makeText(this@CreateRoomActivity, "??? ??????", Toast.LENGTH_SHORT).show()

                        val intent =
                            Intent(this@CreateRoomActivity, RoomInformationActivity::class.java)
                        intent.putExtra("roomId", room.roomId.roomId)
                        intent.putExtra("title", room.roomId.title)
                        intent.putExtra("info", room.roomId.info)
                        intent.putExtra("hostName", room.roomId.hostName)
                        intent.putExtra("address", room.roomId.address)
                        intent.putExtra("date", room.roomId.date)
                        intent.putExtra("shopName", room.roomId.shopName)
                        intent.putExtra("roomStatus", room.roomId.roomStatus)
                        intent.putExtra("nowNumOfPeople", room.roomId.nowNumOfPeople)
                        intent.putExtra("hostIndex", room.roomId.hostIndex)
                        Log.e("nowNumOfPeople", room.roomId.nowNumOfPeople!!)
                        intent.putExtra("numOfPeople", room.roomId.numOfPeople)
                        intent.putExtra("keyWords", room.roomId.keyWords)
                        intent.putExtra("mapX", room.roomId.mapX)
                        intent.putExtra("mapY", room.roomId.mapY)
                        intent.putExtra("nowNumOfPeople", room.roomId.nowNumOfPeople.toString())
                        intent.putExtra("imageUrl", MainActivity.userThumbnailImage)
                        intent.putExtra("join", "1")
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<API.createRoomHost>, t: Throwable) {

                }

            })
    }

    /**
     * Retrofit.Builder Client ?????? ?????????
     */
    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    /**
     * ?????? ?????? ?????? ???????????? ??????????????? ?????????
     */
    private fun dateCalendarDialog() {
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var datePicker = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                binding.CreateRoomActivityDateInput.setText(
                    "${year}-${plusZero(month + 1)}-${
                        plusZero(
                            dayOfMonth
                        )
                    }"
                )
            }
        }

        var builder = DatePickerDialog(this, datePicker, year, month, day - 1)

        builder.datePicker.minDate = System.currentTimeMillis()
        builder.show()

    }

    /**
     * ???????????? ?????????
     */
    private fun timeCalendarDialog() {
        var time = Calendar.getInstance(Locale.KOREA)
        var hour = time.get(Calendar.HOUR)
        var minute = time.get(Calendar.MINUTE)

        var timePicker = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                binding.CreateRoomActivityTimeInput.setText(
                    "${plusZero(hourOfDay)}:${
                        plusZero(
                            minute
                        )
                    }"
                )
            }
        }
        var builder = TimePickerDialog(this, timePicker, hour, minute, true)

        builder.show()
    }

    /**
     * ?????? ?????????????????? ????????? ???????????? ??????
     */
    private fun timeCompare(time: String): Boolean {
        var simpleTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
        var now: Long = System.currentTimeMillis()
        var nowTime = simpleTime.format(Date(now))
        var transNowTime: Date = simpleTime.parse(nowTime)
        var settingTime: Date = simpleTime.parse(time)
        Log.e("?????? ??????", "${transNowTime}/${settingTime}")
        Log.e("??????", settingTime.after(transNowTime).toString())
        return settingTime.after(transNowTime)

    }

    /**
     * ?????????/?????? ???????????? ????????? ????????? 10?????? ?????? ??????
     * ?????? ?????? 0??? ?????? ??????????????? ???????????? ?????????
     */
    private fun plusZero(int: Int): String {
        return if (int < 10) {
            "0$int"
        } else {
            int.toString()
        }
    }

    /**
     * ????????? ???????????? ??????
     */
    private fun onClickListenerGroup() {
        /*???????????? ????????????*/
        binding.CreateRoomButton.setOnClickListener {
            if (inputCheck()) {
                createRoom()
            }
        }
        setSupportActionBar(binding.CreateRoomActivityToolbar)
        /*?????? ????????? ??????*/
        val tb = supportActionBar!!
        tb.title = "????????????"
        tb.setDisplayHomeAsUpEnabled(true)


        /*?????? ?????? ?????? ????????? ?????????*/
        binding.CreateRoomActivityMaleImageView.setOnClickListener {
            genderMaleSelected = limitGender("male")
        }
        /*?????? ?????? ?????? ????????? ?????????*/
        binding.CreateRoomActivityFemaleImageView.setOnClickListener {
            genderFemaleSelected = limitGender("female")
        }
        /*?????? ?????? ???????????? ????????? ?????????*/
        binding.CreateRoomActivityGenderAnyImageView.setOnClickListener {
            genderAnySelected = limitGender("any")
        }


        //?????? ????????? ????????? 0 ?????? ???????????? ?????????
        if (tagArray.size == 0) {
            binding.emptyKeyWordTextView.visibility = View.VISIBLE
        }
        /*?????? ????????? ????????? ?????????*/
        binding.CreateRoomActivityKeyWordInput.setOnClickListener {

        }
        /*?????? ?????????????????? ?????????????????????*/
        binding.CreateRoomActivityDateInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                dateCalendarDialog()
            } else {

            }
        }
        /*?????? ??????????????? ??????????????????*/
        binding.CreateRoomActivityDateInput.setOnClickListener {
            dateCalendarDialog()
        }
        /*?????? ?????????????????? ?????????????????????*/
        binding.CreateRoomActivityTimeInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                timeCalendarDialog()

            } else {

            }
        }
        /*?????? ??????????????? ??????????????????*/
        binding.CreateRoomActivityTimeInput.setOnClickListener {
            timeCalendarDialog()
        }
//        binding.time.setOnClickListener {
//            timeCalendarDialog()
//        }
//        binding.date.setOnClickListener {
//            dateCalendarDialog()
//        }
        /*?????? ?????? ?????? ??????????????????*/
        binding.tagAddButton.setOnClickListener {
            if (binding.CreateRoomActivityKeyWordInput.length() != 0) {
                /*?????? ???????????? ??????*/
                binding.tagLayout.addTag(binding.CreateRoomActivityKeyWordInput.text.toString())
                /*?????? ???????????? ????????? ??????*/
                tagArray.add(binding.CreateRoomActivityKeyWordInput.text.toString())
                /*?????? ????????? 0?????? ??? ??? ???????????? ?????????*/
                if (tagArray.size != 0) {
                    binding.emptyKeyWordTextView.visibility = View.GONE
                }
                /*?????? ?????? ??? ????????? ????????? ?????????*/
                binding.CreateRoomActivityKeyWordInput.setText("")
                /*????????? ?????????*/
                val scroll = Handler()
                scroll.post(Runnable {

                    binding.Scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    binding.CreateRoomActivityKeyWordInput.requestFocus()

                })


//                Toast.makeText(this, tagArray.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        /*?????? ????????? ?????????*/
        binding.tagLayout.isEnableCross = true
        binding.tagLayout.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {

            }

            override fun onTagLongClick(position: Int, text: String?) {


            }


            override fun onSelectedTagDrag(position: Int, text: String?) {
                TODO("Not yet implemented")
            }

            override fun onTagCrossClick(position: Int) {
                /*?????? ???????????? ??????*/
                binding.tagLayout.removeTag(position)
                /*?????? ??????????????? ?????? ????????? ??????*/
                tagArray.removeAt(position)
//                Toast.makeText(this@CreateRoomActivity, tagArray.toString(),Toast.LENGTH_SHORT).show()
                /*?????? ???????????? 0??? ??? ???????????? ?????????*/
                if (tagArray.size == 0) {
                    binding.emptyKeyWordTextView.visibility = View.VISIBLE
                }
            }
        })

        /*?????? ????????? ?????? ?????? ?????????*/
        binding.CreateRoomMapSearchButton.setOnClickListener {
            val intent = Intent(this, CreateRoomMapSearchActivity::class.java)
            startActivityForResult(intent, SEARCHMAPRESULTCODE)

        }

        binding.minimumAgeTextView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val num = binding.minimumAgeTextView.text.toString().toInt()
                binding.maximumAgeTextView.setAdapter(setAdapter(MaximumAge(), num))
            }
        })

        binding.maximumAgeTextView.setOnItemClickListener { parent, view, position, id ->
            val num = binding.maximumAgeTextView.text.toString().toInt()
            binding.minimumAgeTextView.setAdapter(setAdapter(MinimumAge(), num))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SEARCHMAPRESULTCODE -> {
                    x = data?.getDoubleExtra("x", 0.0)
                    y = data?.getDoubleExtra("y", 0.0)
                    placeName = data?.getStringExtra("shopName").toString()
                    address = data?.getStringExtra("address").toString()
                    roadAddress = data?.getStringExtra("roadAddress").toString()
                    Log.e("????????????", placeName)
                    getMapImage(x, y, placeName)
                    binding.CreateRoomActivityMapView.setOnClickListener {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse("http://map.naver.com/?query=$address")
                        startActivity(i)
                    }

                }


            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
            else->{}
        }

        return super.onOptionsItemSelected(item)
    }


    fun getMapImage(x: Double?, y: Double?, placeName: String?) {
        var w = 400
        var h = 400
        var center = "126.978082,37.565577"
        var place: String? = null
        var marker: String? = null
        if (x != null && y != null) {
            center = "$x $y"
            place = "|label:$placeName"
            marker =
                "type:t${place}|size:mid|pos:$x $y|viewSizeRatio:2.0"
        }


        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(MapSearch::class.java).getStaticMap(
            "kqfai8b97u",
            "NyaUzYcb3IWf1GKPNFDTYJTHIg9SUNtciSstiv5m",
            w,
            h,
            14,
            "basic",
            marker,
            center,
            2
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val input: InputStream = response.body()!!.byteStream()
//                val bufferedInputStream = BufferedInputStream(input)
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
                binding.CreateRoomActivityMapView.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StaticMap", "??????")
            }
        })


    }

    /* ???????????? ?????? ?????? 2021-05-19 ????????? ?????????????????? ???????????? ?????? ?????????
     private fun ageArrayList(): ArrayList<Int> {
     val list = ArrayList<Int>()

     for (i in 18..100) {
     list.add(i)

     }
     return list
     }*/

    /* ???????????? ???????????? 2021-05-19 ????????? ?????????????????? ???????????? ?????? ?????????
    private fun numOfPeopleArrayList(): ArrayList<Int> {
    val list = ArrayList<Int>()

    for (i in 0..3) {
    list.add(i)

    }
    return list
    }
     */
    @TargetApi(30)
    private fun Context.checkBackgroundLocationPermissionAPI30(backgroundLocationRequestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        } else {
            AlertDialog.Builder(this)
                .setTitle("?????? ????????????")
                .setMessage("????????????????????? ?????????????????? ??????????????????.")
                .setPositiveButton("??????") { _, _ ->
//                     this request will take user to Application's Setting page
//                    requestPermissions(
//                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                        backgroundLocationRequestCode
//                    )
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)

                }
                .setNegativeButton("??????") { dialog, _ ->
                    dialog.dismiss()
                    onBackPressed()
                }
                .setCancelable(false)
                .create()
                .show()
        }


    }

    private fun Context.checkSinglePermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // ????????? ?????? ??????
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // ?????? ?????? (?????? ??? ??? ?????????)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("?????? ????????? ?????????????????? ?????? ????????? ??????????????????.")
                builder.setPositiveButton("??????") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION
                    )
                }
                builder.setNegativeButton("??????") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // ?????? ?????? ??????
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION
                    )
                } else {
                    // ?????? ?????? ?????? ?????? (??? ?????? ???????????? ??????)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("?????? ????????? ?????????????????? ???????????? ?????? ????????? ??????????????????.")
                    builder.setPositiveButton("???????????? ??????") { dialog, which ->
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }
                    builder.setNegativeButton("??????") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // ????????? ?????? ??????

        }
    }

    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}


