package rwu.it.sociallearn.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import rwu.it.sociallearn.Chat_Module.Chat;
import rwu.it.sociallearn.Chat_Module.ChatAdapter;
import rwu.it.sociallearn.Fragment.ChatActivity;
import rwu.it.sociallearn.R;

public class AllChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> chatList;
    private DatabaseReference chatRef;
    private TextView tvEmpty; // Show "No chats yet"

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_all_chat_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvEmpty = view.findViewById(R.id.tvEmptyChats); // Add TextView in XML

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), chatList, chat -> {
            // Open ChatActivity
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CHAT_ID, chat.getChatId());
            intent.putExtra(ChatActivity.EXTRA_CHAT_NAME, chat.getChatName());
            intent.putExtra(ChatActivity.EXTRA_CHAT_TYPE, chat.getChatType());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        loadChats();

        return view;
    }

    private void loadChats() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                if (!snapshot.exists()) {
                    // No chats exist, optionally create sample data
                    createSampleChats();
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat != null) chatList.add(chat);
                }

                adapter.notifyDataSetChanged();

                // Show empty text if no chats
                if (chatList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load chats: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSampleChats() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        String chatId1 = chatRef.push().getKey();
        String chatId2 = chatRef.push().getKey();

        if (chatId1 != null) {
            Chat chat1 = new Chat(chatId1, "Group A", "Welcome to Group A!", "group", "");
            chatRef.child(chatId1).setValue(chat1);
        }

        if (chatId2 != null) {
            Chat chat2 = new Chat(chatId2, "John Doe", "Hi there!", "personal", "");
            chatRef.child(chatId2).setValue(chat2);
        }
    }
}
