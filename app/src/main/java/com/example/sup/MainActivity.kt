package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var senha: EditText
    private lateinit var recuperar_senha: TextView
    private lateinit var cadastra_se: Button
    private lateinit var login: Button
    var emailCliente = "emailCliente"
    var senhaCliente = "senhaCliente"
    var emailSuporte = "emailSuporte"
    var senhaSuporte = "senhaSuporte"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        usuario = findViewById(R.id.usuario)
        senha = findViewById(R.id.senha)
        recuperar_senha = findViewById(R.id.recuperar_senha)
        cadastra_se = findViewById(R.id.cadastra_se)
        login = findViewById(R.id.login)
        login.setOnClickListener{
            if (usuario.text.toString() == emailCliente && senha.text.toString() == senhaCliente){
                startActivity(Intent(this,GeradorTicket::class.java) )
            }
            else if (usuario.text.toString() == emailSuporte && senha.text.toString() == senhaSuporte){
                startActivity(Intent(this,SuporteMenu::class.java) )
            }
            else{
                Toast.makeText(this,"falha",Toast.LENGTH_SHORT).show()
            }

        }
        cadastra_se.setOnClickListener{
            startActivity(Intent(this,cadastra_se::class.java) )
        }
    }
}