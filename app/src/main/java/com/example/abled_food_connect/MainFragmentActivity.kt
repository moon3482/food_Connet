package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.example.abled_food_connect.Fragments.*
import com.example.abled_food_connect.databinding.ActivityMainFragmentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class MainFragmentActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    val binding by lazy { ActivityMainFragmentBinding.inflate(layoutInflater) }
    private lateinit var mainFragment: MainFragment
    private lateinit var reviewFragment: ReviewFragment
    private lateinit var rankingFragment: RankingFragment
    private lateinit var chatingFragment: ChatingFragment
    private lateinit var myPageFragment: MyPageFragment

    companion object {
        const val TAG: String = "홈 액티비티 로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(TAG, "홈액티비티 onCreate()")

        binding.bottomNav.setOnNavigationItemSelectedListener(
            onBottomOnNavigationItemSelectedListener
        )
        setSupportActionBar(binding.maintoolbar)
        val tb = supportActionBar!!
        tb.setTitle("홈")
        mainFragment = MainFragment.newInstance()
        binding.mainFragmentCreateReviewBtn.hide()
        supportFragmentManager.beginTransaction().add(R.id.view, mainFragment).commit()


    }


    private val onBottomOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            showFloatingButton(it.itemId)
            when (it.itemId) {

                R.id.menu_home -> {
                    Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")

                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("홈")

                    mainFragment = MainFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view, mainFragment)
                        .commit()
                }
                R.id.menu_review -> {
                    Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("리뷰")
                    binding.mainFragmentCreateRoomBtn.hide()
                    reviewFragment = ReviewFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view, reviewFragment)
                        .commit()
                }
                R.id.menu_ranking -> {
                    Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("랭킹")
                    binding.mainFragmentCreateRoomBtn.hide()
                    rankingFragment = RankingFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view, rankingFragment)
                        .commit()
                }
                R.id.menu_chat -> {
                    Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("채팅")
                    binding.mainFragmentCreateRoomBtn.hide()
                    chatingFragment = ChatingFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view, chatingFragment)
                        .commit()
                }
                R.id.menu_mypage -> {
                    Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")
                    myPageFragment = MyPageFragment.newInstance()
                    setSupportActionBar(binding.maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("마이페이지")
                    binding.mainFragmentCreateRoomBtn.hide()
                    supportFragmentManager.beginTransaction().replace(R.id.view, myPageFragment)
                        .commit()
                }

            }

            true
        }
    private fun showFloatingButton(itemid: Int){
        when(itemid){
            R.id.menu_home ->{
                binding.mainFragmentCreateRoomBtn.show()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_review ->{
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.show()
            }
            R.id.menu_ranking ->{
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_chat ->{
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }
            R.id.menu_mypage ->{
                binding.mainFragmentCreateRoomBtn.hide()
                binding.mainFragmentCreateReviewBtn.hide()
            }

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
        val tb: Toolbar = findViewById(R.id.maintoolbar)
        tb.visibility = View.VISIBLE
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.menu_home -> {
                Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")
                val fragmentHoem = MainFragment()

                transaction.replace(R.id.view, fragmentHoem, "home")
            }
            R.id.menu_review -> {
                Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")

            }
            R.id.menu_ranking -> {
                Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")

            }
            R.id.menu_chat -> {
                Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")

            }
            R.id.menu_mypage -> {
                Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")

            }

        }
    }
}