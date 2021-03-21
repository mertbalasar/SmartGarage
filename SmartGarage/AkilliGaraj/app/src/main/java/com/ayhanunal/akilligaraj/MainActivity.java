package com.ayhanunal.akilligaraj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView sifreText;
    TextView emailText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sifreText = findViewById(R.id.sifreYetText);
        emailText = findViewById(R.id.emailYetText);

        firebaseAuth = FirebaseAuth.getInstance();


        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); //içeri giriş yapmış biri varsa
        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this,KullaniciSayfa.class);
            intent.putExtra("icerdeki_email",firebaseUser.getEmail().toString());
            startActivity(intent);
            finish();
        }


    }


    public void yetGiris(View view){

        Intent intent = new Intent(MainActivity.this,YetkiliGiris.class);
        startActivity(intent);


    }

    public void kulGiris(View view){
        final String kulEmail = emailText.getText().toString();
        String kulSifre = sifreText.getText().toString();

        if(kulEmail.matches("") || kulSifre.matches("")){

            Toast.makeText(MainActivity.this,"Alanarı Boş Bırakmayınız!",Toast.LENGTH_LONG).show();

        }else {
            firebaseAuth.signInWithEmailAndPassword(kulEmail,kulSifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent = new Intent(MainActivity.this,KullaniciSayfa.class);
                    intent.putExtra("icerdeki_email",kulEmail);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MainActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                    emailText.setText("");
                    sifreText.setText("");

                }
            });
        }





    }

}
