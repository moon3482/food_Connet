package com.example.abled_food_connect

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.abled_food_connect.interfaces.retrofit_interface
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.databinding.ActivityUserRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UserRegisterActivity : AppCompatActivity() {


    //사진첩에서 이미지 가져올때 사용하는 변수
    lateinit var imageView: ImageView
    lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null


    //카메라로 사진찍고 이미지 가져올때 사용하는 변수
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath : String


    //닉네임 중복 체크했는지 확인하는 변수
    var nicName_dup_check_str = "NO"


    //유저정보 테이블에 저장할 값
    lateinit var user_id : String
    lateinit var social_login_type  : String
    lateinit var nick_name  : String
    var profile_image_path  : String ="NOIMAGE"
    lateinit var birth_year  : String
    lateinit var user_gender  : String
    lateinit var phone_number  : String


    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityUserRegisterBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!





    //파이어베이스 전화번호 인증

    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var phone_auth_isFisished : String ="NO"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // 기존 setContentView 를 제거해주시고..
        //setContentView(R.layout.activity_user_register)

         // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityUserRegisterBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        //
        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 nicName_Et -> nicNameEt 로 자동 변환 되었습니다.


        user_id = intent.getStringExtra("user_id").toString()
        social_login_type = intent.getStringExtra("social_login_type").toString()

        Log.d("로그인에서 받아온 값 : 유저 아이디", user_id)
        Log.d("로그인에서 받아온 값 : 소셜 타입", social_login_type)

        imageView = binding.userProfileIv





        //title = "KotlinApp"

        // 카메라 및 이미지 권한체크
        settingPermission()

        //이미지뷰를 누르면 사진을 변경할 수 있다.
        binding.userProfileIv.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("사진 업로드")
            builder.setIcon(R.drawable.ic_baseline_camera_alt_24)

            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->
                            startCapture()

                        DialogInterface.BUTTON_NEGATIVE ->
                            startActivityForResult(gallery, pickImage)
                    }
                }
            }

            builder.setPositiveButton("카메라", listener)
            builder.setNegativeButton("갤러리", listener)


            builder.show()
        }



        //닉네임 글자수 10자 제한
        binding.nicNameEt.setMaxLength(10)


        binding.nicNameCheckBtn.setOnClickListener(View.OnClickListener {

            binding.nicNameEt.setText(binding.nicNameEt.text.toString().replace(" ",""))
            if(binding.nicNameEt.text.toString().length>0){
                    nicName_duplicate_check(binding.nicNameEt.text.toString())
                    Log.d("이름", binding.nicNameEt.text.toString())
            }else{
                Toast.makeText(applicationContext, "닉네임을 입력해주세요.", Toast.LENGTH_LONG).show()
            }

        })


        binding.nicNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nicName_dup_check_str = "NO"
            }
        })





        //출생연도 선택버튼

        //기본값은 ""
        //값이 없는 경우, 가입 시 선택해주세요라는 토스트메시지가 생긴다..
        birth_year = ""

        binding.birthYearBtn.setOnClickListener {

            val dialog = AlertDialog.Builder(this).create()
            val edialog : LayoutInflater = LayoutInflater.from(this)
            val mView : View = edialog.inflate(R.layout.dialog_datepicker,null)

            val year : NumberPicker = mView.findViewById(R.id.yearpicker_datepicker)

            val cancel : Button = mView.findViewById(R.id.cancel_button_datepicker)
            val save : Button = mView.findViewById(R.id.save_button_datepicker)


            //  순환 안되게 막기
            year.wrapSelectorWheel = false


            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


            //  최소값 설정
            year.minValue = 1900
            //  최대값 설정
            year.maxValue = 2020

            year.value = 1990



            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }

            //  완료 버튼 클릭 시
            save.setOnClickListener {

                birth_year =(year.value).toString()
                binding.birthYearBtn.setText(birth_year+"년")
                Log.d("나와", (year.value).toString() + "년")
                // month_textview_statsfrag.text = (month.value).toString() + "월"

                dialog.dismiss()
                dialog.cancel()
            }
            dialog.setView(mView)
            dialog.create()
            dialog.show()

        }

        // 라디오 버튼 상태를 바꿨을때, 자동으로 나타나는 상태 표시(람다식)
        binding.genderSelectRg.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.genderManRb ->
                    user_gender = "MAN"
                R.id.genderWomanRb ->
                    user_gender = "WOMAN"

            }
            Log.d("무슨성별선택?", user_gender)
        }


        /*
        파이어베이스 인증
         */

        auth=FirebaseAuth.getInstance()






        binding.firebaseCodeSendBtn.setOnClickListener{
            login()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //Toast.makeText(applicationContext, "야호.", Toast.LENGTH_LONG).show()
//                startActivity(Intent(applicationContext, Home::class.java))
//                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "[나는 로봇이 아닙니다] 인증을 해주세요.", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                if(verificationId!=null) {
                    storedVerificationId = verificationId
                    resendToken = token
                    Toast.makeText(applicationContext, "코드를 발송했습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        }


        binding.firebaseCodeCheckBtn.setOnClickListener{
            var otp=binding.firebaseCodeInputEt.text.toString().trim()
            if(!otp.isEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"다시 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
        }





        binding.userRegisterBtn.setOnClickListener {


            if(profile_image_path == "NOIMAGE"){
                Toast.makeText(this,"프로필 사진을 등록해주세요.",Toast.LENGTH_SHORT).show()
            }
            else if(nicName_dup_check_str=="NO"){
                Toast.makeText(this,"닉네임 중복확인을 해주세요.",Toast.LENGTH_SHORT).show()
            }
            else if(birth_year.equals("")){
                Toast.makeText(this,"출생연도를 선택해주세요.",Toast.LENGTH_SHORT).show()
            }
            else if(!binding.genderManRb.isChecked()&&!binding.genderWomanRb.isChecked()){
                Toast.makeText(this,"성별을 선택해주세요.",Toast.LENGTH_SHORT).show()
            }
            else if(phone_auth_isFisished.equals("NO")){
                Toast.makeText(this,"전화번호 인증을 완료해주세요.",Toast.LENGTH_SHORT).show()
            }


            else {

               RegistUserInfo(profile_image_path)
            }


        }



    }

    fun EditText.setMaxLength(maxLength: Int){
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    // onDestroy
    override fun onDestroy() {
        super.onDestroy()
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mBinding = null
    }




    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //사진첩에서 사진을 가져올 때.
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)

            val uriPathHelper = URIPathHelper()
            var filePath = imageUri?.let { uriPathHelper.getPath(this, it) }
            if (filePath != null) {
                profile_image_path = filePath
            }

        }

        //카메라로 촬영했을 때.
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val file = File(currentPhotoPath)

            profile_image_path = currentPhotoPath

            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media
                    .getBitmap(contentResolver, Uri.fromFile(file))
                imageView.setImageBitmap(bitmap)



            }
            else{
                val decode = ImageDecoder.createSource(this.contentResolver,
                    Uri.fromFile(file))
                val bitmap = ImageDecoder.decodeBitmap(decode)
                imageView.setImageBitmap(bitmap)


            }
        }
    }







    fun RegistUserInfo(path:String){

        //creating a file
        val file = File(path)
        var fileName = user_id.replace("@","").replace(".","")
        fileName = fileName+".png"


        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody)

        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        //creating our api

        var server = retrofit.create(retrofit_interface::class.java)

        // 파일, 사용자 아이디, 파일이름

        nick_name = binding.nicNameEt.text.toString()
        //birth_year 데이터 피커에서 값을 받는다.
        //user_gender = binding.userGenderEt.text.toString()
        phone_number = binding.phoneNumberInputEt.text.toString()

        server.post_Porfile_Request(user_id,social_login_type, nick_name,birth_year,user_gender,phone_number, body).enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 결과2",""+response?.body().toString())
                    var nextIntent :Intent = Intent(this@UserRegisterActivity, MainActivity::class.java)
                    startActivity(nextIntent)
                    finish()

                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 실패결과",""+response?.body().toString())
                }
            }
        })
    }


    class URIPathHelper {

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun getPath(context: Context, uri: Uri): String? {
            val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = uri?.let { context.getContentResolver().query(it, projection, selection, selectionArgs,null) }
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                if (cursor != null) cursor.close()
            }
            return null
        }

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }











    fun settingPermission(){
        var permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
//                Toast.makeText(this@UserRegisterActivity, "권한 허가", Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@UserRegisterActivity, "앱 필수권한을 허용해주세요.", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@UserRegisterActivity) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permis)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }

    fun startCapture(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch(ex: IOException){
                    null
                }
                photoFile?.also{
                    val photoURI : Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.abled_food_connect.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply{
            currentPhotoPath = absolutePath
        }
    }














    /*
    파이어베이스 인증에 사용되는 메서드
     */



    private fun login() {
        val mobileNumber=binding.phoneNumberInputEt
        var number=mobileNumber.text.toString().trim()

        if(!number.isEmpty()){
            number="+82"+number
            sendVerificationcode (number)
        }else{
            Toast.makeText(this,"정확한 전화번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"인증되었습니다.",Toast.LENGTH_SHORT).show()

                    binding.firebaseCodeCheckBtn.setEnabled(false)
                    binding.firebaseCodeCheckBtn.setBackgroundColor(Color.GRAY)

                    binding.firebaseCodeSendBtn.setEnabled(false)
                    binding.firebaseCodeSendBtn.setBackgroundColor(Color.GRAY)
                    binding.phoneNumberInputEt.setEnabled(false)
                    binding.firebaseCodeInputEt.setEnabled(false)

                    phone_auth_isFisished = "YES"

// ...
                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(this,"인증코드를 다시 입력해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }




    //닉네임 중복 확인

    fun nicName_duplicate_check(nick_name:String){

        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()

        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        //creating our api

        var server = retrofit.create(API.nicNameCheck::class.java)

        // 파일, 사용자 아이디, 파일이름
        server.checkNicName(nick_name).enqueue(object:
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    if(response?.body().toString()=="true") {
                        Toast.makeText(getApplicationContext(), "이미 사용중인 닉네임입니다.", Toast.LENGTH_LONG)
                            .show();

                    }

                    if(response?.body().toString()=="false") {
                        Toast.makeText(getApplicationContext(), "사용할 수 있는 닉네임입니다.", Toast.LENGTH_LONG)
                            .show();
                        nicName_dup_check_str = "YES"

                        binding.nicNameCheckBtn.setEnabled(false)
                        binding.nicNameCheckBtn.setBackgroundColor(Color.GRAY)
                        binding.nicNameEt.setEnabled(false)



                    }

                    Log.d("레트로핏 성공결과",""+response?.body().toString())

                } else {
                    Toast.makeText(getApplicationContext(), "서버연결 실패.", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 실패결과",""+response?.body().toString())
                    Log.d("레트로핏 실패결과",""+call.request())

                }
            }
        })
    }





}