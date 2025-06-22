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
    private lateinit var ticketET: EditText
    private lateinit var solicitarAtendimentoBT: Button
    private lateinit var acessarChatBT: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente_menu)

        ticketET = findViewById(R.id.ticket)
        solicitarAtendimentoBT = findViewById(R.id.SolicitarA)
        acessarChatBT = findViewById(R.id.AcessarC)

        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        solicitarAtendimentoBT.setOnClickListener {
            // CORREÇÃO: Navegando para GeradorTicket, que contém a CreateTicketActivity
            startActivity(Intent(this,GeradorTicket::class.java))
        }

        acessarChatBT.setOnClickListener {
            val ticketId = ticketET.text.toString().trim()

            if (ticketId.isEmpty()) {
                ticketET.error = "Por favor, insira o ID do ticket."
                ticketET.requestFocus()
                Toast.makeText(this, "Por favor, insira o ID do ticket para acessar o chat.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intent = Intent(this, Chat::class.java)
            intent.putExtra("TICKET_ID", ticketId)
            startActivity(intent)
        }
    }
}

