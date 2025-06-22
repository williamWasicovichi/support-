package com.example.sup // Adjust to your package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Data class for the ticket
data class SupportTicket(
    var id: String? = null, // Firestore document ID, set after creation
    val motive: String = "",
    val company: String = "",
    val userId: String = "",
    val userName: String = "", // You might want to fetch this
    val userEmail: String = "", // Store user's email
    val status: String = "Open", // Default status
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)

class GeradorTicket : AppCompatActivity() {
    private lateinit var editTextMotive: EditText
    private lateinit var spinnerCompany: Spinner
    private lateinit var buttonCreateTicket: Button
    private lateinit var progressBarCreateTicket: ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedCompany: String? = null
    private val companiesList = mutableListOf<String>() // To hold company names

    companion object {
        private const val TAG = "GeradorTicket"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gerador_ticket)

        auth = Firebase.auth
        db = Firebase.firestore

        editTextMotive = findViewById(R.id.editTextMotive)
        spinnerCompany = findViewById(R.id.spinnerCompany)
        buttonCreateTicket = findViewById(R.id.buttonCreateTicket)
        progressBarCreateTicket = findViewById(R.id.progressBarCreateTicket)

        // Apply window insets
        val mainLayout = findViewById<View>(R.id.mainCreateTicketLayout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (auth.currentUser == null) {
            Toast.makeText(this, "Você precisa estar logado para criar um ticket.", Toast.LENGTH_LONG).show()
            // Optionally, navigate to login screen
            startActivity(Intent(this, MainActivity::class.java)) // Assuming MainActivity is your login
            finish()
            return
        }

        setupCompanySpinner()

        buttonCreateTicket.setOnClickListener {
            createAndSaveTicket()
        }
    }

    private fun setupCompanySpinner() {
        // Option 1: Hardcoded list (add a prompt as the first item)
        val staticCompanies = listOf("Selecione uma empresa", "Empresa Alpha", "Empresa Beta", "Tech Solutions Inc.")
        companiesList.addAll(staticCompanies)

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            companiesList
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCompany.adapter = spinnerAdapter

        spinnerCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Check if the selected item is the prompt
                selectedCompany = if (position == 0) {
                    null // No company selected if it's the prompt
                } else {
                    parent.getItemAtPosition(position).toString()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCompany = null
            }
        }

        // Option 2: Fetch from Firestore (more dynamic, commented out for simplicity here)
        /*
        db.collection("companies") // Assuming you have a "companies" collection
            .get()
            .addOnSuccessListener { documents ->
                companiesList.add("Selecione uma empresa") // Add prompt
                for (document in documents) {
                    document.getString("name")?.let { companiesList.add(it) }
                }
                spinnerAdapter.notifyDataSetChanged() // Update spinner if data is fetched after adapter setup
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting companies: ", exception)
                Toast.makeText(this, "Falha ao carregar lista de empresas.", Toast.LENGTH_SHORT).show()
            }
        */
    }

    private fun createAndSaveTicket() {
        val motive = editTextMotive.text.toString().trim()
        val currentUser = auth.currentUser ?: return // Should be checked in onCreate, but good practice

        // --- Validations ---
        if (motive.isEmpty()) {
            editTextMotive.error = "O motivo/problema é obrigatório."
            editTextMotive.requestFocus()
            return
        }

        if (selectedCompany == null) {
            Toast.makeText(this, "Por favor, selecione uma empresa.", Toast.LENGTH_SHORT).show()
            // Optionally, shake the spinner or set an error on its TextView label
            return
        }
        // --- End Validations ---

        setLoading(true)

        // It's good practice to fetch the user's name from your "users" collection
        // For simplicity here, we'll use display name if available, otherwise email
        val userId = currentUser.uid
        val userDisplayName = currentUser.displayName ?: "Usuário" // Fallback
        val userEmail = currentUser.email ?: "email.nao.disponivel@example.com"

        val newTicket = SupportTicket(
            motive = motive,
            company = selectedCompany!!, // We've validated it's not null
            userId = userId,
            userName = userDisplayName, // Consider fetching from your 'users' Firestore collection for accuracy
            userEmail = userEmail,
            status = "Open" // Initial status
            // createdAt is set by default in the data class
        )

        db.collection("supportTickets") // Name of your Firestore collection for tickets
            .add(newTicket)
            .addOnSuccessListener { documentReference ->
                val newTicketId = documentReference.id
                Log.d(TAG, "Ticket criado com sucesso. ID: $newTicketId")
                Toast.makeText(this, "Ticket criado com sucesso!", Toast.LENGTH_SHORT).show()
                setLoading(false)

                // Navigate to Chat Activity, passing the new ticket ID
                val intent = Intent(this, Chat::class.java) // Assuming Chat::class.java exists
                intent.putExtra("TICKET_ID", newTicketId)
                intent.putExtra("USER_ID", userId) // Pass user ID for chat context
                intent.putExtra("COMPANY_NAME", selectedCompany) // Optional: pass company name
                startActivity(intent)
                finish() // Finish this activity so user can't come back by pressing "back"
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Log.w(TAG, "Erro ao criar ticket", e)
                Toast.makeText(this, "Falha ao criar ticket: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBarCreateTicket.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonCreateTicket.isEnabled = !isLoading
        editTextMotive.isEnabled = !isLoading
        spinnerCompany.isEnabled = !isLoading
    }
}