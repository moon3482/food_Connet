package com.example.abled_food_connect

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import co.lujun.androidtagview.TagView
import com.example.abled_food_connect.array.age
import com.example.abled_food_connect.array.numOfPeople
import com.example.abled_food_connect.databinding.ActivityCreateRoomActivityBinding
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

class CreateRoomActivity : AppCompatActivity() {
    val binding by lazy { ActivityCreateRoomActivityBinding.inflate(layoutInflater) }
    var genderMaleSelected: Boolean = false
    var genderFemaleSelected: Boolean = false
    var genderAnySelected: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        /*바인딩 뷰 변수화*/
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput
        val maximum = binding.maximumAgeTextView
        val minimum = binding.minimumAgeTextView

        /*드롭다운 어댑터 설정*/
        maximum.setAdapter(setAdapter(age()))
        minimum.setAdapter(setAdapter(age()))
        numOfPeople.setAdapter(setAdapter(numOfPeople()))


        /*방만들기 버튼클릭*/
        binding.CreateRoomButton.setOnClickListener {
            if (inputCheck()) {
                createRoom()
            }
        }
        binding.CreateRoomActivityMaleImageView.setOnClickListener {
            genderMaleSelected = limitGender("male")
        }
        binding.CreateRoomActivityFemaleImageView.setOnClickListener {
            genderFemaleSelected = limitGender("female")
        }
        binding.CreateRoomActivityGenderAnyImageView.setOnClickListener {
            genderAnySelected = limitGender("any")
        }
        var array: ArrayList<String> = ArrayList()
        binding.tagAddButton.setOnClickListener{
            if(binding.CreateRoomActivityKeyWordInput.length()!=0){

                binding.tagLayout.addTag(binding.CreateRoomActivityKeyWordInput.text.toString())
                array.add(binding.CreateRoomActivityKeyWordInput.text.toString())
                binding.CreateRoomActivityKeyWordInput.setText("")
            }
        }


        binding.tagLayout.setOnTagClickListener(object :TagView.OnTagClickListener{
            override fun onTagClick(position: Int, text: String?) {

            }

            override fun onTagLongClick(position: Int, text: String?) {
                binding.tagLayout.removeTag(position)
                array.removeAt(position)
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
                TODO("Not yet implemented")
            }

            override fun onTagCrossClick(position: Int) {
                TODO("Not yet implemented")
            }
        })

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
            binding.Scroll.scrollTo(0, 0)
            binding.CreateRoomTitleInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityRoomInfoInput.length() == 0) {
            Toast.makeText(this, "방 소개를 입력해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomTitleInputLayout.bottom)
            binding.CreateRoomInfoInputLayout.requestFocus()
            return false
        } else if (binding.CreateRoomActivityNumOfPeopleInput.length() == 0) {
            Toast.makeText(this, "모집 인원수를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomInfoInputLayout.bottom)
            binding.CreateRoomActivityNumOfPeopleInput.requestFocus()
            return false
        } else if (binding.CreateRoomActivityDateInput.length() == 0) {
            Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_LONG).show()
            binding.Scroll.scrollTo(0, binding.CreateRoomActivityNumOfPeopleInput.bottom)
            binding.CreateRoomActivityDateInput.requestFocus()
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

    override fun onStop() {
        super.onStop()
        finish()
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

    private fun createRoom() {
        val tile = binding.CreateRoomActivityRoomTitleInput.text.toString()
        val info = binding.CreateRoomActivityRoomInfoInput.text.toString()
        val numOfPeople = binding.CreateRoomActivityNumOfPeopleInput.text.toString()
        val date = "2021-05-20"
        val time = "18:00:00"
        val adress = "주소부분"
        val shopName = "홍대돈부리"
        val keyWord = "돈부리집"
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
        val hostName = "호스트네임"

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
                        onBackPressed()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

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