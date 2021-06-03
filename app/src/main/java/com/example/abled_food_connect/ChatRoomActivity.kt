package com.example.abled_food_connect

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.abled_food_connect.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {
    val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater) }
    val TAG = "그룹채팅 액티비티"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.groupChatRoomToolbar)
        val tb = supportActionBar!!
        tb.title = "그룹 채팅방"
        binding.chatDrawerLinear.setOnTouchListener { _, _ -> true }


    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart 호출")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onStart 호출")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume 호출")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause 호출")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop 호출")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy 호출")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_chat_room_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.groupChatMenu -> {

                Toast.makeText(this, "버튼 눌림", Toast.LENGTH_SHORT).show()
                binding.chatDrawerLayout.openDrawer(Gravity.RIGHT)
                true

            }
            else -> {
                super.onOptionsItemSelected(item)
            }

        }
    }

    override fun onBackPressed() {

        if (binding.chatDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.chatDrawerLayout.closeDrawer(Gravity.RIGHT)
        } else {
            super.onBackPressed()
        }
    }
}