package com.example.fashionshop.Modules.Authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.fashionshop.MainActivity
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModel
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModelFactory
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        initViewModel()

        binding.loginBtn1.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.loginBtn2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.signupBtn.setOnClickListener {
            if (validation()) {
                val request = CustomerRequest(
                    CustomerRequest.Customer(
                        first_name = binding.firstName.text.toString(),
                        last_name = binding.lastName.text.toString(),
                        email = binding.email.text.toString().trim()
                    )
                )
                val name = binding.firstName.text.toString() + " " + binding.lastName.text.toString()
                val email = binding.email.text.toString().trim()
                val password = binding.password.text.toString()
                fireBaseAuth(name, email, password, request)
            }
        }
    }

    fun fireBaseAuth(name: String, email: String, password: String, request: CustomerRequest){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                    user?.let { getFirebaseIdToken(it, request) }
                } else {
                    Toast.makeText(this, "Create account failed", Toast.LENGTH_SHORT).show()
                    binding.emailTxt.error = "Enter with another email"
                    binding.passwordTxt.error = "Enter with another password"
                }
            }
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)

        val factory = AuthenticationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AuthenticationViewModel::class.java)

        lifecycleScope.launch {
            viewModel.customer.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {}
                    is NetworkState.Success ->{
                        viewModel.saveCustomerData(this@SignupActivity, response.data.customer!!)
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                    }
                    is NetworkState.Failure ->{
                        binding.progressBar.visibility = View.GONE
                        binding.screen.visibility = View.VISIBLE
                        Toast.makeText(this@SignupActivity, response.error.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.i("TAG", "Failure: "+ response.error.message.toString())
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {
        val name = binding.firstName.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        var valid = false
        when {
            name.isEmpty() -> binding.firstNameTxt.error = "Name is empty"
            email.isEmpty() -> binding.emailTxt.error = "Email is empty"
            !email.matches(emailRegex.toRegex()) -> binding.emailTxt.error = "Invalid email format"
            password.isEmpty() -> binding.passwordTxt.error = "Password is empty"
            !password.matches(passwordRegex.toRegex()) -> binding.passwordTxt.error =
                "At least contains 8 characters \nat least one upper letter, one lowercase letter, and one digit"
            confirmPassword.isEmpty() -> binding.confirmPasswordTxt.error = "Confirm Password is empty"
            password != confirmPassword -> binding.confirmPasswordTxt.error = "Password and confirm password doesn't match"
            else -> valid = true
        }

        return valid
    }

    private fun getFirebaseIdToken(user: FirebaseUser, request: CustomerRequest) {
        user.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token
                idToken?.let {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.screen.visibility = View.GONE
                    viewModel.createCustomer(request)
                }
            } else {
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
