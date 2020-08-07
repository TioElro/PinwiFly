package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText ettelefono,etname,etcorreo,etpass;
    FButton botonregistro;

    private FirebaseAuth mAuth;

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

        setContentView(R.layout.activity_register);

        etname = (MaterialEditText)findViewById(R.id.edittextname);
        etcorreo= (MaterialEditText)findViewById(R.id.edittextemail);
        etpass = (MaterialEditText)findViewById(R.id.edittextcontra);
        botonregistro = (FButton)findViewById(R.id.botonregistro);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tabla = database.getReference("User");

        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                mDialog.setMessage("Espere porfavor...");
                mDialog.show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(etname.getText().toString())
                        .setPhotoUri(Uri.parse("http://pluspng.com/img-png/user-png-icon-download-icons-logos-emojis-users-2240.png"))
                        .build();

                user.updateProfile(profileUpdates);
                user.updateEmail(etcorreo.getText().toString());

                user.updatePassword(etpass.getText().toString());


                user.reload()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    User current = new User(etname.getText().toString(),etcorreo.getText().toString(),etpass.getText().toString(),user.getPhoneNumber(),"http://pluspng.com/img-png/user-png-icon-download-icons-logos-emojis-users-2240.png");
                                    Common.currentUser = current;
                                    mDialog.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this,Home.class);
                                    startActivity(intent);
                                }else{

                                    Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        });

            }
        });
    }
}
