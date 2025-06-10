package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns // For email validation
import android.view.View // For ProgressBar visibility
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar // Add to your layout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth // KTX extension
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var usuarioET: EditText // Renamed for clarity (ET for EditText)
    private lateinit var senhaET: EditText   // Renamed for clarity
    private lateinit var recuperarSenhaTV: TextView // Renamed for clarity (TV for TextView)
    private lateinit var cadastraseBT: Button   // Renamed for clarity (BT for Button)
    private lateinit var loginBT: Button        // Renamed for clarity
    private lateinit var progressBarLogin: ProgressBar // Add to your activity_main.xml

    private lateinit var auth: FirebaseAuth

    // Define your own TAG for logging
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Views
        usuarioET = findViewById(R.id.usuario)
        senhaET = findViewById(R.id.senha)
        recuperarSenhaTV = findViewById(R.id.recuperar_senha)
        cadastraseBT = findViewById(R.id.cadastra_se) // Ensure this ID matches your XML
        loginBT = findViewById(R.id.login)           // Ensure this ID matches your XML
        // progressBarLogin = findViewById(R.id.progressBarLogin) // Example ID, add to XML

        // Check if a user is already signed in (optional, but good for UX)
        // if (auth.currentUser != null) {
        //     Log.d(TAG, "User already signed in: ${auth.currentUser?.uid}")
        //     navigateToHomeScreen() // Or whatever your main app screen is
        // }

        loginBT.setOnClickListener {
            performLogin()
        }

        cadastraseBT.setOnClickListener {
            startActivity(Intent(this, Cadastras_se::class.java))
        }

        recuperarSenhaTV.setOnClickListener {
            // TODO: Implement password recovery functionality
            // Example: startActivity(Intent(this, ForgotPasswordActivity::class.java))
            Toast.makeText(this, "Forgot Password Clicked - Implement me!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin() {
        val email = usuarioET.text.toString().trim()
        val password = senhaET.text.toString().trim()

        // --- Input Validations ---
        if (email.isEmpty()) {
            usuarioET.error = "Email é obrigatório"
            usuarioET.requestFocus()
            return
        }
        // Optional: More robust email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usuarioET.error = "Insira um email válido"
            usuarioET.requestFocus()
            return
        }
        if (password.isEmpty()) {
            senhaET.error = "Senha é obrigatória"
            senhaET.requestFocus()
            return
        }
        // --- End of Validations ---

        setLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoading(false) // Hide progress bar regardless of outcome
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user) // Or directly navigate
                    navigateToHomeScreen() // Example navigation
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Falha na autenticação: ${task.exception?.message}", // More specific error
                        Toast.LENGTH_LONG, // Show longer for errors
                    ).show()
                    updateUI(null)
                }
            }
        // Removed the empty if() {} block
    }

    private fun updateUI(user: FirebaseUser?) {
        // This function can be used to update parts of the UI if staying on this screen,
        // but often for login, you navigate away on success.
        if (user != null) {
            // User is signed in.
            // UI updates for this screen are probably minimal if you navigate away.
            Log.i(TAG, "UpdateUI: User ${user.uid} is signed in.")
        } else {
            // User is signed out.
            Log.i(TAG, "UpdateUI: User is signed out.")
        }
    }

    private fun navigateToHomeScreen() {
        // Replace HomeScreenActivity::class.java with your actual main authenticated activity
        // val intent = Intent(this, HomeScreenActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        // startActivity(intent)
        // finish() // Call finish to remove LoginActivity from the back stack

        Toast.makeText(this, "Login Successful! (Implement Navigation)", Toast.LENGTH_LONG).show() // Placeholder
    }

    private fun setLoading(isLoading: Boolean) {
        // Make sure progressBarLogin is initialized if you use it
        // if (::progressBarLogin.isInitialized) {
        // progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        // }
        loginBT.isEnabled = !isLoading
        cadastraseBT.isEnabled = !isLoading // Also disable register button during login attempt
        usuarioET.isEnabled = !isLoading
        senhaET.isEnabled = !isLoading
    }

    // Optional: Add onStart to check if user is already signed in
    // override fun onStart() {
    //     super.onStart()
    //     // Check if user is signed in (non-null) and update UI accordingly.
    //     val currentUser = auth.currentUser
    //     if (currentUser != null) {
    //         // User is already signed in, perhaps navigate to home screen
    //         Log.d(TAG, "onStart: User ${currentUser.uid} already logged in.")
    //         navigateToHomeScreen()
    //     }
    // }
}