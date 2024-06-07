package com.example.fashionshop.network

import com.example.fashionshop.model.customers

interface RemoteDataSource {
    suspend fun getcutomers(): customers

}