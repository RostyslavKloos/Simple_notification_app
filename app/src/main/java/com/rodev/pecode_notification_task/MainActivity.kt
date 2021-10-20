package com.rodev.pecode_notification_task

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rodev.pecode_notification_task.NotificationFragment.Companion.FRAGMENT_NUMBER
import com.rodev.pecode_notification_task.base.BaseFragmentPagerAdapter
import com.rodev.pecode_notification_task.base.newFragmentInstance
import com.rodev.pecode_notification_task.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val PREF_SAVED_FRAGMENTS_COUNT = "prefSavedFragmentsCount"
    private var savedFragmentsCount = 0
    private var fragmentNumberArg = 0

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewPagerAdapter: BaseFragmentPagerAdapter<Fragment> by lazy {
        BaseFragmentPagerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchArguments()
        fetchSharedPreferences()
        setUpViewPager()
        setUpClickListeners()
    }

    private fun fetchArguments() {
        fragmentNumberArg = intent.getIntExtra(FRAGMENT_NUMBER, 0)
    }

    private fun fetchSharedPreferences() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        savedFragmentsCount = sharedPref.getInt(PREF_SAVED_FRAGMENTS_COUNT, 0)
    }

    private fun setUpViewPager() {
        if (isFragmentsSaved()) {
            restoreSavedFragments()
        } else
            addNewFragmentWithNumber()
        setFragmentNumber(0)
        binding.viewPager.adapter = viewPagerAdapter

        setupOnPageChangeCallback()

        if (fragmentNumberArg != 0) {
            binding.viewPager.currentItem = fragmentNumberArg - 1
        }
    }

    private fun isFragmentsSaved(): Boolean {
        return savedFragmentsCount > 0
    }

    private fun addNewFragmentWithNumber() {
        viewPagerAdapter.addFragment(
            newFragmentInstance<NotificationFragment>(
                FRAGMENT_NUMBER to viewPagerAdapter.itemCount + 1
            )
        )
    }

    private fun restoreSavedFragments() {
        for (i in 0 until savedFragmentsCount) {
            addNewFragmentWithNumber()
        }
    }

    private fun setupOnPageChangeCallback() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setFragmentNumber(position)
            }
        })
    }

    private fun setFragmentNumber(position: Int) {
        with(binding) {
            tvRemove.isVisible = position != 0
            val number = "${position + 1}"
            tvNumber.text = number
        }
    }

    private fun setUpClickListeners() {
        with(binding) {
            tvAdd.setOnClickListener {
                addButtonClicked()
            }

            tvRemove.setOnClickListener {
                removeButtonClicked()
            }
        }
    }

    private fun addButtonClicked() {
        addNewFragmentWithNumber()
        viewPagerAdapter.notifyItemInserted(viewPagerAdapter.itemCount)
        binding.viewPager.currentItem = viewPagerAdapter.itemCount
    }

    private fun removeButtonClicked() {
        val currentPosition: Int = binding.viewPager.currentItem
        if (currentPosition != 0) {
            viewPagerAdapter.removeFragment()
            viewPagerAdapter.notifyItemRemoved(viewPagerAdapter.itemCount)
        }

        cancelNotification(binding.viewPager.currentItem + 1)
    }

    private fun cancelNotification(notifyId: Int) {
        val notificationService = NOTIFICATION_SERVICE
        val notificationManager = this.getSystemService(notificationService) as NotificationManager
        notificationManager.cancel(notifyId)
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(PREF_SAVED_FRAGMENTS_COUNT, viewPagerAdapter.itemCount)
            apply()
        }
    }
}