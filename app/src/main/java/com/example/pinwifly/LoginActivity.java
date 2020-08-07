package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginActivity extends AppCompatActivity {
    EditText ettelefono,etpass;
    Button botonentrar;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/fuente.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.activity_login);

        ettelefono = (MaterialEditText) findViewById(R.id.edittextphone);
        etpass=(MaterialEditText)findViewById(R.id.edittextpass);
        botonentrar= (Button)findViewById(R.id.botoniniciar);

        //Iniciamos servicio FIREBASE
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        botonentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("Espere porfavor...");
                mDialog.show();

                mDatabaseReference.child("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Revisamos si el usuario no existe en la base de datos
                        if(dataSnapshot.child(ettelefono.getText().toString()).exists()) {
                            //Obtenemos informacion del usuario
                            mDialog.dismiss();
                            User usuario = dataSnapshot.child(ettelefono.getText().toString()).getValue(User.class);
                            usuario.setPhone(ettelefono.getText().toString()); //Colocamos phone

                            if(!Boolean.parseBoolean(usuario.getRepartidor())){
                                if(usuario.getPassword().equals(etpass.getText().toString())){
                                    Intent Home = new Intent(LoginActivity.this,Home.class);
                                    Common.currentUser = usuario;
                                    startActivity(Home);
                                    finish();
                                }else{
                                    Toast.makeText(LoginActivity.this,"Contraseña incorrecta",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(LoginActivity.this,"Esa cuenta es de repartidor",Toast.LENGTH_SHORT).show();
                            }



                            /*if (usuario.getPassword().equals(etpass.getText().toString())) {
                                Intent login = new Intent(LoginActivity.this,Home.class);
                                Common.currentUser = usuario;
                                startActivity(login);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Incorrecto!", Toast.LENGTH_SHORT).show();
                            }*/


                        }else{
                            mDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Usuario no existe en la base de datos!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
