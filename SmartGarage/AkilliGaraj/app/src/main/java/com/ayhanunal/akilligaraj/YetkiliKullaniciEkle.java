package com.ayhanunal.akilligaraj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class YetkiliKullaniciEkle extends AppCompatActivity {

    TextView emailText;
    TextView sifreText;
    TextView adYetText;
    TextView parkYetText;
    ImageView kullaniciResimYet;

    Uri resimVerisi;
    Bitmap seciliResim;


    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yetkili_kullanici_ekle);

        emailText = findViewById(R.id.emailYetText);
        sifreText = findViewById(R.id.sifreYetText);
        adYetText = findViewById(R.id.adYetText);
        parkYetText = findViewById(R.id.parkYerText);
        kullaniciResimYet = findViewById(R.id.kullaniciResimYet);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance(); //objeyi baslattık.
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();




    }

    public void kaydet(View view){

        final String email = emailText.getText().toString();
        final String sifre = sifreText.getText().toString();
        final String adSoyad = adYetText.getText().toString();
        final String parkYeri = parkYetText.getText().toString();



        if(email.matches("") || sifre.matches("") || adSoyad.matches("") || parkYeri.matches("") || resimVerisi == null){

            Toast.makeText(YetkiliKullaniciEkle.this,"Alanları Boş Bırakmayınız veya Resim Seçiniz.",Toast.LENGTH_LONG).show();

        }else{

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Kaydet");
            alert.setMessage("Emin misin?");

            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    firebaseAuth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            UUID uuid = UUID.randomUUID();
                            final String resimAdi = "resimler/" + uuid + ".jpg";
                            storageReference.child(resimAdi).putFile(resimVerisi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    StorageReference yeniReferans = FirebaseStorage.getInstance().getReference(resimAdi);
                                    yeniReferans.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String resimUrl = uri.toString();

                                            HashMap<String,Object> kaydedilecekVeri = new HashMap<>();
                                            kaydedilecekVeri.put("kullaniciAdi",adSoyad);
                                            kaydedilecekVeri.put("parkYeri",parkYeri);
                                            kaydedilecekVeri.put("resimUrl",resimUrl);
                                            kaydedilecekVeri.put("kullaniciEmail",email);

                                            firebaseFirestore.collection("Kisiler").add(kaydedilecekVeri).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                    Toast.makeText(YetkiliKullaniciEkle.this,"Kullanıcı Başarıyla Kaydedildi",Toast.LENGTH_LONG).show();
                                                    firebaseAuth.signOut();
                                                    Intent intent = new Intent(YetkiliKullaniciEkle.this,YetkiliSayfa.class);
                                                    startActivity(intent);
                                                    finish();


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(YetkiliKullaniciEkle.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                                }
                                            });


                                        }
                                    });




                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(YetkiliKullaniciEkle.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                                }
                            });




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(YetkiliKullaniciEkle.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                        }
                    });

                }
            });

            alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Toast.makeText(getApplicationContext(),"Kaydedilmedi",Toast.LENGTH_LONG).show();
                    emailText.setText("");
                    sifreText.setText("");
                    adYetText.setText("");
                    parkYetText.setText("");
                    kullaniciResimYet.setImageBitmap(null);

                }
            });

            alert.show();

        }

    }


    public void resimSec(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }else {
            Intent galeriIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galeriIntent,2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galeriIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galeriIntent,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 2 && resultCode == RESULT_OK && data != null){

            resimVerisi = data.getData();

            try {

                if(Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),resimVerisi);
                    seciliResim = ImageDecoder.decodeBitmap(source);
                    kullaniciResimYet.setImageBitmap(seciliResim);
                }else{
                    seciliResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resimVerisi);
                    kullaniciResimYet.setImageBitmap(seciliResim);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        super.onActivityResult(requestCode, resultCode, data);


    }
}
