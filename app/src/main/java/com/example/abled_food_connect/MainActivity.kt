package com.example.abled_food_connect

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.abled_food_connect.data.LoginDataClass
import com.example.abled_food_connect.interfaces.CheckingRegisteredUser
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {
    lateinit var callbackManager: CallbackManager
    var TAG = "KAKAO"
    var id = ""
    var firstName = ""
    var middleName = ""
    var lastName = ""
    var name = ""
    var picture = ""
    var email = ""
    var accessToken = ""


    /*
    ????????? ?????????
    ?????? ?????? 21.05.10
    ????????? ?????????
     */
    //OAuthLogin ??????
    lateinit var mOAuthLoginInstance: OAuthLogin

    //?????????????????? ????????????
    lateinit var mContext: Context

    companion object {
        var user_table_id: Int = 0
        var loginUserId: String = ""
        var loginUserNickname: String = ""
        var userThumbnailImage: String = ""
        var userGender:String = ""
        var userAge:Int = 0
        var ranking_explanation_check : Int = 0
    }


    var sharedLoginCheckBoolean: Boolean = false

    /*
    ?????? ?????????
    ?????? ?????? 21.05.10
    ????????? ?????????
     */
    // Firebase Authentication ?????? ?????????
    // firebase ????????? ?????? ??????
    var auth: FirebaseAuth? = null

    // ?????? ????????? ????????? ????????? ??????
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    lateinit var nextIntent: Intent


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnFacebookLogin: LinearLayout = findViewById(R.id.btnFacebookLogin)
        val btnKakaoLogin: LinearLayout = findViewById(R.id.btnKakaoLogin)







        nextIntent = Intent(this, UserRegisterActivity::class.java)


        val keyHash = Utility.getKeyHash(this)
        Log.e("??????", keyHash)


        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

            if (error != null) {

                Log.e(TAG, "????????? ??????", error)

            } else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "????????? ?????? ?????? ??????", error)
                    } else if (user != null) {
                        Log.i(
                            TAG, "????????? ?????? ?????? ??????" +
                                    "\n????????????: ${user.id}" +
                                    "\n?????????: ${user.kakaoAccount?.email}" +
                                    "\n?????????: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n??????????????? ??????: ${user.kakaoAccount?.profile?.profileImageUrl}" +
                                    "\n???????????????: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                        )

                        RegistUserInfo(user.id.toString(), "KAKAO")

//                        nextIntent.putExtra("user_id", user.id.toString())
//                        nextIntent.putExtra("social_login_type", "KAKAO")
//
//                        startActivity(nextIntent)
                    }
                }
                Log.i(TAG, "????????? ?????? ${token.accessToken}")
//                val mainFragmentJoin = Intent(this@MainActivity,MainFragmentActivity::class.java)
//                startActivity(mainFragmentJoin)
            }

        }


        btnKakaoLogin.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }

//            UserApiClient.instance.loginWithKakaoAccount(applicationContext, prompts = listOf(Prompt.LOGIN)) { token, error ->
//                if (error != null) {
//                    Log.e(MyPageFragment.TAG, "????????? ??????", error)
//                }
//                else if (token != null) {
//                    Log.i(MyPageFragment.TAG, "????????? ?????? ${token.accessToken}")
//
//
//
//                    UserApiClient.instance.me { user, error ->
//                        if (error != null) {
//                            Log.e(MyPageFragment.TAG, "????????? ?????? ?????? ??????", error)
//                        } else if (user != null) {
//                            Log.i(
//                                MyPageFragment.TAG, "????????? ?????? ?????? ??????" +
//                                        "\n????????????: ${user.id}" +
//                                        "\n?????????: ${user.kakaoAccount?.email}" +
//                                        "\n?????????: ${user.kakaoAccount?.profile?.nickname}" +
//                                        "\n??????????????? ??????: ${user.kakaoAccount?.profile?.profileImageUrl}" +
//                                        "\n???????????????: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
//                            )
//
//                            RegistUserInfo(user.id.toString(), "KAKAO")
//
//
//
//                        }
//                    }
//
//
//
//
//
//                }
//            }

