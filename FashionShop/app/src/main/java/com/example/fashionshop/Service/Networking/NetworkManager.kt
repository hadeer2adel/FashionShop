package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.customers

interface NetworkManager {
    suspend fun getcutomers(): customers

}