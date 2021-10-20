package com.rodev.pecode_notification_task.base

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

inline fun <reified T : Fragment>
        newFragmentInstance(vararg params: Pair<String, Any?>): T =
    T::class.java.newInstance().apply {
        arguments = bundleOf(*params)
    }