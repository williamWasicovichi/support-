package com.example.sup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class SupportItem(
    val id: String = "",
    val motive: String = "",
    val status: String = "",
    val userName: String = "",
    val company: String = ""
)

class SuporteMenu : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var supportItemsAdapter: SuporteAdapter
    private val supportItemList = mutableListOf<SupportItem>()
    private lateinit var db: FirebaseFirestore
    private lateinit var logoutBT: Button
    private lateinit var cabecarioIV: ImageView
    private lateinit var auth: FirebaseAuth
    private var ticketsListener: ListenerRegistration? = null

    private val TAG = "SuporteMenu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_suporte_menu)

        listView = findViewById(R.id.listView)
        logoutBT = findViewById(R.id.buttonLogout)
        cabecarioIV = findViewById(R.id.cabecario)
        db = Firebase.firestore
        auth = Firebase.auth

        supportItemsAdapter = SuporteAdapter(this, supportItemList,
            onOpenChatClicked = { selectedItem ->
                if (selectedItem.status.equals("Open", ignoreCase = true)) {
                    if (selectedItem.id.isNotEmpty()) {
                        val intent = Intent(this, Chat::class.java)
                        intent.putExtra("TICKET_ID", selectedItem.id)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "ID do chamado inválido.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Este chamado já foi fechado.", Toast.LENGTH_SHORT).show()
                }
            },
            onCloseTicketClicked = { ticketId ->
                closeTicket(ticketId)
            }
        )
        listView.adapter = supportItemsAdapter

        loadSupportItems()

        cabecarioIV.setOnClickListener {
            startActivity(Intent(this, Dados_da_conta::class.java))
        }

        logoutBT.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun closeTicket(ticketId: String) {
        if (ticketId.isNotEmpty()) {
            db.collection("supportTickets").document(ticketId)
                .update("status", "Fechado")
                .addOnSuccessListener {
                    Toast.makeText(this, "Chamado fechado com sucesso!", Toast.LENGTH_SHORT).show()
                    // A lista será atualizada automaticamente pelo listener, não precisa chamar loadSupportItems()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao fechar chamado: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error closing ticket", e)
                }
        } else {
            Toast.makeText(this, "ID do chamado inválido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSupportItems() {
        val query = db.collection("supportTickets")
            .orderBy("status", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.ASCENDING)

        ticketsListener = query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(this, "Erro ao carregar chamados: ${e.message}", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            supportItemList.clear()
            if (snapshots != null) {
                for (document in snapshots.documents) {
                    val item = document.toObject(SupportItem::class.java)?.copy(id = document.id)
                    if (item != null) {
                        supportItemList.add(item)
                    }
                }
            }
            supportItemsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Importante: Remover o listener para evitar vazamentos de memória e cobranças desnecessárias
        ticketsListener?.remove()
    }
}