package com.example.abled_food_connect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.Toast
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


class MainActivity : AppCompatActivity() {

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






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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



        //네이버 로그인 버튼 연결
        val goToUserRegisterActivity : Button = findViewById(R.id.goToUserRegisterActivity)
        //로그인 버튼을 눌렀을때 mOAuthLoginHandler 실행
        goToUserRegisterActivity.setOnClickListener {
            val intent = Intent(applicationContext, UserRegisterActivity::class.java)
            startActivity(intent)
        }






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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
    } // onActivityResult

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

                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                    //startActivity(Intent (this, StudyRecommendActivity::class.java))
                } else {
                    // 로그인 실패 시
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    } //firebaseAuthWithGoogle






}