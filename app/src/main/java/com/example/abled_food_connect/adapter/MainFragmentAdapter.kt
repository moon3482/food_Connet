package com.example.abled_food_connect.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.abled_food_connect.fragments.*

class MainFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return 5
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MainFragment()
            1 -> ReviewFragment()
            2 -> RankingFragment()
            3 -> ChatingFragment()
            else -> MyPageFragment()

        }
    }


}