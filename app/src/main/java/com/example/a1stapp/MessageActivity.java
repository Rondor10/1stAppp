package com.example.a1stapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private ImageButton btn_send;
    private EditText text_send;
    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;
    private TextView username;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolBar);
        //setSupportActionBar(toolbar);  ----> ERROR!! FIX IT!!
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        intent = getIntent();
        final String userId = intent.getStringExtra("userId");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals(""))
                    sendMessage(fuser.getUid(), userId, msg);
                else
                    Toast.makeText(MessageActivity.this, "You Can't Send An Empty Message", Toast.LENGTH_SHORT).show();
                text_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getFirstName() + " " + user.getLastName());
                if(user.getImageURL().equals("default"))
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                else
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                  readMessage(fuser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("chats").push().setValue(hashMap);
    }

    private void readMessage(final String myId, final String userId, final String imageUrl) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  mChat.clear();
                  for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                      Chat chat = snapshot.getValue(Chat.class);
                      if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId))
                          mChat.add(chat);
                      messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageUrl);
                      recyclerView.setAdapter(messageAdapter);
                  }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
