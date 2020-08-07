package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinwifly.Common.Common;
import com.example.pinwifly.Model.User;
import com.facebook.internal.WebDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.raycoarana.codeinputview.CodeInputView;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class VerifyCode extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    CodeInputView codigoinput;
    String telefono,codigo,codigo_verificacion;
    TextView telefonotv;
    Button botonverificar;
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
        setContentView(R.layout.activity_verify_code);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if(getIntent() != null){
            telefono = getIntent().getStringExtra("telefono");
            codigo = getIntent().getStringExtra("codigo");
            codigo_verificacion = getIntent().getStringExtra("sms");
        }


        telefonotv = (TextView)findViewById(R.id.telefonotv);
        codigoinput = (CodeInputView)findViewById(R.id.codigoinput);
        botonverificar = (Button)findViewById(R.id.botonverificar);

        telefonotv.setText("+"+codigo+telefono);

        botonverificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = codigoinput.getCode();
                verificartelefono(codigo_verificacion,codigo);
            }
        });

        //

    }


    private void verificartelefono(String codigo_verificacion, String codigotv) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codigo_verificacion,codigotv);
        if(user==null){
            signInWithPhoneAuthCredential(credential);
        }else if(user.getDisplayName() == null || user.getDisplayName().equals("")){
            signInWithPhoneAuthCredential(credential);
        }else{
            AddPhoneAccount(credential,user);
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            if(user.getDisplayName() == null || user.getDisplayName().equals("")){
                                Intent intent = new Intent(VerifyCode.this, RegisterActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                finish();
                            }


                            // ...
                        } else {
                            Toast.makeText(VerifyCode.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Snackbar.make(findViewById(android.R.id.content), "Invalido",
                                        Snackbar.LENGTH_SHORT).show();
                                codigoinput.setError("Código Invalido");
                                codigoinput.setCode("");
                                codigoinput.setEditable(true);
                            }
                        }
                    }
                });
    }



    private void AddPhoneAccount(PhoneAuthCredential credential,FirebaseUser user) {
        user.updatePhoneNumber(credential).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(), "Tu número verificado: " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                    User current = new User(user.getDisplayName(),user.getEmail(),null,user.getPhoneNumber(),user.getPhotoUrl().toString());
                    Common.currentUser = current;
                    finish();
                }else{
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        //mVerificationField.setError("Invalid code.");
                        Snackbar.make(findViewById(android.R.id.content), "Invalido",
                                Snackbar.LENGTH_SHORT).show();
                        codigoinput.setError("Código Invalido");
                        codigoinput.setCode("");
                        codigoinput.setEditable(true);

                    }else {
                        Snackbar.make(findViewById(android.R.id.content), "signInWithCredential:failure"+task.getException(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
