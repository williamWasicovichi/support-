package com.example.sup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dados_da_conta : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var nomeET: EditText
    private lateinit var mudarSenha: TextView
    private lateinit var cancelarConta: TextView
    private lateinit var Salvar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        usuario = findViewById(R.id.usuario)
        nomeET = findViewById(R.id.nomeET)
        mudarSenha = findViewById(R.id.mudarSenha)
        cancelarConta = findViewById(R.id.cancelarConta)
        Salvar = findViewById(R.id.Salvar)
        Salvar.setOnClickListener{

        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dados_da_conta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}