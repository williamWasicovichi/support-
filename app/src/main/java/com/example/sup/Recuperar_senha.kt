package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Recuperar_senha : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var senha: EditText
    private lateinit var confirmar_senha: EditText
    private lateinit var Salvar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)
        email = findViewById(R.id.emailETC)
        senha = findViewById(R.id.senha)
        confirmar_senha = findViewById(R.id.senhaCET)
        Salvar = findViewById(R.id.salvar)
        Salvar.setOnClickListener {
           // if (){
              //   startActivity(Intent(this, ClienteMenu::class.java))
                }
            //else if (){
              //  startActivity(Intent(this,SuporteMenu::class.java) )
            }
            //else{
              //  Toast.makeText(this,"E-mail ou senha incorretos.",Toast.LENGTH_LONG).show()
            }

       // }
   // }
//}

