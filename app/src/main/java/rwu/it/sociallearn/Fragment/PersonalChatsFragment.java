package rwu.it.sociallearn.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class PersonalChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> personalChatList;
    private DatabaseReference chatRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_chats, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPersonalChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        personalChatList = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), personalChatList, chat -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CHAT_ID, chat.getChatId());
            intent.putExtra(ChatActivity.EXTRA_CHAT_NAME, chat.getChatName());
            intent.putExtra(ChatActivity.EXTRA_CHAT_TYPE, chat.getChatType());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        loadPersonalChats();

        return view;
    }

    private void loadPersonalChats() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                personalChatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat != null && "personal".equals(chat.getChatType())) {
                        personalChatList.add(chat);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}

