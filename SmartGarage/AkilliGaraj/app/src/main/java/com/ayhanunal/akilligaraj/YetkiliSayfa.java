package com.ayhanunal.akilligaraj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class YetkiliSayfa extends AppCompatActivity {

    Intent intent;

    private FirebaseFirestore firebaseFirestore;

    ArrayList<String> kullaniciAdlari;
    ArrayList<String> kullaniciParkYerleri;
    ArrayList<String> kullaniciEmailleri;
    ArrayList<String> kullaniciResimUrlleri;

    YetkiliRecycleAdapter yetkiliRecycleAdapter;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.yetkili_sayfa_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.yet_sayfa_tum_kullaniciler){
            intent = new Intent(YetkiliSayfa.this, YetkiliSayfa.class);
            startActivity(intent);
            finish();

        }else if(item.getItemId() == R.id.yet_sayfa_kullanici_kaydet){
            intent = new Intent(YetkiliSayfa.this, YetkiliKullaniciEkle.class);
            startActivity(intent);

        }else if (item.getItemId() == R.id.yet_sayfa_cikis){
            intent = new Intent(YetkiliSayfa.this, YetkiliGiris.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yetkili_sayfa);

        firebaseFirestore = FirebaseFirestore.getInstance();

        kullaniciAdlari = new ArrayList<>();
        kullaniciEmailleri = new ArrayList<>();
        kullaniciParkYerleri = new ArrayList<>();
        kullaniciResimUrlleri = new ArrayList<>();





        verileriOku();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        yetkiliRecycleAdapter = new YetkiliRecycleAdapter(kullaniciEmailleri,kullaniciAdlari,kullaniciParkYerleri,kullaniciResimUrlleri);

        recyclerView.setAdapter(yetkiliRecycleAdapter);




    }

    public void verileriOku(){

        CollectionReference collectionReference = firebaseFirestore.collection("Kisiler");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null){
                    Toast.makeText(YetkiliSayfa.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if(queryDocumentSnapshots != null){

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> gelenVeri = snapshot.getData();

                        String gelenAd = (String) gelenVeri.get("kullaniciAdi");
                        String gelenEmail = (String) gelenVeri.get("kullaniciEmail");
                        String gelenParkYeri = (String) gelenVeri.get("parkYeri");
                        String gelenResimUrl = (String) gelenVeri.get("resimUrl");

                        if(kullaniciAdlari.indexOf(gelenAd) == -1){
                            kullaniciAdlari.add(gelenAd);
                            kullaniciEmailleri.add(gelenEmail);
                            kullaniciParkYerleri.add(gelenParkYeri);
                            kullaniciResimUrlleri.add(gelenResimUrl);
                        }
                        yetkiliRecycleAdapter.notifyDataSetChanged();


                    }

                }

            }
        });

    }


}
