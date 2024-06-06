package com.example.fashionshop.repository

import com.example.fashionshop.model.customers

interface Repository {
    suspend fun getcustomers():customers

}