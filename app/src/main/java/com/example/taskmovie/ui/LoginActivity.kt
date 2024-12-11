package com.example.taskmovie.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.databinding.ActivityLoginBinding
import com.example.taskmovie.ui.register.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: MovieDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Room.databaseBuilder(
            this,
            MovieDataBase::class.java,
            "movie_database"
        ).build()

        binding.createAccount.setOnClickListener {
            val email = binding.UserEmailLogin.text.toString().trim()
            val password = binding.UserPasswordLogin.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                showToast("Please enter both email and password")
            }
        }

        binding.registerRedirect.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Debug", "Email: $email, Password: $password")
            val userDao = database.moviedao()
            val user = userDao.loginUser(email, password)
            if (user != null) {
                Log.d("Debug", "User found: ${user.username}")
                runOnUiThread {
                    showToast("Login successful!")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Log.d("Debuginvaild", "User not found")
                runOnUiThread {
                    showToast("Invalid email or password")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
