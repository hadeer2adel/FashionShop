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
import com.example.fashionshop.Model.CustomerData
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModel
import com.example.fashionshop.Modules.Authentication.viewModel.AuthenticationViewModelFactory
import com.example.fashionshop.R
import com.example.fashionshop.Repository.Repository
import com.example.fashionshop.Repository.RepositoryImp
import com.example.fashionshop.Service.Networking.NetworkManager
import com.example.fashionshop.Service.Networking.NetworkManagerImp
import com.example.fashionshop.Service.Networking.NetworkState
import com.example.fashionshop.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var customerRequest: CustomerRequest


    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(CustomerData.getInstance(this).isLogged){
            startActivity(Intent(this, MainActivity::class.java))}

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
            googleLogin()
        }
    }

    private fun initViewModel(){
        val networkManager: NetworkManager = NetworkManagerImp.getInstance()
        val repository: Repository = RepositoryImp(networkManager)
        val factory = AuthenticationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AuthenticationViewModel::class.java)

        lifecycleScope.launch {
            viewModel.customers.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {}
                    is NetworkState.Success ->{
                        if(response.data.customers == null || response.data.customers.isEmpty() || response.data.customers.size < 1){
                            viewModel.createCustomer(customerRequest)
                        }
                        else {
                            val customer = response.data.customers?.first()
                            if (customer!!.note == 0L || customer.multipass_identifier == 0L) {
                                viewModel.updateCustomer(customer.id)
                            } else {
                                viewModel.saveCustomerData(this@LoginActivity, customer)
                                binding.progressBar.visibility = View.GONE
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            }
                        }
                    }
                    is NetworkState.Failure ->{
                        Toast.makeText(this@LoginActivity, response.error.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.i("TAG", response.error.message.toString())
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.customer.collectLatest { response ->
                when(response){
                    is NetworkState.Loading -> {}
                    is NetworkState.Success ->{
                        viewModel.saveCustomerData(this@LoginActivity, response.data.customer!!)
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    is NetworkState.Failure ->{
                        binding.progressBar.visibility = View.GONE
                        binding.screen.visibility = View.VISIBLE
                        Toast.makeText(this@LoginActivity, response.error.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.i("TAG", "Failure: "+ response.error.message.toString())
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

    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account.idToken!!)
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.let {
                        customerRequest = CustomerRequest(CustomerRequest.Customer(
                            user.displayName,
                            "",
                            user.email
                        ))
                        binding.progressBar.visibility = View.VISIBLE
                        binding.screen.visibility = View.GONE
                        viewModel.getCustomerByEmail(user.email!!)
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}