//            UserApiClient.instance.logout { error ->
//                if (error != null) {
//                    Log.e(MyPageFragment.TAG, "???????????? ??????. SDK?????? ?????? ?????????", error)
//                }
//                else {
//                    Log.i(MyPageFragment.TAG, "???????????? ??????. SDK?????? ?????? ?????????")
//                }
//            }
//
//

        }
        callbackManager = CallbackManager.Factory.create()


        if (isLoggedIn()) {
            Log.d("LoggedIn? :", "YES")
            // Show the Activity with the logged in user
        } else {
            Log.d("LoggedIn? :", "NO")
            // Show the Home Activity
        }

//       Login Button made by Facebook
//
//
//        val loginButton = findViewById<LoginButton>(R.id.login_button)
//        loginButton.setReadPermissions(listOf("public_profile", "email"))
//        // If you are using in a fragment, call loginButton.setFragment(this)
//
//        // Callback registration
//        // If you are using in a fragment, call loginButton.setFragment(this)
//
//        // Callback registration
//        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
//            override fun onSuccess(loginResult: LoginResult?) {
//                // Get User's Info
//            }
//
//            override fun onCancel() {
//                // App code
//            }
//
//            override fun onError(exception: FacebookException) {
//                // App code
//            }
//        })

//      Custom Login Button


        btnFacebookLogin.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }

        // Callback registration
        LoginManager.getInstance()
            //??????????????? ??????????????? ?????? ?????????, ????????? ?????? ????????? ??????????????? ?????????????????? ??????.
            //???????????? ???????????? ????????????, ????????? ????????? ???????????? ????????? ???????????? ?????? ????????????????????? ????????? ????????? ???????????? ?????? ???????????? ????????? ?????????.
            //?????? ?????? ???????????? ???????????? ????????? ?????? ?????? ????????? ????????? ???????????? ????????? ??????????????????.
//            .setLoginBehavior(LoginBehavior.)
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d("TAG", "Success Login")
                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)


//                 val mainFragmentJoin = Intent(this@MainActivity,MainFragmentActivity::class.java)
//                    startActivity(mainFragmentJoin)
                }

                override fun onCancel() {
                    Toast.makeText(this@MainActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })


        /*
        ????????? ????????? ??????
         */
        //  ?????????????????? ?????? ???, ???????????? ??????????????? ID
        val naver_client_id = "zuHsHf3IHbg9uBONNNyv"
        // ?????????????????? ?????? ???, ???????????? ????????? ???
        val naver_client_secret = "nMQ60W3SjG"
        // ????????? ?????? ????????? ????????? ????????? ?????????????????? ??????.
        val naver_client_name = "food_connect"

        //?????????????????? ????????????
        mContext = this

        //????????? ???????????? ????????? ??????????????? ????????????.
        mOAuthLoginInstance = OAuthLogin.getInstance()

        //????????? ????????? ????????? ????????? ?????????
        mOAuthLoginInstance.enableWebViewLoginOnly()

        //?????? ????????? ?????? ?????????????????? ??????, ????????? ????????? ???????????? ????????????.
