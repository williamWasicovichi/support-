package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth // Import if checking auth status
import com.google.firebase.auth.ktx.auth    // Import if checking auth status
import com.google.firebase.ktx.Firebase      // Import if checking auth status

class ClienteMenu : AppCompatActivity() {
    private lateinit var ticketET: EditText // Renamed for clarity (ET for EditText)
    private lateinit var solicitarAtendimentoBT: Button // Renamed
    private lateinit var acessarChatBT: Button          // Renamed

    // Optional: Firebase Auth for checking login status
    // private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call super.onCreate first
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente_menu) // Then set content view

        // Initialize views AFTER setContentView
        ticketET = findViewById(R.id.ticket) // Ensure ID matches your XML
        solicitarAtendimentoBT = findViewById(R.id.SolicitarA) // Ensure ID matches your XML
        acessarChatBT = findViewById(R.id.AcessarC)       // Ensure ID matches your XML

        // Optional: Initialize Firebase Auth
        // auth = Firebase.auth
        // if (auth.currentUser == null) {
        //     // User not logged in, redirect to login
        //     startActivity(Intent(this, MainActivity::class.java))
        //     finish()
        //     return
        // }

        // Apply window insets to the main layout
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main) // Ensure R.id.main is your root layout ID
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        solicitarAtendimentoBT.setOnClickListener {
            // Navigate to the activity used to create/generate tickets
            // This should be CreateTicketActivity::class.java or GeradorTicket::class.java
            startActivity(Intent(this, CreateTicketActivity::class.java))
        }

        acessarChatBT.setOnClickListener {
            val ticketId = ticketET.text.toString().trim()

            if (ticketId.isEmpty()) {
                ticketET.error = "Por favor, insira o ID do ticket."
                ticketET.requestFocus()
                Toast.makeText(this, "Por favor, insira o ID do ticket para acessar o chat.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Navigate to Chat activity, passing the ticket ID
            val intent = Intent(this, Chat::class.java)
            intent.putExtra("TICKET_ID", ticketId)
            // You might also need to pass the current USER_ID if your Chat activity needs it
            // and it's not directly obtainable from just a TICKET_ID
            // val currentUserId = auth.currentUser?.uid
            // if (currentUserId != null) {
            //    intent.putExtra("USER_ID", currentUserId)
            // } else {
            //    Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_SHORT).show()
            //    return@setOnClickListener
            // }
            startActivity(intent)
        }
    }
}