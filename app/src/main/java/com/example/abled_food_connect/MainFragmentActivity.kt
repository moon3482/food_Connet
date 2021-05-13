package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.abled_food_connect.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_fragment.*

class MainFragmentActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
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
        setContentView(R.layout.activity_main_fragment)
        Log.d(TAG, "홈액티비티 onCreate()")

        bottom_nav.setOnNavigationItemSelectedListener(onBottomOnNavigationItemSelectedListener)
        setSupportActionBar(maintoolbar)
        val tb = supportActionBar!!
        tb.setTitle("홈")
        mainFragment = MainFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.view,mainFragment).commit()
    }

    private val onBottomOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {



            when (it.itemId) {
                R.id.menu_home -> {
                    Log.d(TAG, "메인 엑티비티 홈 버튼 클릭")
                    setSupportActionBar(maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("홈")
                    mainFragment = MainFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view,mainFragment).commit()
                }
                R.id.menu_review -> {
                    Log.d(TAG, "메인 엑티비티 리뷰 버튼 클릭")
                    setSupportActionBar(maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("리뷰")
                    reviewFragment = ReviewFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view,reviewFragment).commit()
                }
                R.id.menu_ranking -> {
                    Log.d(TAG, "메인 엑티비티 랭킹 버튼 클릭")
                    setSupportActionBar(maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("랭킹")
                    rankingFragment = RankingFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view,rankingFragment).commit()
                }
                R.id.menu_chat -> {
                    Log.d(TAG, "메인 엑티비티 채팅 버튼 클릭")
                    setSupportActionBar(maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("채팅")
                    chatingFragment = ChatingFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.view,chatingFragment).commit()
                }
                R.id.menu_mypage -> {
                    Log.d(TAG, "메인 엑티비티 마이페이지 버튼 클릭")
                    myPageFragment = MyPageFragment.newInstance()
                    setSupportActionBar(maintoolbar)
                    val tb = supportActionBar!!
                    tb.setTitle("마이페이지")
                    supportFragmentManager.beginTransaction().replace(R.id.view,myPageFragment).commit()
                }

            }

            true
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