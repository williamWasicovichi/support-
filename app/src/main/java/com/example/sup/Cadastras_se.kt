package com.example.sup

import android.content.Intent // Add if you navigate to another activity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
// Import RadioGroup and RadioButton if you add them for user type
// import android.widget.RadioGroup
// import android.widget.RadioButton
import android.widget.ProgressBar // Assuming you'll add a ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
// Remove unused imports like Api, Task, AuthResult if not directly used
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Cadastras_se : AppCompatActivity() {
    // It's good practice to use ViewBinding or Kotlin Android Extensions (Synthetics - deprecated but you might see it)
    // For now, sticking to findViewById as per your original code, but note the order.
    private lateinit var emailCTV: EditText
    private lateinit var nomeET: EditText
    private lateinit var senhaCET: EditText
    private lateinit var conf_senhaCET: EditText
    private lateinit var Codigo_EmpresaET: EditText // Assuming this is for your 'suporte' type
    private lateinit var cadastra_sebt: Button
    private lateinit var progressBar: ProgressBar // Add a ProgressBar to your layout

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    // Define your own TAG for logging
    private val TAG = "Cadastras_se"

    // User types (as in your code)
    object UserType {
        const val CLIENTE = "cliente"
        const val SUPORTE = "suporte" // Changed from "Suporte" to "suporte" for consistency
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call super first
        enableEdgeToEdge()                 // Then enableEdgeToEdge
        setContentView(R.layout.activity_cadastras_se) // THEN set content view

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Views AFTER setContentView
        emailCTV = findViewById(R.id.emailCTV)
        nomeET = findViewById(R.id.nomeET)
        senhaCET = findViewById(R.id.senhaCET)
        conf_senhaCET = findViewById(R.id.conf_senhaCET)
        Codigo_EmpresaET = findViewById(R.id.Codigo_EmpresaET)
        cadastra_sebt = findViewById(R.id.cadastra_sebt)
        // Initialize ProgressBar (make sure you have <ProgressBar android:id="@+id/progressBar" ... /> in your XML)
        // progressBar = findViewById(R.id.progressBar) // Example ID

        cadastra_sebt.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val email = emailCTV.text.toString().trim()
        val name = nomeET.text.toString().trim()
        val password = senhaCET.text.toString().trim()
        val confirmPassword = conf_senhaCET.text.toString().trim()
        val codigoEmpresa = Codigo_EmpresaET.text.toString().trim() // For 'suporte' logic

        // --- Input Validations ---
        if (name.isEmpty()) {
            nomeET.error = "Nome é obrigatório"
            nomeET.requestFocus()
            return
        }
        if (email.isEmpty()) {
            emailCTV.error = "Email é obrigatório"
            emailCTV.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailCTV.error = "Insira um email válido"
            emailCTV.requestFocus()
            return
        }
        if (password.isEmpty()) {
            senhaCET.error = "Senha é obrigatória"
            senhaCET.requestFocus()
            return
        }
        if (password.length < 6) {
            senhaCET.error = "Senha deve ter no mínimo 6 caracteres"
            senhaCET.requestFocus()
            return
        }
        if (password != confirmPassword) {
            conf_senhaCET.error = "As senhas não coincidem"
            conf_senhaCET.requestFocus()
            return
        }

        // Determine User Type (This is a simplified example)
        // You'll need a more robust way to select this, e.g., RadioButtons or a Spinner
        val userType: String
        if (codigoEmpresa.isNotEmpty()) { // Example logic: if codigoEmpresa is filled, they are 'suporte'
            // TODO: You might want to validate the codigoEmpresa against a known list if it's for 'suporte'
            userType = UserType.SUPORTE
        } else {
            userType = UserType.CLIENTE
        }
        // --- End of Validations ---

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        saveAdditionalUserData(firebaseUser, name, userType, email) // Pass email too
                    } else {
                        setLoading(false)
                        Toast.makeText(baseContext, "Falha ao obter usuário após criação.", Toast.LENGTH_SHORT).show()
                        updateUI(null) // Or handle error appropriately
                    }
                } else {
                    setLoading(false)
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Falha na autenticação: ${task.exception?.message}",
                        Toast.LENGTH_LONG,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun saveAdditionalUserData(firebaseUser: FirebaseUser, name: String, userType: String, email: String) {
        val userId = firebaseUser.uid
        val userData = hashMapOf(
            "uid" to userId,
            "name" to name,
            "email" to email, // Storing email in Firestore as well
            "userType" to userType, // "cliente" or "suporte"
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "Dados do usuário salvos no Firestore!")
                // updateUI will be called from the createUserWithEmailAndPassword's success path
                // which now includes this Firestore save.
                updateUI(firebaseUser) // Call updateUI after everything is successful
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao salvar dados do usuário no Firestore", e)
                // Critical decision: User is created in Auth, but details not saved.
                // You might want to inform the user, or attempt to delete the Auth user (complex).
                Toast.makeText(
                    baseContext,
                    "Registro bem-sucedido, mas falha ao salvar detalhes: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                // Potentially sign the user out or try to delete the auth user if this part is critical
                // For now, we'll proceed as if the auth part was the main goal
                updateUI(firebaseUser) // Or updateUI(null) if this failure means the user can't proceed
            }
            .addOnCompleteListener {
                // This will be called regardless of success/failure of Firestore write,
                // after either addOnSuccessListener or addOnFailureListener.
                // We ensure setLoading(false) is called oncecreateUser completes and firestore completes.
                setLoading(false)
            }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Registration and data save successful
            Toast.makeText(this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
            // Example: Navigate to a main activity or login screen
            // val intent = Intent(this, MainActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // startActivity(intent)
            // finish()
        } else {
            // Registration failed or user data save failed critically
            // Toast for failure is already shown in the respective listeners.
            // You might want to re-enable input fields or clear them.
        }
    }

    private fun setLoading(isLoading: Boolean) {
        // Make sure progressBar is initialized
        // if (::progressBar.isInitialized) {
        //     progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        // }
        cadastra_sebt.isEnabled = !isLoading
        emailCTV.isEnabled = !isLoading
        nomeET.isEnabled = !isLoading
        senhaCET.isEnabled = !isLoading
        conf_senhaCET.isEnabled = !isLoading
        Codigo_EmpresaET.isEnabled = !isLoading
    }
}