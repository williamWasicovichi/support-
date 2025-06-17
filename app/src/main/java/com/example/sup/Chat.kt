package com.example.sup // Adjust to your package name

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Chat : AppCompatActivity() {

    private lateinit var recyclerViewChatMessages: RecyclerView
    private lateinit var editTextChatMessage: EditText
    private lateinit var buttonSendMessage: Button
    private lateinit var progressBarChat: ProgressBar

    private lateinit var chatAdapter: ChatAdapter
    private val messagesList = mutableListOf<ChatMessage>()

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    private var ticketId: String? = null
    // private var receivedUserId: String? = null // If you also get USER_ID from intent

    private var messagesListener: ListenerRegistration? = null

    companion object {
        private const val TAG = "ChatActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        ticketId = intent.getStringExtra("TICKET_ID")
        // receivedUserId = intent.getStringExtra("USER_ID") // Retrieve if passed

        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_LONG).show()
            finish() // Or redirect to login
            return
        }

        if (ticketId == null) {
            Toast.makeText(this, "ID do Ticket não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        recyclerViewChatMessages = findViewById(R.id.recyclerViewChatMessages)
        editTextChatMessage = findViewById(R.id.editTextChatMessage)
        buttonSendMessage = findViewById(R.id.buttonSendMessage)
        progressBarChat = findViewById(R.id.progressBarChat)

        // Apply window insets to the main layout
        val mainLayout = findViewById<View>(R.id.mainChatLayout) // Ensure this ID is on your root layout
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        loadMessages()

        buttonSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter() // Initialize adapter
        recyclerViewChatMessages.apply {
            layoutManager = LinearLayoutManager(this@Chat).apply {
                stackFromEnd = true // New messages appear at the bottom and view scrolls
            }
            adapter = chatAdapter
        }
    }

    private fun loadMessages() {
        setLoading(true)
        val messagesCollectionRef = db.collection("supportTickets")
            .document(ticketId!!) // ticketId is confirmed not null here
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING) // Order by timestamp

        messagesListener = messagesCollectionRef.addSnapshotListener { snapshots, e ->
            setLoading(false)
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                Toast.makeText(this, "Falha ao carregar mensagens: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val newMessages = mutableListOf<ChatMessage>()
            for (doc in snapshots!!) {
                val message = doc.toObject(ChatMessage::class.java).copy(id = doc.id)
                newMessages.add(message)
            }
            chatAdapter.submitList(newMessages)
            // Scroll to the bottom only if new messages were added or initially loading
            if (newMessages.isNotEmpty()) {
                recyclerViewChatMessages.scrollToPosition(newMessages.size - 1)
            }
            Log.d(TAG, "Mensagens carregadas/atualizadas: ${newMessages.size}")
        }
    }

    private fun sendMessage() {
        val messageText = editTextChatMessage.text.toString().trim()
        if (messageText.isEmpty()) {
            return
        }

        val senderId = currentUser!!.uid
        // Fetch senderName from your "users" collection in Firestore or use displayName
        val senderName = currentUser!!.displayName ?: currentUser!!.email ?: "Usuário"


        val message = ChatMessage(
            text = messageText,
            senderId = senderId,
            senderName = senderName
            // timestamp will be set by @ServerTimestamp
        )

        db.collection("supportTickets")
            .document(ticketId!!) // ticketId is confirmed not null
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d(TAG, "Mensagem enviada com sucesso!")
                editTextChatMessage.text.clear() // Clear input field
                // The listener will automatically update the RecyclerView
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao enviar mensagem", e)
                Toast.makeText(this, "Falha ao enviar mensagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setLoading(isLoading: Boolean) {
        progressBarChat.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        messagesListener?.remove() // Important: Remove Firestore listener to prevent memory leaks
    }
}