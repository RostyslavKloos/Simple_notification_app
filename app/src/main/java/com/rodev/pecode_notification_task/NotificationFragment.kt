package com.rodev.pecode_notification_task

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rodev.pecode_notification_task.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment(R.layout.fragment_notification) {

    private val binding by viewBinding(FragmentNotificationBinding::bind)
    private var number: Int? = null

    companion object {
        const val FRAGMENT_NUMBER = "fragmentNumber"
    }

    private val CHANNEL_ID = "channelID"
    private val CHANNEL_NAME = "channelName"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchArguments()
        setUpClickListeners()
    }

    private fun fetchArguments() {
        arguments?.let {
            number = it.getInt(FRAGMENT_NUMBER)
        }
    }

    private fun setUpClickListeners() {
        with(binding) {
            tvCreateNotification.setOnClickListener {
                createNotificationChannel()
                createNotification()
            }
        }
    }

    private fun createNotification() {
        if (isNotificationAllowed()) {
            val intent = Intent(requireActivity(), MainActivity::class.java).also {
                it.putExtra(FRAGMENT_NUMBER, number)
            }
            val pendingIntent = TaskStackBuilder.create(requireActivity()).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT)
            }

            val contextText = "${getString(R.string.notification_description)} $number"
            val notification = Notification.Builder(requireContext(), CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(contextText)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build()
            val notificationManager = NotificationManagerCompat.from(requireContext())
            notificationManager.notify(number!!, notification)
        }
    }

    private fun createNotificationChannel() {
        if (isNotificationAllowed()) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            val manager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.notification_not_supported),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isNotificationAllowed(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}