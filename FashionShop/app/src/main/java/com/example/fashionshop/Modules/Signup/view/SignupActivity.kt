package com.example.fashionshop.Modules.Signup.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fashionshop.MainActivity
import com.example.fashionshop.Modules.Login.view.LoginActivity
import com.example.fashionshop.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.loginBtn1.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.loginBtn2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}