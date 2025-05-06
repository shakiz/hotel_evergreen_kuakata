package com.hotel.evergreenkuakata.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.databinding.ActivityLoginBinding
import com.hotel.evergreenkuakata.presentation.onboard.HomeActivity
import com.hotel.evergreenkuakata.utils.Constants
import com.hotel.evergreenkuakata.utils.PrefManager
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.UX
import com.hotel.evergreenkuakata.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private lateinit var activityBinding: ActivityLoginBinding
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var validation = Validation(this, hashMap)
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var ux: UX

    @Inject
    lateinit var prefManager: PrefManager

    @Inject
    lateinit var tools: Tools

    override val layoutResourceId: Int
        get() = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        bindUiWithComponents()
        initListeners()
        initObservers()
    }

    override fun setVariables(dataBinding: ActivityLoginBinding) {
        activityBinding = dataBinding
    }

    private fun init() {
        ux = UX(this)
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("email", "password"),
            arrayOf(getString(R.string.email_validation), getString(R.string.password_validation))
        )
    }

    private fun initListeners() {
        activityBinding.btnLogin.setOnClickListener {
            if (validation.isValid) {
                val email = activityBinding.email.text.toString()
                val password = activityBinding.password.text.toString()
                viewModel.login(email, password)
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> ux.getLoadingView()
                    is AuthState.Success -> {
                        prefManager[Constants.mIsLoggedIn] = true
                        ux.removeLoadingView()
                        Toast.makeText(
                            this@LoginActivity,
                            "Logged in as ${state.user?.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    }

                    is AuthState.Error -> {
                        prefManager[Constants.mIsLoggedIn] = false
                        ux.removeLoadingView()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> Unit
                }
            }
        }
    }
}