package com.example.abled_food_connect.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.abled_food_connect.*
import com.example.abled_food_connect.data.UserProfileData
import com.example.abled_food_connect.retrofit.API
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MyPageFragment:Fragment() {


    lateinit var userProfileIv : ImageView
    lateinit var userProfileNicNameTv : TextView
    lateinit var userProfileIntroductionTv : TextView

    lateinit var social_login_type : String
    lateinit var table_user_id :String




    lateinit var tierBadgeImageIv : ImageView
    lateinit var tierTv : TextView
    lateinit var rankingPointTv : TextView

    lateinit var rankTv : TextView



    // Firebase Authentication 관리 클래스
    // firebase 인증을 위한 변수
    var auth: FirebaseAuth? = null

    // 구글 로그인 연동에 필요한 변수
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    lateinit var callbackManager: CallbackManager


    companion object{
        const val TAG : String = "마이페이지 프래그먼트 로그"
        fun newInstance(): MyPageFragment{
            return MyPageFragment()
        }

            lateinit var progressBar: ProgressDialog

    }

    override fun onResume() {
        super.onResume()

        //작성자 프로필
        Glide.with(userProfileIv.context)
            .load(getString(R.string.http_request_base_url)+MainActivity.userThumbnailImage)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(userProfileIv)

        userProfileNicNameTv.text = MainActivity.loginUserNickname
        //유저 정보를 DB에서 가져온다.
        userProfileLoading(MainActivity.user_table_id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"마이페이지 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"마이페이지 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mypage_fragments, container, false)


        userProfileIv = view.findViewById<ImageView>(R.id.userProfileIv)
        userProfileNicNameTv = view.findViewById<TextView>(R.id.userProfileNicNameTv)
        userProfileIntroductionTv = view.findViewById<TextView>(R.id.userProfileIntroductionTv)
        progressBar = ProgressDialog(requireContext())
        progressBar.setMessage("로딩중..")
        progressBar.setProgressStyle(ProgressDialog.BUTTON_POSITIVE)
        progressBar.show()



        tierBadgeImageIv = view.findViewById<ImageView>(R.id.tierBadgeImageIv)
        tierTv = view.findViewById<TextView>(R.id.tierTv)
        rankingPointTv = view.findViewById<TextView>(R.id.rankingPointTv)

        rankTv = view.findViewById<TextView>(R.id.rankTv)


        val toUserProfileModifyActivityBtn = view.findViewById<TextView>(R.id.toUserProfileModifyActivityBtn)


        toUserProfileModifyActivityBtn.setOnClickListener{
            val intent = Intent(requireContext(), UserProfileModifyActivity::class.java)
            startActivity(intent)
        }


        val toMoveWrittenReviewListActivityBtn = view.findViewById<LinearLayout>(R.id.toMoveWrittenReviewListActivityBtn)
        toMoveWrittenReviewListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileClickedReviewGridListActivity : Intent = Intent(context, UserProfileClickedReviewGridListActivity::class.java)
            toUserProfileClickedReviewGridListActivity.putExtra("writer_user_tb_id", MainActivity.user_table_id)
            startActivity(toUserProfileClickedReviewGridListActivity, null)
        })


        val toMoveReviewWritingActivityBtn = view.findViewById<LinearLayout>(R.id.toMoveReviewWritingActivityBtn)
        toMoveReviewWritingActivityBtn.setOnClickListener(View.OnClickListener {
            var toUnwrittenReviewListActivity : Intent = Intent(context, UnwrittenReviewListActivity::class.java)
            startActivity(toUnwrittenReviewListActivity, null)
        })


        val toMoveUserProfileBadgeListActivityBtn = view.findViewById<LinearLayout>(R.id.toMoveUserProfileBadgeListActivityBtn)
        toMoveUserProfileBadgeListActivityBtn.setOnClickListener(View.OnClickListener {
            var toUserProfileBadgeListActivity : Intent = Intent(context, UserProfileBadgeListActivity::class.java)
            toUserProfileBadgeListActivity.putExtra("user_tb_id",MainActivity.user_table_id)
            toUserProfileBadgeListActivity.putExtra("user_nicname",MainActivity.loginUserNickname)
            startActivity(toUserProfileBadgeListActivity, null)
        })


        val toMoveUserProfileEvaluationListActivity = view.findViewById<LinearLayout>(R.id.toMoveUserProfileEvaluationListActivity)
        toMoveUserProfileEvaluationListActivity.setOnClickListener(View.OnClickListener {
            var toUserProfileEvaluationListActivity : Intent = Intent(context, UserProfileEvaluationListActivity::class.java)
            toUserProfileEvaluationListActivity.putExtra("user_tb_id",MainActivity.user_table_id)
            toUserProfileEvaluationListActivity.putExtra("user_nicname",MainActivity.loginUserNickname)
            startActivity(toUserProfileEvaluationListActivity, null)
        })


        val toMyPageUserScheduleActivityBtn = view.findViewById<LinearLayout>(R.id.toMyPageUserScheduleActivityBtn)

        toMyPageUserScheduleActivityBtn.setOnClickListener{
            val intent = Intent(requireContext(), MyPageUserScheduleActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = view.findViewById<LinearLayout>(R.id.logoutBtn)
        // 로그아웃
        logoutBtn.setOnClickListener {
            var builder = AlertDialog.Builder(logoutBtn.context)
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃 하시겠습니까?")


            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->


                            activity?.let{

                                logout()

                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                } else { intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                activity!!.finish()


                            }

                        DialogInterface.BUTTON_NEGATIVE ->
                            Log.d(TAG, "로그아웃 취소")
                    }
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)


            builder.show()
        }


        var accountDeleteBtn = view.findViewById<LinearLayout>(R.id.accountDeleteBtn)
        // 로그아웃
        accountDeleteBtn.setOnClickListener {
            var builder = AlertDialog.Builder(logoutBtn.context)
            builder.setTitle("탈퇴")
            builder.setMessage("회원탈퇴를 하시겠습니까?")


            // 버튼 클릭시에 무슨 작업을 할 것인가!
            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE ->


                            activity?.let{

                                accountDelete()




                            }

                        DialogInterface.BUTTON_NEGATIVE ->
                            Log.d(TAG, "회원탈퇴 취소")
                    }
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", listener)


            builder.show()
        }


        /*
        구글 로그인 구현
         */

        // firebaseauth를 사용하기 위한 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // xml에서 구글 로그인 버튼 코드 가져오기





        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }


        callbackManager = CallbackManager.Factory.create()

        try {
            val info = requireContext().packageManager.getPackageInfo(
                "com.example.abled_food_connect;",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }




        return view

    }


    fun logout(){


        //카카오 로그아웃

        if(social_login_type == "KAKAO"){
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
        }

        else if(social_login_type == "FACEBOOK"){
            //페이스북 로그아웃
            LoginManager.getInstance().logOut()
            Log.d(TAG, "logout: facebook 로그아웃")
        }

        else if(social_login_type == "NAVER"){
            //네이버 로그아웃
            OAuthLogin.getInstance().logout(context)
            Log.d("TAG", OAuthLogin.getInstance().getState(context).toString())
            OAuthLogin.getInstance().getState(context)
        }

        else if(social_login_type == "GOOGLE"){

            Log.d(TAG, "logout: 구글 로그아웃")

            //구글 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
            var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


            var googleSignInClient: GoogleSignInClient? = null

            googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }

            if (googleSignInClient != null) {

                //실질적인 구글 클라이언트 로그인 기록이 초기화된다.
                googleSignInClient.signOut()
            }


        }















        val pref = activity?.getSharedPreferences("pref_user_data",0)
        val edit = pref?.edit()
        if (edit != null) {
            edit.putBoolean("login_check",false)
            edit.apply()//저장완료
        }




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
                if (task.isSuccessful) {

                    val user = Firebase.auth.currentUser
                    val uid = user!!.uid
                    Log.d("사용자 식별자 id", uid)

                    if(table_user_id == uid){
                        Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_LONG).show()


                        //구글 계정연동 해제
                        auth!!.getCurrentUser()!!.delete()

                        //구글 로그아웃
                        FirebaseAuth.getInstance().signOut()

                        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
                        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()


                        var googleSignInClient: GoogleSignInClient? = null

                        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }

                        if (googleSignInClient != null) {

                            //실질적인 구글 클라이언트 로그인 기록이 초기화된다.
                            googleSignInClient.signOut()
                        }





                        //로그인화면으로 이동
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else { intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                        requireActivity().finish()


                    }else{
                        Toast.makeText(requireContext(), "현재 로그인 중인 계정과 동일한 계정이 아닙니다.", Toast.LENGTH_LONG).show()
                    }





                } else {
                    // 로그인 실패 시
                    Toast.makeText(requireContext(), "구글로그인실패", Toast.LENGTH_LONG).show()
                }
            }
    } //firebaseAuthWithGoogle



    fun accountDelete(){


        //카카오 로그아웃 및 연결해제



        if(social_login_type == "KAKAO"){

            Toast.makeText(context, "탈퇴를 위해 카카오 로그인이 필요합니다. 카카오 로그인 후, 탈퇴가 완료됩니다.", Toast.LENGTH_SHORT).show()

            UserApiClient.instance.loginWithKakaoAccount(requireContext(), prompts = listOf(Prompt.LOGIN)) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패", error)
                    Toast.makeText(requireContext(), "아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show()
                }
                else if (token != null) {
                    Log.i(TAG, "로그인 성공 ${token.accessToken}")



                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            Log.i(
                                TAG, "사용자 정보 요청 성공" +
                                        "\n회원번호: ${user.id}" +
                                        "\n이메일: ${user.kakaoAccount?.email}" +
                                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                        "\n프로필사진 원본: ${user.kakaoAccount?.profile?.profileImageUrl}" +
                                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                            )

                            if(user.id.toString() == table_user_id){
                                Toast.makeText(context, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                                UserApiClient.instance.unlink  { error ->
                                    if (error != null) {
                                        Log.e(TAG, "카카오 회원 탈퇴 실패.", error)
                                    }
                                    else {
                                        Log.i(TAG, "카카오 회원 탈퇴성공. SDK에서 토큰 삭제됨")

                                        UserApiClient.instance.logout { error ->
                                            if (error != null) {
                                                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                                            }
                                            else {
                                                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                                            }
                                        }

                                        val intent = Intent(context, MainActivity::class.java)
                                        startActivity(intent)
                                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                                        } else { intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                                        requireActivity().finish()

                                    }
                                }
                            }else{
                                Toast.makeText(context, "현재 접속중인 계정과 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                            }


                        }
                    }




                }
            }



        }

        else if(social_login_type == "FACEBOOK"){
            //페이스북 로그아웃 및 연결 해제


            LoginManager.getInstance().logOut()

            Log.d(TAG, "logout: facebook 로그아웃")

            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))

            LoginManager.getInstance()
                //로그아웃시 웹뷰설정을 하지 않으면, 크롬과 같은 인터넷 브라우져로 로그인을하게 된다.
                //브라우져 로그인을 하게되면, 로그인 정보를 브라우져 캐시에 저장하고 있어 브라우져캐시를 지우지 않는한 페이스북 계정 입력창이 나오지 않는다.
                //만약 다른 페이스북 계정으로 로그인 하고 싶은 유저를 위하여 웹뷰온리 옵션을 추가해주었다.
                .setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY)
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        Log.d("TAG", "Success Login")
                        Toast.makeText(requireContext(), "Login 성공", Toast.LENGTH_LONG).show()
                        //삭제요청 리퀘스트
                        var request = GraphRequest(loginResult?.accessToken, "/me/permissions", null, HttpMethod.DELETE, GraphRequest.Callback() { response -> })
                        request.executeAsync()



