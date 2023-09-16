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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    EditText messageText;
    EditText nickName;
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
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.options_menu_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
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

        getData();

    }

    public void sendMessage(View view) {

        String messageToSend = messageText.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();
        int nick = user.getEmail().indexOf("@");
        String userNickName = user.getEmail().substring(0, nick).toString();

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        //  databaseReference.child("Chats").child("Chat 2").child("Test Chat").child("Text 1").setValue(messageToSend);

        databaseReference.child("Chats").child(uuidString).child("userMessage").setValue(messageToSend);
        if (nickName == null){
            databaseReference.child("Chats").child(uuidString).child("userNickName").setValue(userNickName);
        }else {
            databaseReference.child("Chats").child(uuidString).child("userNickName").setValue(nickName);
        }

        // burada yazilan mesajlari zamanladik asagida get data icerisinde de query yaptik ve zamana gore diz dedik!!!
        databaseReference.child("Chats").child(uuidString).child("userMessageTime").setValue(ServerValue.TIMESTAMP);
        messageText.setText("");

        getData();

    }

    // datayi internetten cekmek icin bu kodlari kullanacagiz
    public void getData() {
        // firebase icerisinde acitigimiz chats objesini yazmamiz gerekmektedir!!
        DatabaseReference newReference = database.getReference("Chats");

        Query query = newReference.orderByChild("userMessageTime");


        // en detayli childler icerigine ulasmak icin valuelistener kullaniriz!
        // ayni zamanda en detay oldugu icin istersek eger childlara da ulasabiliriz
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                System.out.println("dataSnapshot Children: "+snapshot.getChildren());
//                System.out.println("dataSnapshot Value: "+snapshot.getValue());
//                System.out.println("dataSnapshot Key: "+snapshot.getKey());

                chatMessages.clear();
                // bunu yapmazsak eger mesaj yazdigimizda her seferinde eski mesajlari tekrar tekrar ekler!


                for (DataSnapshot ds : snapshot.getChildren()) {
                    // System.out.println("data value: "+ds.getValue());


                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String userNickName = hashMap.get("userNickName");
                    String userMessage = hashMap.get("userMessage");

                    chatMessages.add(userNickName + ": " + userMessage);
                    // yeni bir sey yukledim orayi guncelle


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // database bir hata verirse ve ya veriye ulasilamazsa burasi,
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();

            }
        });

    }
}