package com.example.abled_food_connect

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.abled_food_connect.databinding.ActivityUserRegisterBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import okhttp3.MediaType
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

class UserRegisterActivity : AppCompatActivity() {


    //사진첩에서 이미지 가져올때 사용하는 변수
    lateinit var imageView: ImageView
    lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null


    //카메라로 사진찍고 이미지 가져올때 사용하는 변수
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath : String



    //유저정보 테이블에 저장할 값
    lateinit var user_id : String
    lateinit var social_login_type  : String
    lateinit var nick_name  : String
    lateinit var profile_image_path  : String
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

        imageView = binding.userProfileIv




        //title = "KotlinApp"



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

        binding.userRegisterBtn.setOnClickListener {
            RegistUserInfo(profile_image_path)


        }





//        //카메라 기능
        settingPermission() // 권한체크 시작
//
//        binding.userProfileIv.setOnClickListener {
//            startCapture()
//        }


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


        var requestBody : RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody)

        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl("http://3.37.36.188/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        //creating our api

        var server = retrofit.create(retrofit_interface::class.java)

        // 파일, 사용자 아이디, 파일이름

        nick_name = binding.nicNameEt.text.toString()
        birth_year = binding.birthYearEt.text.toString()
        user_gender = binding.userGenderEt.text.toString()
        phone_number = binding.phoneNumberEt.text.toString()

        server.post_Porfile_Request(user_id,social_login_type, nick_name,birth_year,user_gender,phone_number, body).enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Toast.makeText(getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 결과2",""+response?.body().toString())
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
                Toast.makeText(this@UserRegisterActivity, "권한 허가", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@UserRegisterActivity, "권한 거부", Toast.LENGTH_SHORT)
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
                        "com.example.abled_food_connect.fileprovider",
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


}