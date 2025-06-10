package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GeradorTicket : AppCompatActivity() {
    private lateinit var eVMotivo: EditText
    private lateinit var spinner_empresa: Spinner
    private lateinit var gerar_ticket: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gerador_ticket)
        eVMotivo = findViewById(R.id.eVMotivo)
        spinner_empresa = findViewById(R.id.spinner_empresa)
        gerar_ticket = findViewById(R.id.gerar_ticket)
        gerar_ticket.setOnClickListener {
            startActivity(Intent(this,Chat::class.java))
        }

    }
}