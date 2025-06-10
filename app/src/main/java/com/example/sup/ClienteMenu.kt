package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClienteMenu : AppCompatActivity() {
    private lateinit var ticket: EditText
    private lateinit var SolicitarA: Button
    private lateinit var AcessarC: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        ticket = findViewById(R.id.ticket)
        SolicitarA = findViewById(R.id.SolicitarA)
        AcessarC = findViewById(R.id.AcessarC)
        SolicitarA.setOnClickListener {
            startActivity(Intent(this,GeradorTicket::class.java) )
        }
        AcessarC.setOnClickListener {
            startActivity(Intent(this,Chat::class.java) )
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}