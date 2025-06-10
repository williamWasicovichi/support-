package com.example.sup

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView // For OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query // For ordering, if needed
import com.google.firebase.firestore.ktx.firestore // KTX
import com.google.firebase.ktx.Firebase

// Assuming you have a data class for your support items
data class SupportItem(
    val id: String = "", // Document ID from Firestore
    val title: String = "",
    val status: String = "",
    val description: String = ""
    // Add other relevant fields: createdDate, assignedTo, etc.
)

class SuporteMenu : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var supportItemsAdapter: SuporteAdapter // Use your adapter type
    private val supportItemList = mutableListOf<SupportItem>()
    private lateinit var db: FirebaseFirestore

    private val TAG = "SuporteMenu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_suporte_menu)

        listView = findViewById(R.id.listView)
        db = Firebase.firestore

        // Initialize the adapter with an empty list first
        supportItemsAdapter = SuporteAdapter(this, supportItemList) // Pass the list
        listView.adapter = supportItemsAdapter

        loadSupportItems()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = supportItemList[position] // Get the clicked item
            Toast.makeText(this, "Clicked: ${selectedItem.title}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to a detail screen, passing selectedItem.id or the whole object
            // val intent = Intent(this, SuporteDetailActivity::class.java)
            // intent.putExtra("ITEM_ID", selectedItem.id)
            // startActivity(intent)
        }
    }

    private fun loadSupportItems() {
        // Example: Fetching items from a "supportTickets" collection in Firestore
        // You might want to filter these for the current support user if applicable
        db.collection("supportTickets")
            // .whereEqualTo("status", "open") // Example filter
            // .orderBy("createdAt", Query.Direction.DESCENDING) // Example ordering
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d(TAG, "No support items found.")
                    Toast.makeText(this, "No support items available.", Toast.LENGTH_SHORT).show()
                    // Handle empty state - maybe show a message in the UI
                } else {
                    supportItemList.clear() // Clear previous items
                    for (document in documents) {
                        val item = document.toObject(SupportItem::class.java).copy(id = document.id)
                        supportItemList.add(item)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                    supportItemsAdapter.notifyDataSetChanged() // Refresh the ListView
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading items: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}