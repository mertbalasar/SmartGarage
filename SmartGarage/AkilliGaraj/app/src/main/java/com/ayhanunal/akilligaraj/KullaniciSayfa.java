package com.ayhanunal.akilligaraj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KullaniciSayfa extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    String icerdeki_email;

    TextView lambaDurumText;
    TextView kapiDurumText;
    TextView kullaniciText;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference kapiDB = database.getReference("kapi");
    DatabaseReference lambaDB = database.getReference("lamba");

    String okunanKapi;
    String okunanLamba;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.kullanici_sayfa_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.bilgi_duzenle){

            Intent bilgiGor = new Intent(KullaniciSayfa.this, KullaniciBilgiGoruntule.class);
            bilgiGor.putExtra("email",icerdeki_email);
            startActivity(bilgiGor);

        }else if (item.getItemId() == R.id.cikis_yap){

            firebaseAuth.signOut();
            Intent giriseYonlendir = new Intent(KullaniciSayfa.this,MainActivity.class);
            startActivity(giriseYonlendir);

        }else if (item.getItemId() == R.id.sifre_sifirla){

            firebaseAuth.sendPasswordResetEmail(icerdeki_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(getApplicationContext(),"Mail Adresinize Sıfırlama Mesajı Gönderildi",Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),"islem basarısız! yetkiliyle görüsün",Toast.LENGTH_LONG).show();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_sayfa);

        lambaDurumText = findViewById(R.id.lambaDurumText);
        kapiDurumText = findViewById(R.id.kapiDurumText);
        kullaniciText = findViewById(R.id.kullaniciText);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent giristenGelenKullanici = getIntent();
        icerdeki_email = giristenGelenKullanici.getStringExtra("icerdeki_email");
        kullaniciText.setText(icerdeki_email);


        //veritabanından kapı verisini okuyoruz.
        kapiDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    okunanKapi = dataSnapshot.getValue().toString();

                    System.out.println("OKUNAN KAPI"+okunanKapi);


                    if(okunanKapi.matches("1")){
                        kapiDurumText.setTextColor(Color.GREEN);
                        kapiDurumText.setText(": Açık");
                    }else{
                        kapiDurumText.setTextColor(Color.RED);
                        kapiDurumText.setText(": Kapalı");
                    }
                }catch (Exception e){
                    System.out.println(e);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),"Veritabani Baglanti Hatasi",Toast.LENGTH_LONG).show();

            }
        });

        //veritabanından lamba verisini okuyoruz.
        lambaDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    okunanLamba = dataSnapshot.getValue().toString();
                    System.out.println("OKUNAN LAMBA"+okunanLamba);

                    if(okunanLamba.matches("1")){
                        lambaDurumText.setTextColor(Color.GREEN);
                        lambaDurumText.setText(": Açık");
                    }else{
                        lambaDurumText.setTextColor(Color.RED);
                        lambaDurumText.setText(": Kapalı");
                    }
                }catch (Exception e){
                    System.out.println(e);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),"Veritabani Baglanti Hatasi",Toast.LENGTH_LONG).show();

            }
        });







    }

    public void kapiDegis(View view){

        try {
            if(okunanKapi.matches("1")){

                kapiDB.setValue("0");

            }else{

                kapiDB.setValue("1");

            }
        }catch (Exception e){
            System.out.println(e);
        }



    }

    public void lambaDegis(View view){

        try {
            if(okunanLamba.matches("1")){

                lambaDB.setValue("0");

            }else{

                lambaDB.setValue("1");

            }
        } catch (Exception e){
            System.out.println(e);
        }



    }
}
