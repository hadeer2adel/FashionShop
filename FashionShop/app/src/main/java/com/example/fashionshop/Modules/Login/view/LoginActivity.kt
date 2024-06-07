package com.example.fashionshop.Modules.Login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fashionshop.MainActivity
import com.example.fashionshop.Modules.Signup.view.SignupActivity
import com.example.fashionshop.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.signupBtn1.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.signupBtn2.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}