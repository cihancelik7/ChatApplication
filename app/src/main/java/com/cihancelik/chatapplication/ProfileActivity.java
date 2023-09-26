package com.cihancelik.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    EditText ageText;
    EditText nickName;
    EditText mailText;
    ImageView userImageView;
    Uri selected;

    // For upload..
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nickName = findViewById(R.id.nickNameText);
        ageText = findViewById(R.id.ageText);
        mailText = findViewById(R.id.emailText);
        userImageView = findViewById(R.id.userImageTextView);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        getData();

    }

    public void getData() {
        DatabaseReference newReference = database.getReference("Profiles");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String userEmail = hashMap.get("userEmail");

                    // Kullanıcının oturum açtığı e-posta ile veriyi karşılaştırın
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.getEmail() != null && user.getEmail().equals(userEmail)) {
                        String userAge = hashMap.get("userAge");
                        String userNickName = hashMap.get("userNickName");
                        String userImageURL = hashMap.get("userImageUrl");

                        // Data'dan alınan bilgileri görüntüleme öğelerine yerleştirin
                        ageText.setText(userAge);
                        nickName.setText(userNickName);
                        mailText.setText(userEmail);

                        // Picasso kullanarak resmi yükleme
                        if (userImageURL != null && !userImageURL.isEmpty()) {
                            Picasso.get().load(userImageURL).into(userImageView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void upload(View view) {

        final UUID uuidImage = UUID.randomUUID();
        // UUID yaptigimizda bize her seferinde rastgele bir id vermis olur˜˜˜

        // image upload
        String imageName = "images/" + uuidImage + "jpg";
        StorageReference newReference = storageReference.child(imageName);
        // burada bu upload islemi basarili olup olmadigini goruruz˜˜˜
        newReference.putFile(selected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // hata alinmazsa yapilmasi gerekenler asagida yaziyor!!!!

                // metadatanin icinde bizim arayacagimiz bazi farkli farkli veriler var
                // orn upload ettikten sonra bir data ariyoruz o da url, hangi adtrese upload edildi, o adresi almak istiyorum
                // upload yptigim dosyaya sonradan ulasabileyim!!!!!!!
                // NEREYE UPLOAD EDILDIGINI OGRENMEK ICIN
                StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("images/" + uuidImage + "jpg");

                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadURL = uri.toString();
                        //   System.out.println("download url:"+downloadURL);

                        UUID uuid = UUID.randomUUID();
                        String uuidString = uuid.toString();

                        String userNick = nickName.getText().toString();
                        String userAge = ageText.getText().toString();

                        FirebaseUser user = mAuth.getCurrentUser();

                        String userEmail = user.getEmail().toString();

                        // veritabanina kaydettigimiz kodlar burada yaziyor
                        databaseReference.child("Profiles").child(uuidString).child("userImageUrl").setValue(downloadURL);
                        databaseReference.child("Profiles").child(uuidString).child("userNickName").setValue(userNick);
                        databaseReference.child("Profiles").child(uuidString).child("userEmail").setValue(userEmail);
                        databaseReference.child("Profiles").child(uuidString).child("userAge").setValue(userAge);


                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_LONG).show();

                        // Chat activity e geri donmek icin burada intent kullanacagiz!!!1
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        startActivity(intent);


                    }
                });


            }
            // burada bana neden failure oldugunu exceptiion olarak gosterebulir, orn int kopru authenticvaoin eksik veya yeniden gitis gerekli demek
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void selectPicture(View view) {
        // sdk 23 ve alti icin appcompat kullaniyoruz!!!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }


    // media galeriye gitmek icin bu fonksiyonu kullaniriz
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // sonuclari da burada aliriz...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            selected = data.getData();

            // mediadaki fotografi image dosyasi yapmak icin kullaniriz..
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected);
                userImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}