package rwu.it.sociallearn.Fragment;


import android.os.Bundle;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import rwu.it.sociallearn.Chat_Module.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import rwu.it.sociallearn.Chat_Module.MessageAdapter;
import rwu.it.sociallearn.R;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_ID = "chatId";
    public static final String EXTRA_CHAT_NAME = "chatName";
    public static final String EXTRA_CHAT_TYPE = "chatType";

    private TextView tvTitle;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageView btnSend;

    private List<Message> messageList;
    private MessageAdapter adapter;
    private DatabaseReference messagesRef;
    private String chatId;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvTitle = findViewById(R.id.tvChatTitle);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        String chatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        tvTitle.setText(chatName);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(this, messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(chatId);

        loadMessages();

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) sendMessage(text);
        });
    }

    private void loadMessages() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                if (msg != null) {
                    messageList.add(msg);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override public void onChildChanged(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(DataSnapshot snapshot) {}
            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private void sendMessage(String text) {
        long timestamp = System.currentTimeMillis();
        String messageId = messagesRef.push().getKey();

        if (messageId != null) {
            Message msg = new Message(currentUserId, text, timestamp);
            messagesRef.child(messageId).setValue(msg);
            etMessage.setText("");
        }
    }
}
