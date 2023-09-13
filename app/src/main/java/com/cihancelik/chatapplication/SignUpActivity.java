package com.cihancelik.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class SignUpActivity extends AppCompatActivity {
    EditText emailText, passwordText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailText = findViewById(R.id.user_email_edit_text);
        passwordText = findViewById(R.id.user_password_edit_text);

        mAuth = FirebaseAuth.getInstance();
        // firebase sinifindan yeni bir obje olusturdugumuzu gosterir

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
            startActivity(intent);
        }


    }

    public void signUp(View view) {
        mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            String userEmail = user.getEmail().toString();
//                            System.out.println("user email: "+userEmail);
                            // intent
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(SignUpActivity.this, "Boyle bir kullanici mevcut", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    public void signIn(View view) {

        mAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(SignUpActivity.this, "Sifre ve ya kullanici adi yanlis!!!", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

}