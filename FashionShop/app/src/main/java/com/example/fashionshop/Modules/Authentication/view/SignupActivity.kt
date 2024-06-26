package com.example.fashionshop.Modules.Authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.fashionshop.MainActivity
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModel
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModelFactory
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.View.isNetworkConnected
import com.example.fashionshop.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private val nameRegex = "^[a-zA-Z]{3,}$"
    private val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (isNetworkConnected(this)) {
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
                            first_name = binding.firstName.text.toString() + " " + binding.lastName.text.toString(),
                            last_name = "",
                            email = binding.email.text.toString().trim()
                        )
                    )
                    val name =
                        binding.firstName.text.toString() + " " + binding.lastName.text.toString()
                    val email = binding.email.text.toString().trim()
                    val password = binding.password.text.toString()
                    fireBaseAuth(name, email, password, request)
                }
            }
        }
        else
        {
            binding.screen.visibility = View.INVISIBLE
            binding.emptyView.visibility = View.VISIBLE
        }
    }

    fun fireBaseAuth(name: String, email: String, password: String, request: CustomerRequest){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                    changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                    changeTextView(binding.emailError, R.string.exist, View.GONE)
                    changeTextView(binding.passwordError, R.string.exist, View.GONE)
                    changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
                    val user = mAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                    user?.let { getFirebaseIdToken(it, request) }
                } else {
                    onFailure(R.string.error_signup)
                    changeTextView(binding.emailError, R.string.exist, View.VISIBLE)
                    changeTextView(binding.passwordError, R.string.exist, View.VISIBLE)
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
                        mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                            if (it.isSuccessful){
                                binding.progressBar.visibility = View.GONE
                                val onAllow: () -> Unit = {
                                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                                }
                                com.example.fashionshop.View.showDialog(
                                    this@SignupActivity,
                                    R.string.auth,
                                    R.string.email_message,
                                    onAllow
                                )
                            }
                            else { onFailure(R.string.error_signup) }
                        }
                    }
                    is NetworkState.Failure ->{ onFailure(R.string.error_signup) }
                }
            }
        }
    }

    private fun validation(): Boolean {
        val fname = binding.firstName.text.toString()
        val lname = binding.lastName.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        var valid = false
        when {
            fname.isEmpty() -> {
                changeTextView(binding.firstNameError, R.string.empty, View.VISIBLE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            !fname.matches(nameRegex.toRegex()) -> {
                changeTextView(binding.firstNameError, R.string.error_name_format, View.VISIBLE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            lname.isEmpty() -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.empty, View.VISIBLE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            !lname.matches(nameRegex.toRegex()) -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.error_name_format, View.VISIBLE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            email.isEmpty() -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.empty, View.VISIBLE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            !email.matches(emailRegex.toRegex()) -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.error_email_format, View.VISIBLE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            password.isEmpty() -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.empty, View.VISIBLE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            !password.matches(passwordRegex.toRegex()) -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.error_password_format, View.VISIBLE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
            confirmPassword.isEmpty() -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.empty, View.VISIBLE)
            }
            password != confirmPassword -> {
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.error_not_match, View.VISIBLE)
            }
            else -> {
                valid = true
                changeTextView(binding.firstNameError, R.string.exist, View.GONE)
                changeTextView(binding.lastNameError, R.string.exist, View.GONE)
                changeTextView(binding.emailError, R.string.exist, View.GONE)
                changeTextView(binding.passwordError, R.string.exist, View.GONE)
                changeTextView(binding.confirmPasswordError, R.string.exist, View.GONE)
            }
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
                onFailure(R.string.error_signup)
            }
        }
    }

    private fun onFailure(messageId: Int){
        binding.progressBar.visibility = View.GONE
        binding.screen.visibility = View.VISIBLE
       // Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
        Snackbar.make(binding.root,  getString(messageId), Snackbar.LENGTH_SHORT).show()

    }

    private fun changeTextView(textView: TextView, textId: Int, visibility: Int){
        textView.text =  getString(textId)
        textView.visibility = visibility
    }
}
