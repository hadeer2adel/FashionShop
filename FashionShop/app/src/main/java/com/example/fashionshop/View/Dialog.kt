package com.example.fashionshop.View

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.LayoutInflater
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.fashionshop.Modules.Authentication.view.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.fashionshop.R

fun showDialog(context: Context, titleId: Int, bodyId: Int, onAllow: ()->Unit) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(titleId))
        setMessage(context.getString(bodyId))
        setPositiveButton(context.getString(R.string.sure)) { dialog, _ ->
            onAllow()
            dialog.dismiss()
        }
        setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        create()
        show()
    }
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

