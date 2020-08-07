package com.example.pinwifly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class VerifyNumber extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codigo_verificacion;
    CountryCodePicker ccp;
    EditText telefono;
    Button botonsolicitarcodigo;
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

        setContentView(R.layout.activity_verify_number);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        telefono = (EditText)findViewById(R.id.edittexttelefono);
        botonsolicitarcodigo = (Button)findViewById(R.id.botonverificar);


        mFirebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
               // Toast.makeText(getApplicationContext(),"Código enviado al telefono",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codigo_verificacion = s;
                Log.d("testing", "onCodeSent: " + codigo_verificacion);
                Toast.makeText(getApplicationContext(),"Código enviado al telefono",Toast.LENGTH_SHORT).show();
                Intent coding = new Intent(VerifyNumber.this,VerifyCode.class);
                coding.putExtra("sms",codigo_verificacion);
                coding.putExtra("codigo",ccp.getSelectedCountryCode());
                coding.putExtra("telefono",telefono.getText().toString());
                startActivity(coding);
                finish();
            }
        };


        botonsolicitarcodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandarmsj();
            }
        });

    }

    public void mandarmsj(){
        String codigo = ccp.getSelectedCountryCodeWithPlus();
        String numero = telefono.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(codigo+numero,60, TimeUnit.SECONDS,this,mCallbacks);

    }

}
