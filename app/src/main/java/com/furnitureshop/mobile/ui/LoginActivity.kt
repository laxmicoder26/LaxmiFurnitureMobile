package com.furnitureshop.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.UserEntity
import com.furnitureshop.mobile.util.DefaultProducts
import com.furnitureshop.mobile.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var isLoginMode = true

    private lateinit var etNameLayout: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tvError: TextView
    private lateinit var cbHost: CheckBox
    private lateinit var btnSubmit: TextView
    private lateinit var btnTabLogin: TextView
    private lateinit var btnTabSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // First run: seed default products + admin account into the Room database.
        lifecycleScope.launch {
            val db = AppDatabase.get(applicationContext)
            if (db.productDao().count() == 0) {
                db.productDao().insertAll(DefaultProducts.list())
            }
            if (db.userDao().findByEmail("admin@laxmi.com") == null) {
                db.userDao().insert(UserEntity("admin@laxmi.com", "Admin", "admin123", true))
            }
        }

        if (SessionManager.isLoggedIn(this)) {
            goHome()
            return
        }

        setContentView(R.layout.activity_login)
        etNameLayout = findViewById(R.id.etNameLayout)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        cbHost = findViewById(R.id.cbHost)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnTabLogin = findViewById(R.id.btnTabLogin)
        btnTabSignup = findViewById(R.id.btnTabSignup)

        updateMode()
        btnTabLogin.setOnClickListener { isLoginMode = true; updateMode() }
        btnTabSignup.setOnClickListener { isLoginMode = false; updateMode() }
        btnSubmit.setOnClickListener { if (isLoginMode) doLogin() else doSignup() }
        findViewById<TextView>(R.id.tvGuest).setOnClickListener { goHome() }
    }

    private fun updateMode() {
        etNameLayout.visibility = if (isLoginMode) View.GONE else View.VISIBLE
        cbHost.visibility = if (isLoginMode) View.GONE else View.VISIBLE
        btnSubmit.text = if (isLoginMode) "Login" else "Create Account"
        tvError.text = ""
        styleTab(btnTabLogin, isLoginMode)
        styleTab(btnTabSignup, !isLoginMode)
    }

    private fun styleTab(tab: TextView, selected: Boolean) {
        tab.setBackgroundResource(if (selected) R.drawable.bg_chip_selected else android.R.color.transparent)
        tab.setTextColor(getColor(if (selected) R.color.white else R.color.text_dark))
    }

    private fun doLogin() {
        val email = etEmail.text.toString().trim().lowercase()
        val pass = etPassword.text.toString()
        if (email.isEmpty() || pass.isEmpty()) {
            tvError.text = "Email aur password bharein."
            return
        }
        lifecycleScope.launch {
            val user = AppDatabase.get(applicationContext).userDao().findByEmail(email)
            if (user != null && user.password == pass) {
                SessionManager.login(applicationContext, user.email, user.name, user.isAdmin, user.isHost)
                goHome()
            } else {
                tvError.text = "Invalid email ya password."
            }
        }
    }

    private fun doSignup() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim().lowercase()
        val pass = etPassword.text.toString()
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            tvError.text = "Sabhi fields bharein."
            return
        }
        lifecycleScope.launch {
            val db = AppDatabase.get(applicationContext)
            if (db.userDao().findByEmail(email) != null) {
                tvError.text = "Account pehle se hai. Login karein."
                return@launch
            }
            val asHost = cbHost.isChecked
            db.userDao().insert(UserEntity(email, name, pass, false, asHost))
            SessionManager.login(applicationContext, email, name, false, asHost)
            goHome()
        }
    }

    private fun goHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
