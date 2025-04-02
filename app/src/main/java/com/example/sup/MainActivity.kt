package com.example.sup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var senha: EditText
    private lateinit var recuperar_senha: TextView
    private lateinit var cadastra_se: Button
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        usuario = findViewById(R.id.usuario)
        senha = findViewById(R.id.senha)
        recuperar_senha = findViewById(R.id.recuperar_senha)
        cadastra_se = findViewById(R.id.cadastra_se)
        login = findViewById(R.id.login)
        
    }
}