package com.example.fashionshop.Service.Networking

sealed class NetworkState<out T> {
    data class Success<T>(val data: T): NetworkState<T>()
    data class Failure(val error: Throwable): NetworkState<Nothing>()
    object Loading: NetworkState<Nothing>()
}