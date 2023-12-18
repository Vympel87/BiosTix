package com.example.biostix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.biostix.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.keSignIn.setOnClickListener {
            val intentToLogin = Intent(this, SignInActivity::class.java)
            startActivity(intentToLogin)
        }

        binding.btnSignUp.setOnClickListener {
            val username = binding.registerUsername.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()

            if (checkAllField()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid

                            val userMap = HashMap<String, Any>()
                            userMap["username"] = username

                            if (uid != null) {
                                val userRef = firestore.collection("users").document(uid)
                                userRef.set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Your account successfully added", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to add account, cause: ${e?.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
            }
        }
    }

    private fun checkAllField(): Boolean {
        val username = binding.registerUsername.text.toString()
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val confirmPassword = binding.registerConfirmPassword.text.toString()

        when {
            username.isEmpty() -> {
                binding.layoutUsername.error = "Required username"
                return false
            }
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
            confirmPassword.isEmpty() -> {
                binding.layoutConfirmPassword.error = "Fill confirmation password"
                return false
            }
            password != confirmPassword -> {
                binding.layoutConfirmPassword.error = "Passwords do not match"
                return false
            }
            else -> return true
        }
    }
}