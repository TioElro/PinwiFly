package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pinwifly.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText ettelefono,etname,etcorreo,etpass;
    FButton botonregistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ettelefono = (MaterialEditText)findViewById(R.id.edittextphone);
        etname = (MaterialEditText)findViewById(R.id.edittextname);
        etcorreo= (MaterialEditText)findViewById(R.id.edittextemail);
        etpass = (MaterialEditText)findViewById(R.id.edittextpass);
        botonregistro = (FButton)findViewById(R.id.botonregistro);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tabla = database.getReference("User");

        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                mDialog.setMessage("Espere porfavor...");
                mDialog.show();

                tabla.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(ettelefono.getText().toString()).exists()) {
                            mDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Numero ya registrado!", Toast.LENGTH_SHORT).show();
                        }else{
                            mDialog.dismiss();
                            User user = new User(etname.getText().toString(),etcorreo.getText().toString(),etpass.getText().toString());
                            tabla.child(ettelefono.getText().toString()).setValue(user);
                            Toast.makeText(RegisterActivity.this, "Registro correcto!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
