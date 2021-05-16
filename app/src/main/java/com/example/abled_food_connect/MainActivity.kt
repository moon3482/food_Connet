package com.example.abled_food_connect

import android.annotation.SuppressLint

import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import org.json.JSONObject


import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import android.content.Intent
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity(){
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
    네이버 로그인
    최초 작성 21.05.10
    작성자 박의조
     */
    //OAuthLogin 객체
    lateinit var mOAuthLoginInstance : OAuthLogin
    //애플리케이션 컨텍스트
    lateinit var mContext: Context




    /*
    구글 로그인
    최초 작성 21.05.10
    작성자 박의조
     */
    // Firebase Authentication 관리 클래스
    // firebase 인증을 위한 변수
    var auth : FirebaseAuth ? = null

    // 구글 로그인 연동에 필요한 변수
    var googleSignInClient : GoogleSignInClient ? = null
    var GOOGLE_LOGIN_CODE = 9001




    lateinit var nextIntent : Intent



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnFacebookLogin : LinearLayout = findViewById(R.id.btnFacebookLogin)
        val btnKakaoLogin : LinearLayout = findViewById(R.id.btnKakaoLogin)

        val keyHash = Utility.getKeyHash(this)
        Log.e("해시",keyHash)



        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

            if (error != null) {

                Log.e(TAG, "로그인 실패", error)

            }

            else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        Log.i(TAG, "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진 원본: ${user.kakaoAccount?.profile?.profileImageUrl}"+
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                    }
                }
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
                val mainFragmentJoin = Intent(this@MainActivity,MainFragmentActivity::class.java)
                startActivity(mainFragmentJoin)
            }

        }


        btnKakaoLogin.setOnClickListener {
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
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
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d("TAG", "Success Login")
                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                 val mainFragmentJoin = Intent(this@MainActivity,MainFragmentActivity::class.java)
                    startActivity(mainFragmentJoin)
                }

                override fun onCancel() {
                    Toast.makeText(this@MainActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_LONG).show()
                }
            })






        nextIntent = Intent(this, UserRegisterActivity::class.java)

        /*
        네이버 로그인 구현
         */
        //  애플리케이션 등록 후, 발급받은 클라이언트 ID
        val naver_client_id = "zuHsHf3IHbg9uBONNNyv"
        // 애플리케이션 등록 후, 발급받은 시크릿 키
        val naver_client_secret = "nMQ60W3SjG"
        // 네이버 앱의 로그인 화면에 표시할 애플리케이션 이름.
        val naver_client_name = "food_connect"

        //애플리케이션 컨텍스트
        mContext = this

        //네이버 아이디로 로그인 인스턴스를 얻습니다.
        mOAuthLoginInstance = OAuthLogin.getInstance()

        //네이버 아이디로 로그인 인스턴스에 클라이언트 정보를 설정합니다.
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)

        //네이버 로그인 버튼 연결
        val buttonOAuthLoginImg : OAuthLoginButton = findViewById(R.id.buttonOAuthLoginImg)
        //로그인 버튼을 눌렀을때 mOAuthLoginHandler 실행
        buttonOAuthLoginImg.setOAuthLoginHandler(mOAuthLoginHandler)







        /*
        구글 로그인 구현
         */

        // firebaseauth를 사용하기 위한 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // xml에서 구글 로그인 버튼 코드 가져오기

        val btn_googleSignIn : SignInButton = findViewById(R.id.btn_googleSignIn)


        // 구글 로그인 버튼 클릭 시 이벤트 : googleLogin function 실행
        btn_googleSignIn.setOnClickListener {
            googleLogin()
        }

        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)












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
                if (jsonObject.has("id")) {
                    val facebookId = jsonObject.getString("id")
                    Log.i("Facebook Id: ", facebookId.toString())
                    id = facebookId.toString()
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

        if(requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if(result.isSuccess) {

                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        } //if
    }





    /*
        네이버 로그인 메서드
    */
    //로그인 버튼 클릭 시, 결과 처리 로그인핸들러
    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {

            // 로그인에 성공했을 때 실행.
            if (success) {

                //로그인 성공 토스트메시지
                Toast.makeText(
                    baseContext, "로그인 성공했습니다.", Toast.LENGTH_SHORT
                ).show()


                // android.os.NetworkOnMainThreadException 에러 저거
                // 메인 쓰레드에서 네트워크 처리를 해주려면, 다음 두 줄을 추가해주면 됩니다.
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                // 네이버 회원 프로필 조회
                var jObject = JSONObject(
                    mOAuthLoginInstance.requestApi(
                        mContext, mOAuthLoginInstance.getAccessToken(
                            baseContext
                        ), "https://openapi.naver.com/v1/nid/me"
                    ).toString()
                )


                //유저정보를 담고있는 JSONObject
                val response = jObject.getString("response")

                jObject = JSONObject(response)


                //네이버 유저 고유아이디
                val id = jObject.getString("id")
                //네이버 유저 프로필
                var profile_image = jObject.getString("profile_image")
                //profile_image 문자열에 "\\"을 제거해야 제대로 된 이미지 url을 찾을 수 있다.
                profile_image = profile_image.replace("\\", "")
                Log.d("고유ID", id)
                Log.d("프로필이미지", profile_image)

                nextIntent.putExtra("user_id", id)
                nextIntent.putExtra("social_login_type", "NAVER")

                startActivity(nextIntent)



//                val accessToken: String = mOAuthLoginModule.getAccessToken(baseContext)
//                val refreshToken: String = mOAuthLoginModule.getRefreshToken(baseContext)
//                val expiresAt: Long = mOAuthLoginModule.getExpiresAt(baseContext)
//                val tokenType: String = mOAuthLoginModule.getTokenType(baseContext)
//                var intent = Intent(this, )

            } else {
                // 로그인에 실패했을 때 실행.
                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)

                //에러코드 토스트메시지 출력
                Toast.makeText(
                    baseContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    /*
        구글 로그인 메서드
    */


    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    } // googleLogin



    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // 로그인 성공 시
                    val user = Firebase.auth.currentUser
                    val name = user.displayName
                    val email = user.email
                    val photoUrl = user.photoUrl
                    val uid = user.uid

                    Log.d("나와라", "구글 로그인하였습니다.")
                    Log.d("이름", name)
                    Log.d("이메일", email)
                    Log.d("프로필사진", photoUrl.toString())
                    Log.d("사용자 식별자 id", uid)


                    nextIntent.putExtra("user_id", user.uid)
                    nextIntent.putExtra("social_login_type", "GOOGLE")
                    startActivity(nextIntent)

                    //Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                    //startActivity(Intent (this, StudyRecommendActivity::class.java))
                } else {
                    // 로그인 실패 시
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    } //firebaseAuthWithGoogle





}
