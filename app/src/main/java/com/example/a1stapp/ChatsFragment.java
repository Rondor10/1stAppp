package com.example.a1stapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private List<String> usersList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getSender().equals(fuser.getUid())) {
                        if(!usersList.contains(chat.getReceiver())) {
                            usersList.add(chat.getReceiver());
                        }
                    }
                    if(chat.getReceiver().equals(fuser.getUid())) {
                        if(!usersList.contains(chat.getSender())) {
                            usersList.add(chat.getSender());
                        }
                    }
                }
                readChats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
    private void readChats() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    //display 1 user from chats
                    for (String id : usersList) {
                        if(user.getKey().equals(id)) {
                            if (mUsers.size() != 0) {
                                for(User user1 : mUsers) {
                                    if(!user.getKey().equals(user1.getKey())) {
                                        mUsers.add(user);
                                        break;
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