//                 val mainFragmentJoin = Intent(this@MainActivity,MainFragmentActivity::class.java)
//                    startActivity(mainFragmentJoin)
                    }

                    override fun onCancel() {
                        Toast.makeText(requireContext(), "Login Cancelled", Toast.LENGTH_LONG).show()
                    }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
                    }
                })






//                val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
//                    try {
//                        //here is the data that you want
//
//                        userEmail = `object`.getString("email")
//                        Log.e("TAGG", userEmail)
//                        userName = `object`.getString("name")
//                        Log.e("TAGG", userName)
//                        jobj1 = `object`.optJSONObject("picture")
//                        Log.e("TAGG", jobj1.toString())
//                        jobj2 = jobj1.optJSONObject("data")
//                        Log.e("TAGG", jobj2.toString())
//                        userPicture = jobj2.getString("url")
//                        Log.e("TAGG", userPicture)
//
//
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                    goTomain()
//
//                }
//
//                val parameters = Bundle()
//                parameters.putString("fields", "name,email,picture")
//                request.parameters = parameters




        }

        else if(social_login_type == "NAVER"){


            //기존 네이버 로그인 세션을 제거한다.
            OAuthLogin.getInstance().logout(context)
            Toast.makeText(context, "탈퇴를 위해 네이버 아이디 로그인이 필요합니다. 네이버 로그인 후, 탈퇴가 완료됩니다.", Toast.LENGTH_SHORT).show()

            //네이버 로그인 창을 띄운다.
            //로그인에 성공하면 계정연결 해제가 이루어진다.
            OAuthLogin.getInstance().startOauthLoginActivity(activity,mOAuthLoginHandler)


        }

        else if(social_login_type == "GOOGLE"){

            //구글 연결해제 과정


            Log.d(TAG, "logout: 구글 로그아웃")

            //구글 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
            var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


            var googleSignInClient: GoogleSignInClient? = null

            googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }

            if (googleSignInClient != null) {

                //실질적인 구글 클라이언트 로그인 기록이 초기화된다.
                googleSignInClient.signOut()
            }

            googleLogin()
            Toast.makeText(context, "현재 계정과 동일한 구글 계정을 선택해주세요. 로그인한 계정과 동일할 경우 탈퇴처리가 완료됩니다.", Toast.LENGTH_LONG).show()




        }




        val pref = activity?.getSharedPreferences("pref_user_data",0)
        val edit = pref?.edit()
        if (edit != null) {
            edit.putBoolean("login_check",false)
            edit.apply()//저장완료
        }




    }




    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {

                // android.os.NetworkOnMainThreadException 에러 저거
                // 메인 쓰레드에서 네트워크 처리를 해주려면, 다음 두 줄을 추가해주면 됩니다.
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                // 네이버 회원 프로필 조회
                var jObject = JSONObject(
                    OAuthLogin.getInstance().requestApi(
                        context, OAuthLogin.getInstance().getAccessToken(
                            requireContext()
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

                if(table_user_id == id){
                    OAuthLogin.getInstance().logoutAndDeleteToken(context)
                    Log.d("TAG", OAuthLogin.getInstance().getState(context).toString())
                    OAuthLogin.getInstance().getState(context)


                    Toast.makeText(context, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); }

                    requireActivity().finish()
                }else{
                    Toast.makeText(context, "현재 접속 중인 계정과 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

                    OAuthLogin.getInstance().logoutAndDeleteToken(context)
                    Log.d("TAG", OAuthLogin.getInstance().getState(context).toString())
                    OAuthLogin.getInstance().getState(context)
                }








            } else {
                Toast.makeText(requireContext(), "아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show()
                Log.d(TAG, "네이버회원탈퇴실패")
            }
        }

    }


    fun userProfileLoading(user_tb_id:Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API.UserProfileDataInterface::class.java)

        //어떤 리뷰를 선택했는지 확인하는 변수 + 좋아요 클릭여부를 확인하기 위하여 사용자 id보냄
        val user_profile_data_get = api.user_profile_data_get(user_tb_id)


        user_profile_data_get.enqueue(object : Callback<UserProfileData> {
            override fun onResponse(
                call: Call<UserProfileData>,
                response: Response<UserProfileData>
            ) {
                Log.d(ReviewFragment.TAG, "프로필 정보 : ${response.raw()}")
                Log.d(ReviewFragment.TAG, "프로필 정보 : ${response.body().toString()}")

                var items : UserProfileData? =  response.body()





                social_login_type = items!!.social_login_type
                table_user_id =  items!!.user_id
                Log.d("소셜로그인 타입은?", social_login_type)


                if(items!!.introduction == null||items!!.introduction.length<1){
                    userProfileIntroductionTv.text = "자기소개가 없습니다."
                }else{
                    userProfileIntroductionTv.text = items!!.introduction
                }



                //랭킹관련

                tierTv.text = "${items.tier}"
                rankingPointTv.text = "${items.rank_point}PT"


                Glide.with(requireContext())
                    .load(getString(R.string.http_request_base_url)+items!!.tier_image)
                    .into(tierBadgeImageIv)

                rankTv.text = "(${items.rank}위)"

                progressBar.dismiss()

            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.d("유저정보가져오기실패?", "실패 : $t")
            }
        })
    }


}