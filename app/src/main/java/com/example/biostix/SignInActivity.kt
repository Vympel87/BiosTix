package com.example.biostix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.biostix.databinding.ActivitySignInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val firestore = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.keSignUp.setOnClickListener {
            val intentToLogin = Intent(this, SignUpActivity::class.java)
            startActivity(intentToLogin)
        }

        binding.lupaPw.setOnClickListener {
            val intentToLogin = Intent(this, ForgotPassword::class.java)
            startActivity(intentToLogin)
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

                            val userRef = uid?.let { firestore.collection("users").document(it) }

                            userRef?.get()?.addOnSuccessListener { documentSnapshot ->
                                val isAdmin = documentSnapshot.getBoolean("isAdmin")

                                if (isAdmin == true) {
                                    val intent = Intent(this, AdminActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
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
    }

    private fun checkAllField(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                binding.layoutEmail.error = "Required email"
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.layoutEmail.error = "Invalid email format"
                return false
            }
            password.isEmpty() -> {
                binding.layoutPassword.error = "Required password"
                return false
            }
            password.length <= 8 -> {
                binding.layoutPassword.error = "Password should be at least 8 characters"
                return false
            }
            else -> return true
        }
    }
}

