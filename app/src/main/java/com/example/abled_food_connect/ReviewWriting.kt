package com.example.abled_food_connect

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.databinding.ActivityReviewWritingBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class ReviewWriting : AppCompatActivity() {



    /*
       코틀린 뷰 바인딩을 적용시켰습니다.
     */
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityReviewWritingBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!


    var tasteStarPoint : Int = 0
    var serviceStarPoint : Int = 0
    var cleanStarPoint : Int = 0
    var interiorStarPoint : Int = 0


    //갤러리에서 이미지 불러오기
    private  val OPEN_GALLERY = 1000

    var imageview_control_num = 0

    //서버로 보낼 이미지 어래이
    var itemphoto = ArrayList<MultipartBody.Part>()


    lateinit var myPath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 기존 setContentView 를 제거해주시고..
        //setContentView(R.layout.activity_review_writing)

        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityReviewWritingBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.

        setContentView(binding.root)

        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.
        // 뷰 id도 파스칼케이스 + 카멜케이스의 네이밍규칙 적용으로 인해서 nicName_Et -> nicNameEt 로 자동 변환 되었습니다.




        binding.imageViewOne.setOnClickListener(View.OnClickListener {
            imageview_control_num =  1
            openGallery()
        })

        binding.imageViewTwo.setOnClickListener(View.OnClickListener {
            imageview_control_num =  2
            openGallery()
        })

        binding.imageViewThree.setOnClickListener(View.OnClickListener {
            imageview_control_num =  3
            openGallery()
        })



        binding.ratingStarTaste.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            tasteStarPoint = rating.toInt()
            Log.d("레이팅스타 맛", "${tasteStarPoint}점")
        }

        binding.ratingStarService.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            serviceStarPoint = rating.toInt()
            Log.d("레이팅스타 서비스", "${serviceStarPoint}점")
        }

        binding.ratingStarClean.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            cleanStarPoint= rating.toInt()
            Log.d("레이팅스타 위생", "${cleanStarPoint}점")
        }

        binding.ratingStarInterior.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            interiorStarPoint= rating.toInt()
            Log.d("레이팅스타 인테리어", "${interiorStarPoint}점")
        }


        binding.writingFinishBtn.setOnClickListener(View.OnClickListener {
            UserReviewWriting("t");
        })






    }

    //이미지 갤러리 열기
    private  fun openGallery(){
        val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                var currentImageUri : Uri? = data?.data

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageUri)

                    if(imageview_control_num == 1 ){
                        binding.imageViewOne.setImageBitmap(bitmap)

                        val uriPathHelper = UserRegisterActivity.URIPathHelper()
                        myPath = currentImageUri?.let { uriPathHelper.getPath(this, it) }.toString()

                        val file = File(myPath)


                        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file);
                        var body : MultipartBody.Part = MultipartBody.Part.createFormData("itemphoto1","photo1",requestBody)
                        itemphoto.add(body)

                        Log.d("패스", itemphoto.toString())


//                        val uriPathHelper = UserRegisterActivity.URIPathHelper()
//                        myPath = currentImageUri?.let { uriPathHelper.getPath(this, it) }.toString()
//
//                        Log.d("패스", myPath)
//                        val file = File(myPath)
//                        var surveyBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//                        itemphoto.add(MultipartBody.Part.createFormData("itemphoto", file.name, surveyBody))



                    } else if(imageview_control_num == 2 ){
                        binding.imageViewTwo.setImageBitmap(bitmap)
//                        val file = File()
//                        var surveyBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//                        itemphoto.add(MultipartBody.Part.createFormData("itemphoto", file.name, surveyBody))

                        val uriPathHelper = UserRegisterActivity.URIPathHelper()
                        myPath = currentImageUri?.let { uriPathHelper.getPath(this, it) }.toString()

                        val file = File(myPath)
                        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file);
                        var body : MultipartBody.Part = MultipartBody.Part.createFormData("itemphoto2","photo2",requestBody)
                        itemphoto.add(body)
                        Log.d("패스", itemphoto.toString())


                    } else if(imageview_control_num == 3 ){
                        binding.imageViewThree.setImageBitmap(bitmap)

//                        val file = File()
//                        var surveyBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//                        itemphoto.add(MultipartBody.Part.createFormData("itemphoto", file.name, surveyBody))

                        val uriPathHelper = UserRegisterActivity.URIPathHelper()
                        myPath = currentImageUri?.let { uriPathHelper.getPath(this, it) }.toString()

                        val file = File(myPath)
                        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file);
                        var body : MultipartBody.Part = MultipartBody.Part.createFormData("itemphoto3","photo3",requestBody)
                        itemphoto.add(body)



//                        var surveyBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//                        itemphoto.add(MultipartBody.Part.createFormData("itemphoto", file.name, surveyBody))
                        Log.d("패스", itemphoto.toString())
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

//    private fun prepareFilePart(partName: String, fileUri: Uri): Part? {
//        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
//        // use the FileUtils to get the actual file by uri
//        val file: File = FileUtils.getFile(this, fileUri)
//
//        // create RequestBody instance from file
//        val requestFile: RequestBody = create(MediaType.parse(contentResolver.getType(fileUri)), file)
//
//        // MultipartBody.Part is used to send also the actual file name
//        return createFormData.createFormData(partName, file.name, requestFile)
//    }


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





    fun UserReviewWriting(path:String){

//            //creating a file
//            val file = File(myPath)
//            var fileName = myPath.replace("@","").replace(".","")
//            fileName = fileName+".png"
//
//
//            var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
//            var body : MultipartBody.Part = MultipartBody.Part.createFormData("itemphoto",fileName,requestBody)

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

        var server = retrofit.create(API.reviewWriting::class.java)

        // 파일, 사용자 아이디, 파일이름

//            nick_name = binding.nicNameEt.text.toString()
//            //birth_year 데이터 피커에서 값을 받는다.
//            //user_gender = binding.userGenderEt.text.toString()
//            phone_number = binding.phoneNumberInputEt.text.toString()

        server.review_Writing_Request(itemphoto,1,"t","tt","d","d","2021-05-19 14:57:42","2021-05-19","14:57:42","d",tasteStarPoint,serviceStarPoint,cleanStarPoint,interiorStarPoint,"d","d","d").enqueue(object:
            Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { Log.d("레트로핏 결과1", it) }
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful) {
                    Toast.makeText(getApplicationContext(), "리뷰를 작성했습니다.", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 결과2",""+response?.body().toString())


                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                    Log.d("레트로핏 실패결과",""+response?.body().toString())
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainFragmentActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("review","review"))
    }
}