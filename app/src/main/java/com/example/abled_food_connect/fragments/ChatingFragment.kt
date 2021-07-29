package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.ChatFragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChatingFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val TAG: String = "채팅 프래그먼트 로그"
        fun newInstance(): ChatingFragment {
            return ChatingFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "채팅 프래그먼트 onCreate()")

        viewPager = ViewPager2(requireContext())
        tabLayout = TabLayout(requireContext())

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "채팅 프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chating_fragments, container, false)
        viewPager = view.findViewById(R.id.chatViewPager)
        tabLayout = view.findViewById(R.id.ChatFragmentTabLayout)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = ChatFragmentViewPagerAdapter(childFragmentManager,lifecycle)
        pagerAdapter.addFragment(ChatGroupFragment())
        pagerAdapter.addFragment(ChatDMFragment())

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("페이지", "페이지${position}")
            }
        })
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "그룹채팅"
                }
                else -> {
                    tab.text = "DM"
                }
            }

        }.attach()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}