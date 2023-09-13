package com.cihancelik.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    EditText messageText;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private ArrayList<String> chatMessages = new ArrayList<>();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.options_menu_signOut) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.options_menu_profile) {
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        messageText = findViewById(R.id.messageEditText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(chatMessages);

        RecyclerView.LayoutManager recyclerViewManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerview ve adapteri bagliyoruz
        recyclerView.setAdapter(recyclerViewAdapter);

        mAuth = FirebaseAuth.getInstance();
        // veri tabani
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

    }
    public void sendMessage (View view){

        String messageToSend = messageText.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();
        int nick = user.getEmail().indexOf("@");
        String userNickName =user.getEmail().substring(0,nick).toString();

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        databaseReference.child("Chats").child(uuidString).child("usermessage").setValue(messageToSend);
        databaseReference.child("Chats").child(uuidString).child("userNickName").setValue(userNickName);

        messageText.setText("");

      //  databaseReference.child("Chats").child("Chat 2").child("Test Chat").child("Text 1").setValue(messageToSend);

    }
}