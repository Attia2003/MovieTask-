package com.example.taskmovie.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmovie.database.MovieDataBase
import com.example.taskmovie.database.ResgisterEntity
import com.example.taskmovie.databinding.ActivityRegisterBinding
import com.example.taskmovie.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

 class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: MovieDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        database = Room.databaseBuilder(
            this,
            MovieDataBase::class.java,
            "movie_database"
        ).addMigrations(MIGRATION_4_5)
            .build()


        binding.createAccount.setOnClickListener {
            val username = binding.UserName.text.toString().trim()
            val email = binding.UserEmail.text.toString().trim()
            val password = binding.UserPassword.text.toString().trim()
            val confirmPassword = binding.UserPasswordConfirm.text.toString().trim()

            if (validateInput(username, email, password, confirmPassword)) {
                registerUser(username, email, password)
            }
        }

        binding.alreadyHaveAccount.setOnClickListener {
            Toast.makeText(this, "Redirecting to login...", Toast.LENGTH_SHORT).show()

        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (username.isEmpty()) {
            showToast("Username is required")
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter a valid email")
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            showToast("Password must be at least 6 characters")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }
        return true
    }

    private fun registerUser(username: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDao = database.moviedao()
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    runOnUiThread {
                        showToast("Email is already registered")
                    }
                    return@launch
                }
                val newUser = ResgisterEntity(username = username, email = email, password = password)
                userDao.insertUser(newUser)

                runOnUiThread {
                    showToast("User registered successfully!")
                    navigateToMainActivity()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("An error occurred: ${e.message}")
                }
            }
        }
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                username TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
        """)
        }
    }
}
