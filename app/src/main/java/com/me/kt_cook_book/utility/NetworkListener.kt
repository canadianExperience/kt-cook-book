package com.me.kt_cook_book.utility

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow


class NetworkListener : ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable = MutableStateFlow(false)

    fun checkNetworkAvailability(connectivityManager: ConnectivityManager): MutableStateFlow<Boolean> {
        connectivityManager.registerDefaultNetworkCallback(this)

        var isConnected = false

        val network = connectivityManager.activeNetwork
        val networkCapability = connectivityManager.getNetworkCapabilities(network)
        networkCapability?.let {
            if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                isConnected = true
            }
        }

        isNetworkAvailable.value = isConnected

        return isNetworkAvailable
    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}