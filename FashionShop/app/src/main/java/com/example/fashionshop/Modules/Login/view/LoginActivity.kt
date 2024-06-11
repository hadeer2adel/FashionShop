package com.example.fashionshop.Modules.Login.view

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.fashionshop.MainActivity
import com.example.fashionshop.Modules.Login.viewModel.LoginViewModel
import com.example.fashionshop.Modules.Login.viewModel.LoginViewModelFactory
import com.example.fashionshop.Modules.Signup.view.SignupActivity
import com.example.fashionshop.Modules.Signup.viewModel.SignupViewModel
import com.example.fashionshop.Modules.Signup.viewModel.SignupViewModelFactory
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel

    companion object {
        private const val REQ_ONE_TAP = 5
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        initViewModel()

        binding.signupBtn1.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.signupBtn2.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (validation()) {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.screen.visibility = View.GONE
                                viewModel.getCustomerByEmail(email)
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                            binding.emailTxt.error = "Wrong email"
                            binding.passwordTxt.error = "Wrong password"
                        }
                    }
            }
        }

        binding.googleBtn.setOnClickListener {
            Log.i("TAG", "onCreate: ")
            googleLogin()
        }
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)

        val factory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        lifecycleScope.launch {
            viewModel.customer.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {}
                    is NetworkState.Success ->{
                        viewModel.saveCustomerData(this@LoginActivity, response.data.customers.first())
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    is NetworkState.Failure ->{
                        Toast.makeText(this@LoginActivity, response.error.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {
        var valid = false
        when {
            binding.email.text.toString().isEmpty() -> binding.emailTxt.error = "Email is empty"
            binding.password.text.toString().isEmpty() -> binding.passwordTxt.error = "Password is empty"
            else -> valid = true
        }
        return valid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ONE_TAP && resultCode == RESULT_OK) {
            handleGoogleResult(data)
        }
    }

    private fun handleGoogleResult(data: Intent?) {
        try {
            val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.screen.visibility = View.GONE
                                viewModel.getCustomerByEmail(user.email!!)
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } catch (e: ApiException) {
            Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun googleLogin() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

        Identity.getSignInClient(this).beginSignIn(signInRequest)
            .addOnSuccessListener(this) { signInResult ->
                try {
                    startIntentSenderForResult(signInResult.pendingIntent.intentSender, REQ_ONE_TAP, null, 0, 0, 0)
                } catch (e: IntentSender.SendIntentException) {
                    Toast.makeText(this@LoginActivity, "Failed to start Request", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
