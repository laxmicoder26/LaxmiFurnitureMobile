package com.furnitureshop.mobile.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.furnitureshop.mobile.R
import com.furnitureshop.mobile.data.AppDatabase
import com.furnitureshop.mobile.data.entity.UserEntity
import com.furnitureshop.mobile.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private var current: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val tvAvatar = findViewById<TextView>(R.id.tvAvatar)
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val email = SessionManager.email(this)
        if (email == null) {
            finish()
            return
        }

        lifecycleScope.launch {
            val user = AppDatabase.get(applicationContext).userDao().findByEmail(email)
            if (user == null) {
                finish()
                return@launch
            }
            current = user
            etName.setText(user.name)
            etEmail.setText(user.email)
            tvAvatar.text = user.name.take(1).uppercase()
        }

        findViewById<TextView>(R.id.btnSave).setOnClickListener {
            val user = current ?: return@setOnClickListener
            val newName = etName.text.toString().trim()
            val newPass = etPassword.text.toString()
            if (newName.isEmpty()) {
                Toast.makeText(this, "Naam khaali nahi ho sakta.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val updated = user.copy(
                name = newName,
                password = if (newPass.isEmpty()) user.password else newPass
            )
            lifecycleScope.launch {
                AppDatabase.get(applicationContext).userDao().update(updated)
                SessionManager.updateName(applicationContext, newName)
                Toast.makeText(this@EditProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
