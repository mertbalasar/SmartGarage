package com.ayhanunal.akilligaraj;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class YetkiliGiris extends AppCompatActivity {

    TextView kulAdText;
    TextView sifreText;

    FirebaseFirestore firebaseFirestore;

    ArrayList<String> yetKulAdlar;
    ArrayList<String> yetSifreler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yetkili_giris);

        kulAdText = findViewById(R.id.kulAdText);
        sifreText = findViewById(R.id.sifreYetText);

        firebaseFirestore = FirebaseFirestore.getInstance();

        yetKulAdlar = new ArrayList<>();
        yetSifreler = new ArrayList<>();

        yetkiliAl();





    }
    public void giris(View view){

        String kulAd = kulAdText.getText().toString();
        String sifre = sifreText.getText().toString();

        if(kulAd.matches("") || sifre.matches("")){
            Toast.makeText(YetkiliGiris.this,"Alanları Boş Bırakmaynız !!",Toast.LENGTH_LONG).show();
        }else{
            if (yetKulAdlar.indexOf(kulAd) != -1 && yetSifreler.indexOf(sifre) != -1){
                Intent intent = new Intent(YetkiliGiris.this, YetkiliSayfa.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(YetkiliGiris.this,"Yetkiniz Yok!!",Toast.LENGTH_LONG).show();
                kulAdText.setText("");
                sifreText.setText("");
            }
        }






    }

    public void yetkiliAl(){

        CollectionReference collectionReference = firebaseFirestore.collection("Yetkili");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Toast.makeText(YetkiliGiris.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(queryDocumentSnapshots != null){

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> gelenVeri = snapshot.getData();

                        String gelenAd = (String) gelenVeri.get("kulAdi");
                        String gelenSifre = (String) gelenVeri.get("sifre");

                        yetKulAdlar.add(gelenAd);
                        yetSifreler.add(gelenSifre);
                    }

                }

            }
        });

    }
}
