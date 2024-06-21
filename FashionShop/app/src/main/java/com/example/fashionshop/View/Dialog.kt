package com.example.fashionshop.View

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.fashionshop.R

fun showDialog(context: Context, titleId: Int, bodyId: Int, onAllow: ()->Unit) {
    MaterialAlertDialogBuilder(
        context,
        com.google.android.material.R.style.MaterialAlertDialog_Material3
    )
        .setTitle(context.getString(titleId))
        .setMessage(context.getString(bodyId))
        .setPositiveButton(context.getString(R.string.sure)) { _, _ -> onAllow() }
        .setNegativeButton(context.getString(R.string.cancel), null)
        .show()
}

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}

