package com.example.abled_food_connect

import android.Manifest
import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.lujun.androidtagview.TagView
import com.example.abled_food_connect.array.age
import com.example.abled_food_connect.array.numOfPeople
import com.example.abled_food_connect.databinding.ActivityCreateRoomActivityBinding
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.daum.android.map.*
import net.daum.mf.map.api.MapView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateRoomActivity : AppCompatActivity() {
    val binding by lazy { ActivityCreateRoomActivityBinding.inflate(layoutInflater) }
    var genderMaleSelected: Boolean = false
    var genderFemaleSelected: Boolean = false
    var genderAnySelected: Boolean = false
    private lateinit var map: MapView
    private lateinit var mapview: ViewGroup
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    /*태그 리스트*/val PERMISSIONS_REQUEST_CODE = 100
    var tagArray: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        /*카카오 맵 API*/
//        map = MapView(this)
//        mapview = binding.mapView
//        mapview.addView(map)
        /*바인딩 뷰 변수화*/
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput
        val maximum = binding.maximumAgeTextView
        val minimum = binding.minimumAgeTextView

        /*드롭다운 어댑터 설정*/
        maximum.setAdapter(setAdapter(age()))
        minimum.setAdapter(setAdapter(age()))
        numOfPeople.setAdapter(setAdapter(numOfPeople()))

        onClickListenerGroup()
        checkPermissions()



    }




    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /*


    메소드 구역



    */

    /**
     * 퍼미션 리절트
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
                        "권한 설정이 거부되었습니다.\n앱을 사용하시려면 다시 실행해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n설정에서 권한을 허용해야 합니다..", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /**
     * 위치권한 퍼미션 체크
     * */
    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), PERMISSIONS_REQUEST_CODE)
        }
    }

    /**
     * 나이 어댑터
     * */
    private fun setAdapter(age: age): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.simple_list_item_1, age.numArray())

    }

    /**
     * 모집인원 어댑터
     * */
    private fun setAdapter(numOfPeople: numOfPeople): ArrayAdapter<Int> {


        return ArrayAdapter(this, R.layout.simple_list_item_1, numOfPeople.numArray())

    }

    /**
     *입력 항목 체크 메서드
     */
    private fun inputCheck(): Boolean {

        if (binding.CreateRoomActivityRoomTitleInput.length() == 0) {
            Toast.makeText(this, "방 제목을 입력해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomShopNameInputLayout.bottom)
            binding.CreateRoomTitleInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityRoomShopNameInput.length() == 0) {
            Toast.makeText(this, "매장 이름을 입력해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, 0)
            binding.CreateRoomShopNameInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityRoomInformationInput.length() == 0) {
            Toast.makeText(this, "방 소개를 입력해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomTitleInputLayout.bottom)
            binding.CreateRoomInformationInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityNumOfPeopleInput.length() == 0) {
            Toast.makeText(this, "모집 인원수를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomInformationInputLayout.bottom)
            binding.CreateRoomActivityNumOfPeopleInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityDateInput.length() == 0) {
            Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityDateInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityTimeInput.length() == 0) {
            Toast.makeText(this, "시간을 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityTimeInput.requestFocus()
            return false
        } else if (!timeCompare("${binding.CreateRoomActivityDateInput.text.toString()} ${binding.CreateRoomActivityTimeInput.text.toString()}")) {
            Toast.makeText(this, "입력한 시간이 이미 지나간 시간입니다", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)

            return false
        } else if (!genderMaleSelected && !genderFemaleSelected && !genderAnySelected) {
            Toast.makeText(this, "모집 성별 선택해주세요", Toast.LENGTH_LONG).show()
            return false
        } else if (binding.minimumAgeTextView.length() == 0) {
            Toast.makeText(this, "최소나이를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.minimumAgeTextView.requestFocus()
            return false

        } else if (binding.maximumAgeTextView.length() == 0) {
            Toast.makeText(this, "최대나이를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.maximumAgeTextView.requestFocus()
            return false

        } else if (binding.maximumAgeTextView.text.toString()
                .toInt() < binding.minimumAgeTextView.text.toString().toInt()
        ) {
            Toast.makeText(this, "최대나이가 최소나이 보다 작을 수 없습니다.", Toast.LENGTH_LONG).show()
            binding.maximumAgeTextView.requestFocus()
            return false
        } else {
            return true
        }
    }


    /**
     * 모집 성별 선택 메소드
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
     * 입력된 방정보 서버에 리퀘스트
     */
    private fun createRoom() {
        val tile = binding.CreateRoomActivityRoomTitleInput.text.toString()
        val info = binding.CreateRoomActivityRoomInformationInput.text.toString()
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput.text.toString()
        val date = binding.CreateRoomActivityDateInput.text.toString()
        val time = binding.CreateRoomActivityTimeInput.text.toString()
        val adress = "주소부분"
        val shopName = binding.CreateRoomActivityRoomShopNameInput.text.toString()
        val keyWord = tagArray.toString()
        var gender: String = ""

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
                .baseUrl("http://3.37.36.188/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.createRoom(
            tile,
            info,
            numOfPeople,
            date,
            time,
            adress,
            shopName,
            keyWord,
            gender,
            minAge,
            maxAge,
            hostName
        )
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response?.body().toString() == "true") {
                        Toast.makeText(this@CreateRoomActivity, "방 생성", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CreateRoomActivity,MainFragmentActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                        finish()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

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

    /**
     * 오늘 날짜 까지 제한하여 데이트피커 띄우기
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
     * 타임피커 띄우기
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
     * 현재 시스템시간과 설정한 약속시간 비교
     */
    private fun timeCompare(time: String): Boolean {
        var simpleTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
        var now: Long = System.currentTimeMillis()
        var nowTime = simpleTime.format(Date(now))
        var transNowTime: Date = simpleTime.parse(nowTime)
        var settingTime: Date = simpleTime.parse(time)
        Log.e("날짜 비교", "${transNowTime}/${settingTime}")
        Log.e("결과", settingTime.after(transNowTime).toString())
        return settingTime.after(transNowTime)

    }

    /**
     * 데이트/타임 피커에서 선택한 숫자가 10보다 낮을 경우
     * 숫자 앞에 0을 븉여 두자릿수로 표현하는 메소드
     */
    private fun plusZero(int: Int): String {
        return if (int < 10) {
            "0$int"
        } else {
            int.toString()
        }
    }

    /**
     * 온클릭 메서드들 그룹
     */
    private fun onClickListenerGroup() {
        /*방만들기 버튼클릭*/
        binding.CreateRoomButton.setOnClickListener {
            if (inputCheck()) {
                createRoom()
            }
        }
        /*툴바 타이틀 세팅*/
        binding.CreateRoomActivityToolbar.title = "방만들기"

        /*성별 선택 남자 온클릭 리스너*/
        binding.CreateRoomActivityMaleImageView.setOnClickListener {
            genderMaleSelected = limitGender("male")
        }
        /*성별 선택 여자 온클릭 리스너*/
        binding.CreateRoomActivityFemaleImageView.setOnClickListener {
            genderFemaleSelected = limitGender("female")
        }
        /*성별 선택 상관없음 온클릭 리스너*/
        binding.CreateRoomActivityGenderAnyImageView.setOnClickListener {
            genderAnySelected = limitGender("any")
        }


        //태그 리스트 사이즈 0 이면 텍스트뷰 보이기
        if (tagArray.size == 0) {
            binding.emptyKeyWordTextView.visibility = View.VISIBLE
        }
        /*태그 입력창 온클릭 리스너*/
        binding.CreateRoomActivityKeyWordInput.setOnClickListener {

        }
        /*날짜 텍스트박스에 포커스생겼을때*/
        binding.CreateRoomActivityDateInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                dateCalendarDialog()
            } else {

            }
        }
        /*날짜 텍스트박스 온클릭리스너*/
        binding.CreateRoomActivityDateInput.setOnClickListener {
            dateCalendarDialog()
        }
        /*시간 텍스트박스에 포커스생겼을때*/
        binding.CreateRoomActivityTimeInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                timeCalendarDialog()

            } else {

            }
        }
        /*시간 텍스트박스 온클릭리스너*/
        binding.CreateRoomActivityTimeInput.setOnClickListener {
            timeCalendarDialog()
        }
//        binding.time.setOnClickListener {
//            timeCalendarDialog()
//        }
//        binding.date.setOnClickListener {
//            dateCalendarDialog()
//        }
        /*태그 등록 버튼 온클릭리스너*/
        binding.tagAddButton.setOnClickListener {
            if (binding.CreateRoomActivityKeyWordInput.length() != 0) {
                /*태그 레이아웃 추가*/
                binding.tagLayout.addTag(binding.CreateRoomActivityKeyWordInput.text.toString())
                /*태그 리스트에 키워드 추가*/
                tagArray.add(binding.CreateRoomActivityKeyWordInput.text.toString())
                /*태그 리스트 0초과 일 때 텍스트뷰 없애기*/
                if (tagArray.size != 0) {
                    binding.emptyKeyWordTextView.visibility = View.GONE
                }
                /*등록 완료 후 텍스트 입력창 초기화*/
                binding.CreateRoomActivityKeyWordInput.setText("")
                /*스크롤 내리기*/
                binding.Scroll.smoothScrollTo(0, binding.CreateRoomButton.bottom)
//                Toast.makeText(this, tagArray.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        /*태그 온클릭 리스너*/
        binding.tagLayout.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {

            }

            override fun onTagLongClick(position: Int, text: String?) {
                /*태그 롱클릭시 제거*/
                binding.tagLayout.removeTag(position)
                /*태그 리스트에서 해당 포지션 제거*/
                tagArray.removeAt(position)
//                Toast.makeText(this@CreateRoomActivity, tagArray.toString(),Toast.LENGTH_SHORT).show()
                /*태그 리스트가 0일 때 텍스트뷰 보이기*/
                if (tagArray.size == 0) {
                    binding.emptyKeyWordTextView.visibility = View.VISIBLE
                }


            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
                TODO("Not yet implemented")
            }

            override fun onTagCrossClick(position: Int) {
                TODO("Not yet implemented")
            }
        })

        binding.CreateRoomMapSearchButton.setOnClickListener {
            startActivity(Intent(this, CreateRoomMapSearchActivity::class.java))

        }
    }


    /* 나이제한 배열 생성 2021-05-19 기능을 인터페이스로 전환하여 사용 불필요
     private fun ageArrayList(): ArrayList<Int> {
     val list = ArrayList<Int>()

     for (i in 18..100) {
     list.add(i)

     }
     return list
     }*/

    /* 모집인원 배열생성 2021-05-19 기능을 인터페이스로 전환하여 사용 불필요
    private fun numOfPeopleArrayList(): ArrayList<Int> {
    val list = ArrayList<Int>()

    for (i in 0..3) {
    list.add(i)

    }
    return list
    }
     */

}


