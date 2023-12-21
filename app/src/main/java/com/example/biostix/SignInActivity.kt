package com.example.biostix

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.biostix.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("login_pref", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            navigateToHome()
            finish()
        }

        binding.lupaPw.setOnClickListener {
            val intentToSignUp = Intent(this, ForgotPassword::class.java)
            startActivity(intentToSignUp)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.inEmail.text.toString()
            val password = binding.inPassword.text.toString()

            if (checkAllField(email, password)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val uid = currentUser?.uid

                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.apply()

                            val userRef = uid?.let { uid -> firestore.collection("users").document(uid) }

                            userRef?.get()?.addOnSuccessListener { documentSnapshot ->
                                val isAdmin = documentSnapshot.getBoolean("isAdmin")

                                if (isAdmin == true) {
                                    val intent = Intent(this, AdminActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    navigateToHome()
                                }
                            }?.addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to get user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to sign in: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.keSignUp.setOnClickListener {
            val intentToSignUp = Intent(this, SignUpActivity::class.java)
            startActivity(intentToSignUp)
        }
    }

    private fun checkAllField(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.layoutEmail.error = "Required email"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.layoutEmail.error = "Invalid email format"
                false
            }
            password.isEmpty() -> {
                binding.layoutPassword.error = "Required password"
                false
            }
            password.length <= 8 -> {
                binding.layoutPassword.error = "Password should be at least 8 characters"
                false
            }
            else -> true
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
