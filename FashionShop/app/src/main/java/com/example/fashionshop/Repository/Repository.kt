package com.example.fashionshop.Repository

import com.example.fashionshop.Model.customers

interface Repository {
    suspend fun getcustomers():customers

}