//        try {
//            if (packageManager.getApplicationInfo("com.nhn.android.search", 0).enabled) {
//                mOAuthLoginInstance?.enableNaverAppLoginOnly()
//            }
//        }
//        catch (e: Exception) {
//        }

        //????????? ???????????? ????????? ??????????????? ??????????????? ????????? ???????????????.
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)


        var btnNaverLogin: LinearLayout = findViewById(R.id.btnNaverLogin)
        //????????? ????????? ?????? ??????

        //??????????????? ???????????? ????????? ????????????.
        //????????? ????????? ???????????? ???????????? ??????.
        val buttonOAuthLoginImg: OAuthLoginButton = findViewById(R.id.buttonOAuthLoginImg)
        buttonOAuthLoginImg.setOAuthLoginHandler(mOAuthLoginHandler)



        btnNaverLogin.setOnClickListener {
            //????????? ????????? ???????????? mOAuthLoginHandler ??????


            buttonOAuthLoginImg.performClick()
        }


        /*
        ?????? ????????? ??????
         */

        // firebaseauth??? ???????????? ?????? ???????????? get
        auth = FirebaseAuth.getInstance()

        // xml?????? ?????? ????????? ?????? ?????? ????????????

        val btn_googleSignIn: SignInButton = findViewById(R.id.btn_googleSignIn)


        var btnGoogleLogin: LinearLayout = findViewById(R.id.btnGoogleLogin)


        btnGoogleLogin.setOnClickListener {
            //????????? ????????? ???????????? ??????????????? ??????
            googleSignInClient!!.signOut()
            googleLogin()
        }


        // ?????? ???????????? ?????? ??????????????? ?????? ?????? (Id, Email request)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)



        try {
            val info = packageManager.getPackageInfo(
                "com.example.abled_food_connect;",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }


    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT>=30) {
            if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                sharedLoadData()
            } else {
                checkBackgroundLocationPermissionAPI30()

            }
        }else{
            sharedLoadData()
        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
            "/$userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }
                accessToken = token.toString()

                // Facebook Id
                if (jsonObject!!.has("id")) {
                    val facebookId = jsonObject.getString("id")
                    Log.i("Facebook Id: ", facebookId.toString())
                    id = facebookId.toString()


                    RegistUserInfo(id, "FACEBOOK")

//                    nextIntent.putExtra("user_id", id)
//                    nextIntent.putExtra("social_login_type", "FACEBOOK")
//
//                    startActivity(nextIntent)


                } else {
                    Log.i("Facebook Id: ", "Not exists")
                    id = "Not exists"
                }


                // Facebook First Name
                if (jsonObject.has("first_name")) {
                    val facebookFirstName = jsonObject.getString("first_name")
                    Log.i("Facebook First Name: ", facebookFirstName)
                    firstName = facebookFirstName
                } else {
                    Log.i("Facebook First Name: ", "Not exists")
                    firstName = "Not exists"
                }


                // Facebook Middle Name
                if (jsonObject.has("middle_name")) {
                    val facebookMiddleName = jsonObject.getString("middle_name")
                    Log.i("Facebook Middle Name: ", facebookMiddleName)
                    middleName = facebookMiddleName
                } else {
                    Log.i("Facebook Middle Name: ", "Not exists")
                    middleName = "Not exists"
                }


                // Facebook Last Name
                if (jsonObject.has("last_name")) {
                    val facebookLastName = jsonObject.getString("last_name")
                    Log.i("Facebook Last Name: ", facebookLastName)
                    lastName = facebookLastName
                } else {
                    Log.i("Facebook Last Name: ", "Not exists")
                    lastName = "Not exists"
                }


                // Facebook Name
                if (jsonObject.has("name")) {
                    val facebookName = jsonObject.getString("name")
                    Log.i("Facebook Name: ", facebookName)
                    name = facebookName
                } else {
                    Log.i("Facebook Name: ", "Not exists")
                    name = "Not exists"
                }


                // Facebook Profile Pic URL
                if (jsonObject.has("picture")) {
                    val facebookPictureObject = jsonObject.getJSONObject("picture")
                    if (facebookPictureObject.has("data")) {
                        val facebookDataObject = facebookPictureObject.getJSONObject("data")
                        if (facebookDataObject.has("url")) {
                            val facebookProfilePicURL = facebookDataObject.getString("url")
                            Log.i("Facebook Profile Pic URL: ", facebookProfilePicURL)
                            picture = facebookProfilePicURL
                        }
                    }
                } else {
                    Log.i("Facebook Profile Pic URL: ", "Not exists")
                    picture = "Not exists"
                }

                // Facebook Email
                if (jsonObject.has("email")) {
                    val facebookEmail = jsonObject.getString("email")
                    Log.i("Facebook Email: ", facebookEmail)
                    email = facebookEmail
                } else {
                    Log.i("Facebook Email: ", "Not exists")
                    email = "Not exists"
                }

                val facebookId = jsonObject.getString("id")


            }).executeAsync()
    }

    fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        return isLoggedIn
    }


    fun logOutUser() {
        LoginManager.getInstance().logOut()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {

                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        } //if
    }


    /*
        ????????? ????????? ?????????
    */
    //????????? ?????? ?????? ???, ?????? ?????? ??????????????????
    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {


            // ???????????? ???????????? ??? ??????.
            if (success) {

                //????????? ?????? ??????????????????
//                Toast.makeText(
//                    baseContext, "????????? ??????????????????.", Toast.LENGTH_SHORT
//                ).show()


                // android.os.NetworkOnMainThreadException ?????? ??????
                // ?????? ??????????????? ???????????? ????????? ????????????, ?????? ??? ?????? ??????????????? ?????????.
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                // ????????? ?????? ????????? ??????
                var jObject = JSONObject(
                    mOAuthLoginInstance.requestApi(
                        mContext, mOAuthLoginInstance.getAccessToken(
                            baseContext
                        ), "https://openapi.naver.com/v1/nid/me"
                    ).toString()
                )


                //??????????????? ???????????? JSONObject
                val response = jObject.getString("response")

                jObject = JSONObject(response)


                //????????? ?????? ???????????????
                val id = jObject.getString("id")
                //????????? ?????? ?????????
                var profile_image = jObject.getString("profile_image")
                //profile_image ???????????? "\\"??? ???????????? ????????? ??? ????????? url??? ?????? ??? ??????.
                profile_image = profile_image.replace("\\", "")
                Log.d("??????ID", id)
                Log.d("??????????????????", profile_image)


                RegistUserInfo(id, "NAVER")

//                nextIntent.putExtra("user_id", id)
//                nextIntent.putExtra("social_login_type", "NAVER")
//
//                startActivity(nextIntent)


//                val accessToken: String = mOAuthLoginModule.getAccessToken(baseContext)
//                val refreshToken: String = mOAuthLoginModule.getRefreshToken(baseContext)
//                val expiresAt: Long = mOAuthLoginModule.getExpiresAt(baseContext)
//                val tokenType: String = mOAuthLoginModule.getTokenType(baseContext)
//                var intent = Intent(this, )

            } else {
                // ???????????? ???????????? ??? ??????.
                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)

                //???????????? ?????????????????? ??????
                Toast.makeText(
                    baseContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    /*
        ?????? ????????? ?????????
    */


    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    } // googleLogin


    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // ????????? ?????? ???
                    val user = Firebase.auth.currentUser
                    val name = user!!.displayName
                    val email = user!!.email
                    val photoUrl = user!!.photoUrl
                    val uid = user.uid

                    Log.d("?????????", "?????? ????????????????????????.")
                    Log.d("??????", name.toString())
                    Log.d("?????????", email.toString())
                    Log.d("???????????????", photoUrl.toString())
                    Log.d("????????? ????????? id", uid)

                    RegistUserInfo(user.uid, "GOOGLE")

//                    nextIntent.putExtra("user_id", user.uid)
//                    nextIntent.putExtra("social_login_type", "GOOGLE")
//                    startActivity(nextIntent)

                    //Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                    //startActivity(Intent (this, StudyRecommendActivity::class.java))
                } else {
                    // ????????? ?????? ???
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    } //firebaseAuthWithGoogle


    fun RegistUserInfo(userId: String, social_login_type: String) {


        //The gson builder
        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()


        //creating retrofit object
        var retrofit =
            Retrofit.Builder()
                .baseUrl("ServerIP")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(createOkHttpClient())
                .build()

        //creating our api

        var server = retrofit.create(CheckingRegisteredUser::class.java)

        // ??????, ????????? ?????????, ????????????
        server.post_checking_register_user(userId).enqueue(object :
            Callback<LoginDataClass> {
            override fun onFailure(call: Call<LoginDataClass>, t: Throwable) {
                t.message?.let { Log.d("???????????? ??????1", it) }
            }

            override fun onResponse(
                call: Call<LoginDataClass>,
                response: Response<LoginDataClass>
            ) {
                if (response.isSuccessful) {


                    val userlogin: LoginDataClass = response.body()!!




                    if (userlogin.success) {
                        val get_user_table_id = userlogin.id
                        val loginId = userlogin.userId
                        val loginNickname = userlogin.userNickname
                        val loginThumbnailImage = userlogin.userThumbnailImage

                        Toast.makeText(applicationContext, "????????? ???????????????.", Toast.LENGTH_LONG)
                            .show();
                        Log.d("??????", "" + "?????????????????????.")
                        val mainFragmentJoin =
                            Intent(this@MainActivity, MainFragmentActivity::class.java)
                        user_table_id = get_user_table_id
                        loginUserId = loginId
                        loginUserNickname = loginNickname
                        userThumbnailImage = loginThumbnailImage
                        userAge = userlogin.userAge
                        userGender = userlogin.userGender
                        ranking_explanation_check = userlogin.ranking_explanation_check


                        //??????????????????????????? ?????? ????????????.
                        sharedSaveData()


                        startActivity(mainFragmentJoin)
                        finish()


                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "??????????????? ????????????.",
                            Toast.LENGTH_LONG
                        ).show();


                        nextIntent.putExtra("user_id", userId)
                        nextIntent.putExtra("social_login_type", social_login_type)
                        startActivity(nextIntent)
                    }

                    Log.d("???????????? ????????????", "" + response?.body().toString())


                } else {
                    Toast.makeText(applicationContext, "???????????? ??????.", Toast.LENGTH_LONG).show();
                    Log.d("???????????? ????????????", "" + response?.body().toString())
                    Log.d("???????????? ????????????", "" + call.request())

                }
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


    private fun sharedSaveData() {
        val pref = getSharedPreferences("pref_user_data", 0)
        val edit = pref.edit()

        edit.putInt("user_table_id", user_table_id)
        edit.putString("loginUserId", loginUserId)
        edit.putString("loginUserNickname", loginUserNickname)
        edit.putString("userThumbnailImage", userThumbnailImage)
        edit.putString("userGender", userGender)
        edit.putInt("userAge",userAge)
        edit.putBoolean("login_check", true)
        edit.putInt("ranking_explanation_check",ranking_explanation_check)


        edit.apply()//????????????


    }

    private fun sharedLoadData() {
        val pref = getSharedPreferences("pref_user_data", 0)
        user_table_id = pref.getInt("user_table_id", 0)
        loginUserId = pref.getString("loginUserId", "")!!
        loginUserNickname = pref.getString("loginUserNickname", "")!!
        userThumbnailImage = pref.getString("userThumbnailImage", "")!!
        userAge = pref.getInt("userAge",0)
        userGender = pref.getString("userGender","")!!
        sharedLoginCheckBoolean = pref.getBoolean("login_check", false)!!
        ranking_explanation_check = pref.getInt("ranking_explanation_check",0)

        //??????????????? ??????
        if (sharedLoginCheckBoolean == true) {
            val mainFragmentJoin = Intent(this@MainActivity, MainFragmentActivity::class.java)
            if(intent.hasExtra("FCMRoomId")){
                val roomId = intent.getStringExtra("FCMRoomId")
                mainFragmentJoin.putExtra("FCMRoomId",roomId)
            }
            startActivity(mainFragmentJoin)
            finish()
        }

    }



    @TargetApi(30)
    private fun Context.checkBackgroundLocationPermissionAPI30() {
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            return
        } else {
            startActivity(Intent(this,PermissionGrantedActivity::class.java))
//            AlertDialog.Builder(this)
//                .setTitle("?????? ????????????")
//                .setMessage("????????????????????? ?????????????????? ??????????????????.")
//                .setPositiveButton("??????") { _, _ ->
////                     this request will take user to Application's Setting page
////                    requestPermissions(
////                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
////                        backgroundLocationRequestCode
////                    )
//                    val intent = Intent(
//                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                        Uri.parse("package:$packageName")
//                    )
//                    startActivity(intent)
//
//                }
//                .setNegativeButton("??????") { dialog, _ ->
//                    dialog.dismiss()
//                    onBackPressed()
//                }
//                .setCancelable(false)
//                .create()
//                .show()
        }


    }

    private fun Context.checkSinglePermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}