package com.example.sup

import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
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

    private val TAG = "SuporteMenu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_suporte_menu)

        listView = findViewById(R.id.listView)
        db = Firebase.firestore

        supportItemsAdapter = SuporteAdapter(this, supportItemList) { ticketId ->
            closeTicket(ticketId)
        }
        listView.adapter = supportItemsAdapter

        loadSupportItems()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = supportItemList[position]

            if (selectedItem.id.isNotEmpty()) {
                Toast.makeText(this, "Abrindo chat para: ${selectedItem.motive}", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Chat::class.java)
                intent.putExtra("TICKET_ID", selectedItem.id)
                startActivity(intent)

            } else {
                Toast.makeText(this, "ID do ticket inválido.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Tentativa de abrir chat com ID de ticket vazio na posição: $position")
            }
        }
    }

    private fun closeTicket(ticketId: String) {
        if (ticketId.isNotEmpty()) {
            db.collection("supportTickets").document(ticketId)
                .update("status", "Fechado")
                .addOnSuccessListener {
                    Toast.makeText(this, "Chamado fechado com sucesso!", Toast.LENGTH_SHORT).show()
                    loadSupportItems()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao fechar chamado: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error closing ticket", e)
                }
        } else {
            Toast.makeText(this, "ID do ticket inválido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSupportItems() {
        db.collection("supportTickets")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No support items found.")
                    Toast.makeText(this, "No support items available.", Toast.LENGTH_SHORT).show()
                } else {
                    supportItemList.clear()
                    for (document in documents) {
                        val item = document.toObject(SupportItem::class.java).copy(id = document.id)
                        supportItemList.add(item)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                    supportItemsAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading items: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}