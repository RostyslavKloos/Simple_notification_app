package com.rodev.pecode_notification_task.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class BaseFragmentPagerAdapter<T : Fragment>(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments: ArrayList<T> = arrayListOf()

    fun addFragment(fragment: T) {
        fragments.add(fragment)
    }

    fun removeFragment() {
        if (fragments.size > 1)
        fragments.removeLast()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}