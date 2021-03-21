package com.ayhanunal.akilligaraj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class KullaniciBilgiGoruntule extends AppCompatActivity {

    ImageView kulBilGorImage;
    TextView kulBilGorAd;
    TextView kulBilGorEmail;
    TextView kulBilGorYer;

    String email;
    String parkYer;
    String ad;
    String resimUrl;



    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_bilgi_goruntule);

        kulBilGorAd = findViewById(R.id.kulBilGorAd);
        kulBilGorImage = findViewById(R.id.kulBilGorImage);
        kulBilGorEmail = findViewById(R.id.kulBilGorEmail);
        kulBilGorYer = findViewById(R.id.kulBilGorYer);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        firebaseFirestore = FirebaseFirestore.getInstance();

        kisiVeriOku();


    }

    public void kisiVeriOku(){

        CollectionReference collectionReference = firebaseFirestore.collection("Kisiler");
        collectionReference.whereEqualTo("kullaniciEmail",email).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Toast.makeText(KullaniciBilgiGoruntule.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(queryDocumentSnapshots != null){

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> gelenVeri = snapshot.getData();

                        ad = (String) gelenVeri.get("kullaniciAdi");
                        parkYer = (String) gelenVeri.get("parkYeri");
                        resimUrl = (String) gelenVeri.get("resimUrl");

                        kulBilGorAd.setText("Ad Soyad : "+ad);
                        kulBilGorEmail.setText("Email : "+email);
                        kulBilGorYer.setText("Park Yeri : "+parkYer);

                        Picasso.get().load(resimUrl).into(kulBilGorImage);

                    }

                }

            }
        });

    }
}
